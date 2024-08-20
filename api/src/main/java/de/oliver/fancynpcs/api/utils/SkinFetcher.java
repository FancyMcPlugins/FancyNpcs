package de.oliver.fancynpcs.api.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.chatcolorhandler.parsers.custom.PlaceholderAPIParser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class SkinFetcher {
    public static final Map<String, SkinData> skinCache = new ConcurrentHashMap<>(); // identifier -> skinData

    private SkinFetcher() {
    }

    /**
     * Fetches the skin data from the Mojang API asynchronously.
     *
     * @param identifier The identifier of the skin. This can be a UUID, username, URL or a placeholder by PAPI.
     * @return A CompletableFuture that will contain the SkinData.
     */
    public static CompletableFuture<SkinData> fetchSkin(String identifier) {
        return CompletableFuture.supplyAsync(() -> {
            String parsedIdentifier = ChatColorHandler.translate(identifier, List.of(PlaceholderAPIParser.class));

            if (skinCache.containsKey(parsedIdentifier)) {
                return skinCache.get(parsedIdentifier);
            }

            if (isURL(parsedIdentifier)) {
                return fetchSkinByURL(parsedIdentifier).join();
            }

            if (isUUID(parsedIdentifier)) {
                return fetchSkinByUUID(parsedIdentifier).join();
            }

            // assume it's a username
            UUID uuid = UUIDFetcher.getUUID(parsedIdentifier);
            if (uuid != null) {
                return fetchSkinByUUID(uuid.toString()).join();
            }

            return null;
        });
    }

    /**
     * Fetches the skin data from the Mojang API by UUID asynchronously.
     *
     * @param uuid The UUID of the player.
     * @return A CompletableFuture that will contain the SkinData.
     * @throws IOException If the skin data could not be fetched.
     */
    public static CompletableFuture<SkinData> fetchSkinByUUID(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                String json = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(json).getAsJsonObject();

                String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString();
                String signature = obj.getAsJsonArray("properties").get(0).getAsJsonObject().getAsJsonPrimitive("signature").getAsString();
                SkinData skinData = new SkinData(uuid, value, signature);

                skinCache.put(uuid, skinData);

                FancyNpcsPlugin.get().getSkinCache().upsert(new SkinCacheData(skinData, System.currentTimeMillis(), 1000L * 60 * 60 * 24 * 7)); //TODO: add some randomization

                return skinData;
            } catch (IOException e) {
                FancyNpcsPlugin.get().getPlugin().getLogger().warning("Failed to fetch skin data for UUID " + uuid);
                FancyNpcsPlugin.get().getPlugin().getLogger().warning(e.getMessage());
                return null;
            }
        });
    }

    /**
     * Fetches the skin data from the Mojang API by URL asynchronously.
     *
     * @param skinURL The URL of the skin.
     * @return A CompletableFuture that will contain the SkinData.
     * @throws IOException If the skin data could not be fetched.
     */
    public static CompletableFuture<SkinData> fetchSkinByURL(String skinURL) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://api.mineskin.org/generate/url");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.writeBytes("url=" + URLEncoder.encode(skinURL, StandardCharsets.UTF_8));
                outputStream.close();

                String json = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(json).getAsJsonObject();

                String value = obj.getAsJsonObject("data").getAsJsonObject("texture").getAsJsonPrimitive("value").getAsString();
                String signature = obj.getAsJsonObject("data").getAsJsonObject("texture").getAsJsonPrimitive("signature").getAsString();
                SkinData skinData = new SkinData(skinURL, value, signature);

                FancyNpcsPlugin.get().getSkinCache().upsert(new SkinCacheData(skinData, System.currentTimeMillis(), 1000L * 60 * 60 * 24 * 30 * 12));

                skinCache.put(skinURL, skinData);
                return skinData;
            } catch (IOException e) {
                FancyNpcsPlugin.get().getPlugin().getLogger().warning("Failed to fetch skin data for URL " + skinURL);
                FancyNpcsPlugin.get().getPlugin().getLogger().warning(e.getMessage());
                return null;
            }
        });
    }

    private static boolean isURL(String identifier) {
        return identifier.startsWith("http");
    }

    public static boolean isPlaceholder(String identifier) {
        return identifier.startsWith("%") && identifier.endsWith("%") || identifier.startsWith("{") && identifier.endsWith("}");
    }

    private static boolean isUUID(String identifier) {
        return identifier.length() == 36 && identifier.contains("-");
    }

    /**
     * Represents all required data for a skin.
     *
     * @param identifier The identifier of the skin. This can be a UUID, username, URL or a placeholder by PAPI.
     * @param value      The value of the skin. If {@code null}, the skin will be fetched from the Mojang API.
     * @param signature  The signature of the skin. If {@code null}, the skin will be fetched from the Mojang API.
     */
    public record SkinData(@NotNull String identifier, @Nullable String value, @Nullable String signature) {

        /**
         * Fetches the skin data from the Mojang API if the value or signature is {@code null}.
         *
         * @return The value of the skin or {@code null} if the skin data could not be fetched.
         */
        @Override
        public String value() {
            if (value == null || value.isEmpty()) {
                try {
                    SkinData skinData = fetchSkin(identifier).join();
                    return skinData == null ? null : skinData.value();
                } catch (Exception e) {
                    FancyNpcsPlugin.get().getPlugin().getLogger().warning("Failed to fetch skin data for " + identifier);
                }
            }

            return value;
        }

        /**
         * Fetches the skin data from the Mojang API if the value or signature is {@code null}.
         *
         * @return The signature of the skin or {@code null} if the skin data could not be fetched.
         */
        @Override
        public String signature() {
            if (signature == null || signature.isEmpty()) {
                try {
                    SkinData skinData = fetchSkin(identifier).join();
                    return skinData == null ? null : skinData.signature();
                } catch (Exception e) {
                    FancyNpcsPlugin.get().getPlugin().getLogger().warning("Failed to fetch skin data for " + identifier);
                }
            }

            return signature;
        }
    }

    /**
     * Represents the cached skin data. For internal use only.
     *
     * @param skinData    The skin data.
     * @param lastUpdated The timestamp when the skin data was last updated.
     * @param timeToLive  The time to live of the skin data in milliseconds.
     */
    @ApiStatus.Internal
    public record SkinCacheData(@NotNull SkinData skinData, long lastUpdated, long timeToLive) {
        public boolean isExpired() {
            return System.currentTimeMillis() - lastUpdated > timeToLive;
        }
    }
}
