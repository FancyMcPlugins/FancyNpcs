package de.oliver.fancynpcs;

import de.oliver.fancynpcs.utils.SkinFetcher;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NpcManager {

    private final File npcConfigFile = new File(FancyNpcs.getInstance().getDataFolder().getAbsolutePath() + "/npcs.yml");
    private final HashMap<String, Npc> npcs; // npc name -> npc

    public NpcManager() {
        npcs = new HashMap<>();
    }

    public void registerNpc(Npc npc){
        npcs.put(npc.getName(), npc);
    }

    public void removeNpc(Npc npc){
        npcs.remove(npc.getName());

        YamlConfiguration npcConfig = YamlConfiguration.loadConfiguration(npcConfigFile);
        npcConfig.set("npcs." + npc.getName(), null);
        try {
            npcConfig.save(npcConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Npc getNpc(int entityId){
        for (Npc npc : npcs.values()) {
            if(npc.getNpc() != null && npc.getNpc().getId() == entityId){
                return npc;
            }
        }

        return null;
    }

    public Npc getNpc(String name){
        return npcs.getOrDefault(name, null);
    }

    public Collection<Npc> getAllNpcs(){
        return npcs.values();
    }

    public void saveNpcs(boolean force){
        if(!npcConfigFile.exists()){
            try {
                npcConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        YamlConfiguration npcConfig = YamlConfiguration.loadConfiguration(npcConfigFile);

        for (Npc npc : npcs.values()) {
            if(!npc.isSaveToFile()){
                continue;
            }

            boolean shouldSave = force || npc.isDirty();
            if(!shouldSave){
                continue;
            }

            npcConfig.set("npcs." + npc.getName() + ".displayName", npc.getDisplayName());
            npcConfig.set("npcs." + npc.getName() + ".location", npc.getLocation());
            npcConfig.set("npcs." + npc.getName() + ".showInTab", npc.isShowInTab());
            npcConfig.set("npcs." + npc.getName() + ".spawnEntity", npc.isSpawnEntity());
            npcConfig.set("npcs." + npc.getName() + ".glowing", npc.isGlowing());
            npcConfig.set("npcs." + npc.getName() + ".glowingColor", npc.getGlowingColor().getName());
            npcConfig.set("npcs." + npc.getName() + ".turnToPlayer", npc.isTurnToPlayer());

            if(npc.getSkin() != null) {
                npcConfig.set("npcs." + npc.getName() + ".skin.uuid", npc.getSkin().getUuid());
                npcConfig.set("npcs." + npc.getName() + ".skin.value", npc.getSkin().getValue());
                npcConfig.set("npcs." + npc.getName() + ".skin.signature", npc.getSkin().getSignature());
            }

            if(npc.getEquipment() != null) {
                for (Map.Entry<EquipmentSlot, ItemStack> entry : npc.getEquipment().entrySet()) {
                    npcConfig.set("npcs." + npc.getName() + ".equipment." + entry.getKey().getName(), CraftItemStack.asBukkitCopy(entry.getValue()));
                }
            }

            if(npc.getServerCommand() != null){
                npcConfig.set("npcs." + npc.getName() + ".serverCommand", npc.getServerCommand());
            }

            if(npc.getPlayerCommand() != null){
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

    public void loadNpcs(){
        YamlConfiguration npcConfig = YamlConfiguration.loadConfiguration(npcConfigFile);

        if(!npcConfig.isConfigurationSection("npcs")){
            return;
        }

        for (String name : npcConfig.getConfigurationSection("npcs").getKeys(false)) {
            String displayName = npcConfig.getString("npcs." + name + ".displayName");
            Location location = npcConfig.getLocation("npcs." + name + ".location");
            String skinUuid = npcConfig.getString("npcs." + name + ".skin.uuid");
            String skinValue = npcConfig.getString("npcs." + name + ".skin.value");
            String skinSignature = npcConfig.getString("npcs." + name + ".skin.signature");
            SkinFetcher skin = new SkinFetcher(skinUuid, skinValue, skinSignature);
            boolean showInTab = npcConfig.getBoolean("npcs." + name + ".showInTab");
            boolean spawnEntity = npcConfig.getBoolean("npcs." + name + ".spawnEntity");
            boolean glowing = npcConfig.getBoolean("npcs." + name + ".glowing");
            ChatFormatting glowingColor = ChatFormatting.getByName(npcConfig.getString("npcs." + name + ".glowingColor"));
            boolean turnToPlayer = npcConfig.getBoolean("npcs." + name + ".turnToPlayer");
            String serverCommand = npcConfig.getString("npcs." + name + ".serverCommand");
            String playerCommand = npcConfig.getString("npcs." + name + ".playerCommand");

            Npc npc = new Npc(name, location);
            if(npcConfig.isConfigurationSection("npcs." + name + ".equipment")){
                for (String equipmentSlotStr : npcConfig.getConfigurationSection("npcs." + name + ".equipment").getKeys(false)) {
                    EquipmentSlot equipmentSlot = EquipmentSlot.byName(equipmentSlotStr);
                    org.bukkit.inventory.ItemStack item = npcConfig.getItemStack("npcs." + name + ".equipment." + equipmentSlotStr);
                    npc.addEquipment(equipmentSlot, CraftItemStack.asNMSCopy(item));
                }
            }

            npc.setShowInTab(showInTab);
            npc.setSpawnEntity(spawnEntity);

            npc.setGlowing(glowing);
            npc.setGlowingColor(glowingColor);

            npc.setTurnToPlayer(turnToPlayer);

            if(displayName != null && displayName.length() > 0) {
                npc.setDisplayName(displayName);
            }

            if(serverCommand != null && serverCommand.length() > 0){
                npc.setServerCommand(serverCommand);
            }

            if(playerCommand != null && playerCommand.length() > 0){
                npc.setPlayerCommand(playerCommand);
            }

            if(npcConfig.isConfigurationSection("npcs." + name + ".skin")){
                npc.setSkin(skin);
            }

            npc.create();
            npc.register();
            npc.spawnForAll();
        }
    }

    public void reloadNpcs(){
        for (Npc npc : new ArrayList<>(getAllNpcs())) {
            npc.removeForAll();
        }

        loadNpcs();
    }
}
