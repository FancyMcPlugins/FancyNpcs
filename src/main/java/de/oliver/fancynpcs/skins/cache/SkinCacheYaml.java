package de.oliver.fancynpcs.skins.cache;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;
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
    public List<SkinCacheData> load() {
        YamlConfiguration yaml = loadYaml();
        if (yaml == null) {
            return new ArrayList<>(0);
        }

        ConfigurationSection skinsSection = yaml.getConfigurationSection("skins");

        if (skinsSection == null) {
            return new ArrayList<>(0);
        }

        List<SkinCacheData> cache = new ArrayList<>();

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

            SkinData skinData = new SkinData(identifier, SkinData.SkinType.USERNAME, SkinData.SkinVariant.DEFAULT, value, signature); // TODO

            long lastUpdated = skinSection.getLong("lastUpdated");
            long timeToLive = skinSection.getLong("timeToLive");

            SkinCacheData skinCacheData = new SkinCacheData(skinData, lastUpdated, timeToLive);
            if (skinCacheData.isExpired()) {
                delete(identifier);
                continue;
            }

            cache.add(skinCacheData);
        }


        return cache;
    }

    @Override
    public void upsert(SkinCacheData skinData, boolean onlyIfExists) {
        YamlConfiguration yaml = loadYaml();
        if (yaml == null) {
            yaml = new YamlConfiguration();
        }

        ConfigurationSection skinsSection = yaml.getConfigurationSection("skins");
        if (skinsSection == null) {
            skinsSection = yaml.createSection("skins");
        }

        String identifier = Base64.getEncoder().encodeToString(skinData.skinData().identifier().getBytes());

        ConfigurationSection skinSection = skinsSection.getConfigurationSection(identifier);
        if (skinSection == null) {
            if (onlyIfExists) {
                return; // only update if it already exists
            }

            skinSection = skinsSection.createSection(identifier);
        }

        skinSection.set("identifier", skinData.skinData().identifier());
        skinSection.set("value", skinData.skinData().textureValue());
        skinSection.set("signature", skinData.skinData().textureSignature());
        skinSection.set("lastUpdated", System.currentTimeMillis());
        skinSection.set("timeToLive", skinData.timeToLive());

        try {
            yaml.save(file);
        } catch (Exception e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to save skin cache");
            FancyNpcs.getInstance().getFancyLogger().error(e);
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
}
