package de.oliver.fancynpcs.utils;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.utils.SkinCache;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SkinCacheYaml implements SkinCache {

    private final static File file = new File("plugins/FancyNpcs/.skinCache.yml");

    private static YamlConfiguration loadYaml() {
        if (!file.exists()) {
            return null;
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public List<SkinFetcher.SkinCacheData> load() {
        YamlConfiguration yaml = loadYaml();
        if (yaml == null) {
            return new ArrayList<>(0);
        }

        ConfigurationSection skinsSection = yaml.getConfigurationSection("skins");

        if (skinsSection == null) {
            return new ArrayList<>(0);
        }

        List<SkinFetcher.SkinCacheData> cache = new ArrayList<>();

        for (String identifierBase64 : skinsSection.getKeys(false)) {
            ConfigurationSection skinSection = skinsSection.getConfigurationSection(identifierBase64);
            if (skinSection == null) {
                continue;
            }

            String identifier = new String(Base64.getDecoder().decode(identifierBase64));

            String value = skinSection.getString("value");
            String signature = skinSection.getString("signature");
            if (value == null || signature == null) {
                continue;
            }

            SkinFetcher.SkinData skinData = new SkinFetcher.SkinData(identifier, value, signature);

            long lastUpdated = skinSection.getLong("lastUpdated");
            long timeToLive = skinSection.getLong("timeToLive");

            SkinFetcher.SkinCacheData skinCacheData = new SkinFetcher.SkinCacheData(skinData, lastUpdated, timeToLive);
            if (skinCacheData.isExpired()) {
                delete(identifier);
                continue;
            }

            cache.add(skinCacheData);
        }


        return cache;
    }

    @Override
    public void upsert(SkinFetcher.SkinCacheData skinCacheData, boolean onlyIfExists) {
        YamlConfiguration yaml = loadYaml();
        if (yaml == null) {
            yaml = new YamlConfiguration();
        }

        ConfigurationSection skinsSection = yaml.getConfigurationSection("skins");
        if (skinsSection == null) {
            skinsSection = yaml.createSection("skins");
        }

        String identifier = Base64.getEncoder().encodeToString(skinCacheData.skinData().identifier().getBytes());

        ConfigurationSection skinSection = skinsSection.getConfigurationSection(identifier);
        if (skinSection == null) {
            if (onlyIfExists) {
                return; // only update if it already exists
            }

            skinSection = skinsSection.createSection(identifier);
        }

        skinSection.set("identifier", skinCacheData.skinData().identifier());
        skinSection.set("value", skinCacheData.skinData().value());
        skinSection.set("signature", skinCacheData.skinData().signature());
        skinSection.set("lastUpdated", System.currentTimeMillis());
        skinSection.set("timeToLive", skinCacheData.timeToLive());

        try {
            yaml.save(file);
        } catch (Exception e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to save skin cache");
        }
    }

    public void delete(String identifier) {
        YamlConfiguration yaml = loadYaml();
        if (yaml == null) {
            return;
        }

        ConfigurationSection skinsSection = yaml.getConfigurationSection("skins");
        if (skinsSection == null) {
            return;
        }

        skinsSection.set(identifier, null);
    }

    public void loadAndInsertToSkinFetcher() {
        List<SkinFetcher.SkinCacheData> cache = load();
        for (SkinFetcher.SkinCacheData skinCacheData : cache) {
            SkinFetcher.skinCache.put(skinCacheData.skinData().identifier(), skinCacheData.skinData());
        }
    }
}
