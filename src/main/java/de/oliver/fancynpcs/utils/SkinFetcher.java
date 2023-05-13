package de.oliver.fancynpcs.utils;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;

public class SkinFetcher {
    public static Map<String, SkinFetcher> skinCache = new HashMap<>();

    private final SkinType skinType;
    private final String identifier; // uuid or url
    private String value;
    private String signature;
    private boolean loaded;

    public SkinFetcher(String identifier) {
        this.skinType = SkinType.getType(identifier);
        this.identifier = identifier;

        if(skinCache.containsKey(identifier)){
            SkinFetcher cached = skinCache.get(identifier);
            this.value = cached.getValue();
            this.signature = cached.getSignature();
            this.loaded = true;
            return;
        }

        this.loaded = false;
        load();
    }

    public SkinFetcher(String identifier, String value, String signature){
        this.skinType = SkinType.getType(identifier);
        this.identifier = identifier;
        this.value = value;
        this.signature = signature;
        this.loaded = true;
    }


    public void load() {
        this.loaded = false;
        try {
            URL url = new URL(skinType.getRequestUrl().replace("{uuid}", identifier));
            HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(skinType.getRequestMethod());
            if(skinType == SkinType.URL){
                conn.setDoOutput(true);
                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.writeBytes("url=" + URLEncoder.encode(identifier, "UTF-8"));
                outputStream.close();
            }

            String json = new Scanner(conn.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(json).getAsJsonObject();
            if(skinType == SkinType.UUID){
                this.value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString();
                this.signature = obj.getAsJsonArray("properties").get(0).getAsJsonObject().getAsJsonPrimitive("signature").getAsString();
            } else if(skinType == SkinType.URL){
                this.value = obj.getAsJsonObject("data").getAsJsonObject("texture").getAsJsonPrimitive("value").getAsString();
                this.signature = obj.getAsJsonObject("data").getAsJsonObject("texture").getAsJsonPrimitive("signature").getAsString();
            }
            this.loaded = true;
            skinCache.put(identifier, this);
        } catch (Exception e) {
            this.loaded = false;
        }
    }

    public SkinType getSkinType() {
        return skinType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public enum SkinType{
        UUID("https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false", "GET"),
        URL("https://api.mineskin.org/generate/url", "POST");

        private final String requestUrl;
        private final String requestMethod;

        SkinType(String requestUrl, String requestMethod) {
            this.requestUrl = requestUrl;
            this.requestMethod = requestMethod;
        }

        public String getRequestUrl() {
            return requestUrl;
        }

        public String getRequestMethod() {
            return requestMethod;
        }

        public static SkinType getType(String s){
            return s.startsWith("http") ? URL : UUID;
        }
    }
}