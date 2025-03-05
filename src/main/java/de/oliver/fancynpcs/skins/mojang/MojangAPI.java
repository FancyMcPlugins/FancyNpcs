package de.oliver.fancynpcs.skins.mojang;

import com.google.gson.Gson;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.skins.mineskin.RatelimitException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class MojangAPI {

    private final HttpClient client;
    private final Gson gson = new Gson();

    public MojangAPI() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(3, ChronoUnit.SECONDS))
                .build();
    }

    public SkinData fetchSkin(String uuid, SkinData.SkinVariant variant) throws RatelimitException {
        FancyNpcsPlugin.get().getFancyLogger().debug("Fetching skin from MojangAPI for " + uuid);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false"))
                    .GET()
                    .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                FancyNpcsPlugin.get().getFancyLogger().warn("Failed to fetch skin from Mojang API for " + uuid + " (status code: " + resp.statusCode() + ")");
                FancyNpcsPlugin.get().getFancyLogger().debug("Body: " + resp.body());
                return null;
            } else if (resp.statusCode() == 429) {
                throw new RatelimitException(System.currentTimeMillis() + 1000 * 10); // retry in next run
            }

            RequestResponse response = gson.fromJson(resp.body(), RequestResponse.class);
            RequestResponseProperty textures = response.getProperty("textures");

            FancyNpcsPlugin.get().getFancyLogger().debug("Skin fetched from MojangAPI for " + uuid);
            return new SkinData(uuid, variant, textures.value(), textures.signature());
        } catch (RatelimitException e) {
            throw e; // rethrow
        } catch (Exception e) {
            FancyNpcsPlugin.get().getFancyLogger().warn("Failed to fetch skin from Mojang API for " + uuid);
            FancyNpcsPlugin.get().getFancyLogger().warn(e);
            return null;
        }
    }

    record RequestResponse(String id, String name, RequestResponseProperty[] properties) {
        public RequestResponseProperty getProperty(String name) {
            for (RequestResponseProperty property : properties) {
                if (property.name.equals(name)) {
                    return property;
                }
            }
            return null;
        }
    }

    record RequestResponseProperty(String name, String value, String signature) {
    }

}
