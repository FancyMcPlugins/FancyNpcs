package de.oliver.fancynpcs.api;

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

public class NpcManager {

    private final JavaPlugin plugin;
    private final Function<NpcData, Npc> npcAdapter;
    private final File npcConfigFile;
    private final HashMap<String, Npc> npcs; // npc name -> npc

    public NpcManager(JavaPlugin plugin, Function<NpcData, Npc> npcAdapter) {
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

            npcConfig.set("npcs." + npc.getData().getName() + ".name", npc.getData().getName());
            npcConfig.set("npcs." + npc.getData().getName() + ".displayName", npc.getData().getDisplayName());
            npcConfig.set("npcs." + npc.getData().getName() + ".type", npc.getData().getType().name());
            npcConfig.set("npcs." + npc.getData().getName() + ".location.world", npc.getData().getLocation().getWorld().getName());
            npcConfig.set("npcs." + npc.getData().getName() + ".location.x", npc.getData().getLocation().getX());
            npcConfig.set("npcs." + npc.getData().getName() + ".location.y", npc.getData().getLocation().getY());
            npcConfig.set("npcs." + npc.getData().getName() + ".location.z", npc.getData().getLocation().getZ());
            npcConfig.set("npcs." + npc.getData().getName() + ".location.yaw", npc.getData().getLocation().getYaw());
            npcConfig.set("npcs." + npc.getData().getName() + ".location.pitch", npc.getData().getLocation().getPitch());
            npcConfig.set("npcs." + npc.getData().getName() + ".showInTab", npc.getData().isShowInTab());
            npcConfig.set("npcs." + npc.getData().getName() + ".spawnEntity", npc.getData().isSpawnEntity());
            npcConfig.set("npcs." + npc.getData().getName() + ".glowing", npc.getData().isGlowing());
            npcConfig.set("npcs." + npc.getData().getName() + ".glowingColor", npc.getData().getGlowingColor().toString());
            npcConfig.set("npcs." + npc.getData().getName() + ".turnToPlayer", npc.getData().isTurnToPlayer());
            npcConfig.set("npcs." + npc.getData().getName() + ".message", npc.getData().getMessage());

            if (npc.getData().getSkin() != null) {
                npcConfig.set("npcs." + npc.getData().getName() + ".skin.identifier", npc.getData().getSkin().getIdentifier());
                npcConfig.set("npcs." + npc.getData().getName() + ".skin.value", npc.getData().getSkin().getValue());
                npcConfig.set("npcs." + npc.getData().getName() + ".skin.signature", npc.getData().getSkin().getSignature());
            }

            if (npc.getData().getEquipment() != null) {
                for (Map.Entry<NpcEquipmentSlot, ItemStack> entry : npc.getData().getEquipment().entrySet()) {
                    npcConfig.set("npcs." + npc.getData().getName() + ".equipment." + entry.getKey().name(), entry.getValue());
                }
            }

            if (npc.getData().getServerCommand() != null) {
                npcConfig.set("npcs." + npc.getData().getName() + ".serverCommand", npc.getData().getServerCommand());
            }

            if (npc.getData().getPlayerCommand() != null) {
                npcConfig.set("npcs." + npc.getData().getName() + ".playerCommand", npc.getData().getPlayerCommand());
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

            NpcData data = new NpcData(name, displayName, skin, location, showInTab, spawnEntity, glowing, glowingColor, type, new HashMap<>(), turnToPlayer, null, message, serverCommand, playerCommand);
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
