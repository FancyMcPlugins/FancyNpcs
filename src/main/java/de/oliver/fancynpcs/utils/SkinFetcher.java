package de.oliver.fancynpcs.utils;


import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;

public class SkinFetcher {
    public static Map<String, SkinFetcher> skinCache = new HashMap<>();

    private final String uuid;
    private String name;
    private String value;
    private String signature;
    private boolean loaded;

    public SkinFetcher(String uuid) {
        this.uuid = uuid;

        if(skinCache.containsKey(uuid)){
            SkinFetcher cached = skinCache.get(uuid);
            this.name = cached.getName();
            this.value = cached.getValue();
            this.signature = cached.getSignature();
            this.loaded = true;
            return;
        }

        this.loaded = false;
        load();
    }

    public SkinFetcher(String uuid, String value, String signature){
        this.uuid = uuid;
        this.value = value;
        this.signature = signature;
        this.loaded = true;
    }


    public void load() {
        this.loaded = false;
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            uc.setDefaultUseCaches(false);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0");
            uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            uc.addRequestProperty("Pragma", "no-cache");

            String json = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray properties = (JSONArray) ((JSONObject) obj).get("properties");
            for (int i = 0; i < properties.size(); i++) {
                try {
                    JSONObject property = (JSONObject) properties.get(i);
                    String name = (String) property.get("name");
                    String value = (String) property.get("value");
                    String signature = property.containsKey("signature") ? (String) property.get("signature") : null;


                    this.name = name;
                    this.value = value;
                    this.signature = signature;
                    this.loaded = true;
                    skinCache.put(uuid, this);

                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to apply auth property", e);
                }
            }
        } catch (Exception e) {
            this.loaded = false;
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
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
}