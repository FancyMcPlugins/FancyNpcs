package de.oliver.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.utils.SkinFetcher;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NpcManagerImpl implements NpcManager {

    private final File npcConfigFile = new File(FancyNpcs.getInstance().getDataFolder().getAbsolutePath() + "/npcs.yml");
    private final HashMap<String, NpcImpl> npcs; // npc name -> npc

    public NpcManagerImpl() {
        npcs = new HashMap<>();
    }

    @Override
    public void registerNpc(Npc npc) {
        npcs.put(npc.getName(), (NpcImpl) npc);
    }

    @Override
    public void removeNpc(Npc npc) {
        npcs.remove(npc.getName());

        YamlConfiguration npcConfig = YamlConfiguration.loadConfiguration(npcConfigFile);
        npcConfig.set("npcs." + npc.getName(), null);
        try {
            npcConfig.save(npcConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Npc getNpc(int entityId) {
        return getNpcImpl(entityId);
    }

    public NpcImpl getNpcImpl(int entityId) {
        for (NpcImpl npc : npcs.values()) {
            if (npc.getNpc() != null && npc.getNpc().getId() == entityId) {
                return npc;
            }
        }

        return null;
    }

    @Override
    public Npc getNpc(String name) {
        return getNpcImpl(name);
    }

    public NpcImpl getNpcImpl(String name){
        return npcs.getOrDefault(name, null);
    }

    @Override
    public Collection<Npc> getAllNpcs() {
        return getAllNpcsImpl().stream().map(npc -> (Npc) npc).toList();
    }

    public Collection<NpcImpl> getAllNpcsImpl() {
        return npcs.values();
    }

    @Override
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

        for (NpcImpl npc : npcs.values()) {
            if (!npc.isSaveToFile()) {
                continue;
            }

            boolean shouldSave = force || npc.isDirty();
            if (!shouldSave) {
                continue;
            }

            npcConfig.set("npcs." + npc.getName() + ".displayName", npc.getDisplayName());
            npcConfig.set("npcs." + npc.getName() + ".type", npc.getType().toShortString());
            npcConfig.set("npcs." + npc.getName() + ".location.world", npc.getLocation().getWorld().getName());
            npcConfig.set("npcs." + npc.getName() + ".location.x", npc.getLocation().getX());
            npcConfig.set("npcs." + npc.getName() + ".location.y", npc.getLocation().getY());
            npcConfig.set("npcs." + npc.getName() + ".location.z", npc.getLocation().getZ());
            npcConfig.set("npcs." + npc.getName() + ".location.yaw", npc.getLocation().getYaw());
            npcConfig.set("npcs." + npc.getName() + ".location.pitch", npc.getLocation().getPitch());
            npcConfig.set("npcs." + npc.getName() + ".showInTab", npc.isShowInTab());
            npcConfig.set("npcs." + npc.getName() + ".spawnEntity", npc.isSpawnEntity());
            npcConfig.set("npcs." + npc.getName() + ".glowing", npc.isGlowing());
            npcConfig.set("npcs." + npc.getName() + ".glowingColor", npc.getGlowingColor().getName());
            npcConfig.set("npcs." + npc.getName() + ".turnToPlayer", npc.isTurnToPlayer());
            npcConfig.set("npcs." + npc.getName() + ".message", npc.getMessage());

            if (npc.getSkin() != null) {
                npcConfig.set("npcs." + npc.getName() + ".skin.identifier", npc.getSkin().getIdentifier());
                npcConfig.set("npcs." + npc.getName() + ".skin.value", npc.getSkin().getValue());
                npcConfig.set("npcs." + npc.getName() + ".skin.signature", npc.getSkin().getSignature());
            }

            if (npc.getEquipment() != null) {
                for (Map.Entry<EquipmentSlot, ItemStack> entry : npc.getEquipment().entrySet()) {
                    org.bukkit.inventory.ItemStack bukkitItemStack = FancyNpcs.getInstance().getNmsBase().getBukkitItemStack(entry.getValue());
                    npcConfig.set("npcs." + npc.getName() + ".equipment." + entry.getKey().getName(), bukkitItemStack);
                }
            }

            if (npc.getServerCommand() != null) {
                npcConfig.set("npcs." + npc.getName() + ".serverCommand", npc.getServerCommand());
            }

            if (npc.getPlayerCommand() != null) {
                npcConfig.set("npcs." + npc.getName() + ".playerCommand", npc.getPlayerCommand());
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
            EntityType<?> type = FancyNpcs.getInstance().getNmsBase().getAllEntityTypes().get(npcConfig.getString("npcs." + name + ".type", "player"));

            Location location = null;

            try {
                location = npcConfig.getLocation("npcs." + name + ".location");
            } catch (Exception ignored) {
            }

            if (location == null) {
                String worldName = npcConfig.getString("npcs." + name + ".location.world");
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    FancyNpcs.getInstance().getLogger().info("Trying to load the world: '" + worldName + "'");
                    world = new WorldCreator(worldName).createWorld();
                }

                if (world == null) {
                    FancyNpcs.getInstance().getLogger().info("Could not load npc '" + name + "', because the world '" + worldName + "' is not loaded");
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
            ChatFormatting glowingColor = ChatFormatting.getByName(npcConfig.getString("npcs." + name + ".glowingColor"));
            boolean turnToPlayer = npcConfig.getBoolean("npcs." + name + ".turnToPlayer");
            String serverCommand = npcConfig.getString("npcs." + name + ".serverCommand");
            String playerCommand = npcConfig.getString("npcs." + name + ".playerCommand");
            String message = npcConfig.getString("npcs." + name + ".message");

            NpcImpl npc = new NpcImpl(name, location);
            if (npcConfig.isConfigurationSection("npcs." + name + ".equipment")) {
                for (String equipmentSlotStr : npcConfig.getConfigurationSection("npcs." + name + ".equipment").getKeys(false)) {
                    EquipmentSlot equipmentSlot = EquipmentSlot.byName(equipmentSlotStr);
                    org.bukkit.inventory.ItemStack item = npcConfig.getItemStack("npcs." + name + ".equipment." + equipmentSlotStr);

                    ItemStack nmsItemStack = FancyNpcs.getInstance().getNmsBase().getNmsItemStack(item);
                    npc.addEquipment(equipmentSlot, nmsItemStack);
                }
            }

            npc.setType(type);
            npc.setShowInTab(showInTab);
            npc.setSpawnEntity(spawnEntity);
            npc.setGlowing(glowing);
            npc.setGlowingColor(glowingColor);
            npc.setTurnToPlayer(turnToPlayer);
            npc.setMessage(message);

            if (displayName != null && displayName.length() > 0) {
                npc.setDisplayName(displayName);
            }

            if (serverCommand != null && serverCommand.length() > 0) {
                npc.setServerCommand(serverCommand);
            }

            if (playerCommand != null && playerCommand.length() > 0) {
                npc.setPlayerCommand(playerCommand);
            }

            if (skin != null && skin.isLoaded()) {
                npc.setSkin(skin);
            }

            npc.create();
            npc.register();
            npc.spawnForAll();
        }
    }

    @Override
    public void reloadNpcs() {
        for (Npc npc : new ArrayList<>(getAllNpcs())) {
            npc.removeForAll();
        }

        loadNpcs();
    }
}