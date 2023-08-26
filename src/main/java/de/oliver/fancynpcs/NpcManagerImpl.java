package de.oliver.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NpcManagerImpl implements NpcManager {

    private final JavaPlugin plugin;
    private final Function<NpcData, Npc> npcAdapter;
    private final File npcConfigFile;
    private final HashMap<String, Npc> npcs; // npc name -> npc

    public NpcManagerImpl(JavaPlugin plugin, Function<NpcData, Npc> npcAdapter) {
        this.plugin = plugin;
        this.npcAdapter = npcAdapter;
        npcs = new HashMap<>();
        npcConfigFile = new File("plugins/FancyNpcs/npcs.yml");
    }

    public void registerNpc(Npc npc) {
        npcs.put(npc.getData().getName(), npc);
    }

    public void removeNpc(Npc npc) {
        npcs.remove(npc.getData().getName());

        YamlConfiguration npcConfig = YamlConfiguration.loadConfiguration(npcConfigFile);
        npcConfig.set("npcs." + npc.getData().getName(), null);
        try {
            npcConfig.save(npcConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Npc getNpc(int entityId) {
        for (Npc npc : npcs.values()) {
            if (npc.getEntityId() == entityId) {
                return npc;
            }
        }

        return null;
    }

    public Npc getNpc(String name) {
        return npcs.getOrDefault(name, null);
    }

    public Collection<Npc> getAllNpcs() {
        return npcs.values();
    }

    public void saveNpcs(boolean force) {
        if (!npcConfigFile.exists()) {
            try {
                npcConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        YamlConfiguration npcConfig = YamlConfiguration.loadConfiguration(npcConfigFile);

        for (Npc npc : npcs.values()) {
            if (!npc.isSaveToFile()) {
                continue;
            }

            boolean shouldSave = force || npc.isDirty();
            if (!shouldSave) {
                continue;
            }

            NpcData data = npc.getData();

            npcConfig.set("npcs." + data.getName() + ".name", data.getName());
            npcConfig.set("npcs." + data.getName() + ".displayName", data.getDisplayName());
            npcConfig.set("npcs." + data.getName() + ".type", data.getType().name());
            npcConfig.set("npcs." + data.getName() + ".location.world", data.getLocation().getWorld().getName());
            npcConfig.set("npcs." + data.getName() + ".location.x", data.getLocation().getX());
            npcConfig.set("npcs." + data.getName() + ".location.y", data.getLocation().getY());
            npcConfig.set("npcs." + data.getName() + ".location.z", data.getLocation().getZ());
            npcConfig.set("npcs." + data.getName() + ".location.yaw", data.getLocation().getYaw());
            npcConfig.set("npcs." + data.getName() + ".location.pitch", data.getLocation().getPitch());
            npcConfig.set("npcs." + data.getName() + ".showInTab", data.isShowInTab());
            npcConfig.set("npcs." + data.getName() + ".spawnEntity", data.isSpawnEntity());
            npcConfig.set("npcs." + data.getName() + ".glowing", data.isGlowing());
            npcConfig.set("npcs." + data.getName() + ".glowingColor", data.getGlowingColor().toString());
            npcConfig.set("npcs." + data.getName() + ".turnToPlayer", data.isTurnToPlayer());
            npcConfig.set("npcs." + data.getName() + ".message", data.getMessage());

            if (data.getSkin() != null) {
                npcConfig.set("npcs." + data.getName() + ".skin.identifier", data.getSkin().getIdentifier());
                npcConfig.set("npcs." + data.getName() + ".skin.value", data.getSkin().getValue());
                npcConfig.set("npcs." + data.getName() + ".skin.signature", data.getSkin().getSignature());
            }

            if (data.getEquipment() != null) {
                for (Map.Entry<NpcEquipmentSlot, ItemStack> entry : data.getEquipment().entrySet()) {
                    npcConfig.set("npcs." + data.getName() + ".equipment." + entry.getKey().name(), entry.getValue());
                }
            }

            if (data.getServerCommand() != null) {
                npcConfig.set("npcs." + data.getName() + ".serverCommand", data.getServerCommand());
            }

            if (data.getPlayerCommand() != null) {
                npcConfig.set("npcs." + data.getName() + ".playerCommand", data.getPlayerCommand());
            }

            if (FancyNpcs.NPC_ATTRIBUTES_FEATURE_FLAG.isEnabled()) {
                for (NpcAttribute attribute : FancyNpcs.getInstance().getAttributeManager().getAllAttributesForEntityType(data.getType())) {
                    String value = data.getAttributes().getOrDefault(attribute, null);
                    npcConfig.set("npcs." + data.getName() + ".attributes." + attribute.getName(), value);
                }
            }

            npc.setDirty(false);
        }

        try {
            npcConfig.save(npcConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadNpcs() {
        YamlConfiguration npcConfig = YamlConfiguration.loadConfiguration(npcConfigFile);

        if (!npcConfig.isConfigurationSection("npcs")) {
            return;
        }

        for (String name : npcConfig.getConfigurationSection("npcs").getKeys(false)) {
            String displayName = npcConfig.getString("npcs." + name + ".displayName");
            EntityType type = EntityType.valueOf(npcConfig.getString("npcs." + name + ".type", "PLAYER"));

            Location location = null;

            try {
                location = npcConfig.getLocation("npcs." + name + ".location");
            } catch (Exception ignored) {
            }

            if (location == null) {
                String worldName = npcConfig.getString("npcs." + name + ".location.world");
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    plugin.getLogger().info("Trying to load the world: '" + worldName + "'");
                    world = new WorldCreator(worldName).createWorld();
                }

                if (world == null) {
                    plugin.getLogger().info("Could not load npc '" + name + "', because the world '" + worldName + "' is not loaded");
                    continue;
                }

                double x = npcConfig.getDouble("npcs." + name + ".location.x");
                double y = npcConfig.getDouble("npcs." + name + ".location.y");
                double z = npcConfig.getDouble("npcs." + name + ".location.z");
                float yaw = (float) npcConfig.getDouble("npcs." + name + ".location.yaw");
                float pitch = (float) npcConfig.getDouble("npcs." + name + ".location.pitch");

                location = new Location(world, x, y, z, yaw, pitch);
            }

            String skinIdentifier = npcConfig.getString("npcs." + name + ".skin.identifier", npcConfig.getString("npcs." + name + ".skin.uuid", ""));
            String skinValue = npcConfig.getString("npcs." + name + ".skin.value");
            String skinSignature = npcConfig.getString("npcs." + name + ".skin.signature");
            SkinFetcher skin = null;
            if (skinIdentifier.length() > 0) {
                skin = new SkinFetcher(skinIdentifier, skinValue, skinSignature);
            }

            boolean showInTab = npcConfig.getBoolean("npcs." + name + ".showInTab");
            boolean spawnEntity = npcConfig.getBoolean("npcs." + name + ".spawnEntity");
            boolean glowing = npcConfig.getBoolean("npcs." + name + ".glowing");
            NamedTextColor glowingColor = NamedTextColor.NAMES.value(npcConfig.getString("npcs." + name + ".glowingColor", "white"));
            boolean turnToPlayer = npcConfig.getBoolean("npcs." + name + ".turnToPlayer");
            String serverCommand = npcConfig.getString("npcs." + name + ".serverCommand");
            String playerCommand = npcConfig.getString("npcs." + name + ".playerCommand");
            String message = npcConfig.getString("npcs." + name + ".message");

            Map<NpcAttribute, String> attributes = new HashMap<>();
            if (FancyNpcs.NPC_ATTRIBUTES_FEATURE_FLAG.isEnabled()) {
                if (npcConfig.isConfigurationSection("npcs." + name + ".attributes")) {
                    for (String attrName : npcConfig.getConfigurationSection("npcs." + name + ".attributes").getKeys(false)) {
                        NpcAttribute attribute = FancyNpcs.getInstance().getAttributeManager().getAttributeByName(type, attrName);
                        if (attribute == null) {
                            continue;
                        }

                        String value = npcConfig.getString("npcs." + name + ".attributes." + attrName);
                        if (!attribute.isValidValue(value)) {
                            continue;
                        }

                        attributes.put(attribute, value);
                    }
                }
            }

            NpcData data = new NpcData(name, displayName, skin, location, showInTab, spawnEntity, glowing, glowingColor, type, new HashMap<>(), turnToPlayer, null, message, serverCommand, playerCommand, attributes);
            Npc npc = npcAdapter.apply(data);

            if (npcConfig.isConfigurationSection("npcs." + name + ".equipment")) {
                for (String equipmentSlotStr : npcConfig.getConfigurationSection("npcs." + name + ".equipment").getKeys(false)) {
                    NpcEquipmentSlot equipmentSlot = NpcEquipmentSlot.parse(equipmentSlotStr);
                    ItemStack item = npcConfig.getItemStack("npcs." + name + ".equipment." + equipmentSlotStr);
                    npc.getData().addEquipment(equipmentSlot, item);
                }
            }

            npc.create();
            registerNpc(npc);
            npc.spawnForAll();
        }
    }

    public void reloadNpcs() {
        for (Npc npc : new ArrayList<>(getAllNpcs())) {
            npc.removeForAll();
        }

        loadNpcs();
    }
}
