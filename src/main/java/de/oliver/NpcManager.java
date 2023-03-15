package de.oliver;

import de.oliver.utils.SkinFetcher;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NpcManager {

    private final HashMap<Integer, Npc> npcs; // entityId -> npc

    public NpcManager() {
        npcs = new HashMap<>();
    }

    public void registerNpc(Npc npc){
        npcs.put(npc.getNpc().getId(), npc);
    }

    public void removeNpc(Npc npc){
        npcs.remove(npc.getNpc().getId());
    }

    public Npc getNpc(int entityId){
        if(npcs.containsKey(entityId)){
            return npcs.get(entityId);
        }

        return null;
    }

    public Npc getNpc(String name){
        for (Npc npc : npcs.values()) {
            if(npc.getName().equalsIgnoreCase(name)){
                return npc;
            }
        }

        return null;
    }

    public Collection<Npc> getAllNpcs(){
        return npcs.values();
    }

    public void saveNpcs(){
        FileConfiguration config = NpcPlugin.getInstance().getConfig();

        if(config.isConfigurationSection("npcs")) {
            config.set("npcs", null);
        }

        for (Npc npc : npcs.values()) {
            config.set("npcs." + npc.getName() + ".displayName", npc.getDisplayName());
            config.set("npcs." + npc.getName() + ".location", npc.getLocation());
            config.set("npcs." + npc.getName() + ".showInTab", npc.isShowInTab());
            config.set("npcs." + npc.getName() + ".spawnEntity", npc.isSpawnEntity());
            config.set("npcs." + npc.getName() + ".glowing", npc.isGlowing());
            config.set("npcs." + npc.getName() + ".glowingColor", npc.getGlowingColor().getName());
            config.set("npcs." + npc.getName() + ".turnToPlayer", npc.isTurnToPlayer());

            if(npc.getSkin() != null) {
                config.set("npcs." + npc.getName() + ".skin.uuid", npc.getSkin().getUuid());
                config.set("npcs." + npc.getName() + ".skin.value", npc.getSkin().getValue());
                config.set("npcs." + npc.getName() + ".skin.signature", npc.getSkin().getSignature());
            }

            if(npc.getEquipment() != null) {
                for (Map.Entry<EquipmentSlot, ItemStack> entry : npc.getEquipment().entrySet()) {
                    config.set("npcs." + npc.getName() + ".equipment." + entry.getKey().getName(), CraftItemStack.asBukkitCopy(entry.getValue()));
                }
            }

            if(npc.getServerCommand() != null){
                config.set("npcs." + npc.getName() + ".serverCommand", npc.getServerCommand());
            }

            if(npc.getPlayerCommand() != null){
                config.set("npcs." + npc.getName() + ".playerCommand", npc.getPlayerCommand());
            }

        }

        NpcPlugin.getInstance().saveConfig();
    }

    public void loadNpcs(){
        NpcPlugin.getInstance().reloadConfig();
        FileConfiguration config = NpcPlugin.getInstance().getConfig();

        if(!config.isConfigurationSection("npcs")){
            return;
        }

        for (String name : config.getConfigurationSection("npcs").getKeys(false)) {
            String displayName = config.getString("npcs." + name + ".displayName");
            Location location = (Location) config.get("npcs." + name + ".location");
            String skinUuid = config.getString("npcs." + name + ".skin.uuid");
            String skinValue = config.getString("npcs." + name + ".skin.value");
            String skinSignature = config.getString("npcs." + name + ".skin.signature");
            SkinFetcher skin = new SkinFetcher(skinUuid, skinValue, skinSignature);
            boolean showInTab = config.getBoolean("npcs." + name + ".showInTab");
            boolean spawnEntity = config.getBoolean("npcs." + name + ".spawnEntity");
            boolean glowing = config.getBoolean("npcs." + name + ".glowing");
            ChatFormatting glowingColor = ChatFormatting.getByName(config.getString("npcs." + name + ".glowingColor"));
            boolean turnToPlayer = config.getBoolean("npcs." + name + ".turnToPlayer");
            String serverCommand = config.getString("npcs." + name + ".serverCommand");
            String playerCommand = config.getString("npcs." + name + ".playerCommand");

            Npc npc = new Npc(name, location);
            if(config.isConfigurationSection("npcs." + name + ".equipment")){
                for (String equipmentSlotStr : config.getConfigurationSection("npcs." + name + ".equipment").getKeys(false)) {
                    EquipmentSlot equipmentSlot = EquipmentSlot.byName(equipmentSlotStr);
                    org.bukkit.inventory.ItemStack item = config.getItemStack("npcs." + name + ".equipment." + equipmentSlotStr);
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

            if(config.isConfigurationSection("npcs." + name + ".skin")){
                npc.setSkin(skin);
            }

            npc.create();
        }

    }

}
