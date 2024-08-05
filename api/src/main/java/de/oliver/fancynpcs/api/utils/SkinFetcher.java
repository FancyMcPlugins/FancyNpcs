package de.oliver.fancynpcs.api.utils;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.chatcolorhandler.parsers.custom.PlaceholderAPIParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class SkinFetcher {
    public static Map<String, SkinData> skinCache = new HashMap<>(); // identifier -> skinData

    private SkinFetcher() {
    }

    /**
     * Fetches the skin data from the Mojang API.
     *
     * @param identifier The identifier of the skin. This can be a UUID, username, URL or a placeholder by PAPI.
     * @throws IOException If the skin data could not be fetched.
     */
    public static SkinData fetchSkin(String identifier) throws IOException {
        if (skinCache.containsKey(identifier)) {
            return skinCache.get(identifier);
        }

        if (isPlaceholder(identifier)) {
            String parsedIdentifier = ChatColorHandler.translate(identifier, List.of(PlaceholderAPIParser.class));
            return fetchSkin(parsedIdentifier);
        }

        if (isURL(identifier)) {
            return fetchSkinByURL(identifier);
        }

        if (isUUID(identifier)) {
            return fetchSkinByUUID(identifier);
        }

        // assume it's a username
        UUID uuid = UUIDFetcher.getUUID(identifier);
        if (uuid != null) {
            return fetchSkinByUUID(uuid.toString());
        }

        return null;
    }

    /**
     * Fetches the skin data from the Mojang API.
     *
     * @throws IOException If the skin data could not be fetched.
     */
    public static SkinData fetchSkinByUUID(String uuid) throws IOException {
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
        return skinData;
    }

    /**
     * Fetches the skin data from the Mojang API.
     *
     * @throws IOException If the skin data could not be fetched.
     */
    public static SkinData fetchSkinByURL(String skinURL) throws IOException {
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

        skinCache.put(skinURL, skinData);
        return skinData;
    }

    private static boolean isURL(String identifier) {
        return identifier.startsWith("http");
    }

    private static boolean isPlaceholder(String identifier) {
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
                    SkinData skinData = fetchSkin(identifier);
                    return skinData == null ? null : skinData.value();
                } catch (IOException e) {
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
                    SkinData skinData = fetchSkin(identifier);
                    return skinData == null ? null : skinData.signature();
                } catch (IOException e) {
                    FancyNpcsPlugin.get().getPlugin().getLogger().warning("Failed to fetch skin data for " + identifier);
                }
            }

            return signature;
        }
    }
}