package de.oliver.fancynpcs.converters.citizens;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import de.oliver.fancynpcs.converters.Converter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CitizensConverter extends Converter {

    private final static String STORAGE_PATH = "plugins/Citizens/saves.yml";

    public CitizensConverter() {
        super("Citizens");
    }

    @Override
    public Npc convertOne(String npcName) {
        return null;
    }

    @Override
    public List<Npc> convertAll() {
        File storage = new File(STORAGE_PATH);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(storage);

        if (!config.isConfigurationSection("npc")) {
            return new ArrayList<>();
        }

        List<Npc> npcs = new ArrayList<>();

        ConfigurationSection npcSection = config.getConfigurationSection("npc");
        for (String npcIndex : npcSection.getKeys(false)) {
            Npc npc = convertFromConfig(npcSection.getConfigurationSection(npcIndex));

            if (npc != null) {
                npcs.add(npc);
            }
        }

        return npcs;
    }

    private Npc convertFromConfig(ConfigurationSection config) {
        String name = config.getString("name", "N/A");
        String owner = config.getString("traits.owner.uuid", "");

        String world = config.getString("traits.location.world", "world");
        double x = Double.parseDouble(config.getString("traits.location.x", "0.0"));
        double y = Double.parseDouble(config.getString("traits.location.y", "1000.0"));
        double z = Double.parseDouble(config.getString("traits.location.z", "0.0"));
        float yaw = Float.parseFloat(config.getString("traits.location.yaw", "0.0"));
        float pitch = Float.parseFloat(config.getString("traits.location.pitch", "0.0"));
        Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);

        NpcData data = new NpcData(name, owner.isEmpty() ? null : UUID.fromString(owner), location);

        EntityType entityType = EntityType.valueOf(config.getString("traits.type", "PLAYER").toUpperCase());
        data.setType(entityType);

        switch (entityType) {
            case PLAYER -> {
                if (config.isConfigurationSection("traits.skintrait")) {
                    String signature = config.getString("traits.skintrait.signature");
                    String texture = config.getString("traits.skintrait.textureRaw");
                    SkinFetcher skin = new SkinFetcher(UUID.randomUUID().toString(), texture, signature);
                    data.setSkin(skin);
                }
            }
        }


        return FancyNpcs.getInstance().getNpcAdapter().apply(data);
    }
}
