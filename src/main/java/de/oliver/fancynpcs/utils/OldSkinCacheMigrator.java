package de.oliver.fancynpcs.utils;

import de.oliver.fancyanalytics.api.events.Event;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class OldSkinCacheMigrator {

    private static final File OLD_SKIN_CACHE = new File("plugins/FancyNPCs/.skinCache.yml");

    public static void migrate() {
        if (!OLD_SKIN_CACHE.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(OLD_SKIN_CACHE);
        ConfigurationSection skinsSection = config.getConfigurationSection("skins");
        if (skinsSection == null) {
            return;
        }

        int amount = 0;
        for (String key : skinsSection.getKeys(false)) {
            String id = skinsSection.getString(key + ".identifier");
            String value = skinsSection.getString(key + ".value");
            String signature = skinsSection.getString(key + ".signature");
            SkinData skinData = new SkinData(id, SkinData.SkinVariant.AUTO, value, signature);
            FancyNpcs.getInstance().getSkinManagerImpl().cacheSkin(skinData);
            amount++;
        }

        OLD_SKIN_CACHE.delete();

        FancyNpcs.getInstance().getLogger().info("Migrated " + amount + " skins from old cache.");
        FancyNpcs.getInstance().getFancyAnalytics().sendEvent(new Event("SkinCacheMigrated").withProperty("amount", String.valueOf(amount)));
    }

}
