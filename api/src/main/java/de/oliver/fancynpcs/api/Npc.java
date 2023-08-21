package de.oliver.fancynpcs.api;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.oliver.fancylib.RandomUtils;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Npc {

    private static final char[] localNameChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};
    protected final Map<UUID, Boolean> isTeamCreated = new HashMap<>();
    protected final Map<UUID, Boolean> isVisibleForPlayer = new HashMap<>();
    protected final Map<UUID, Boolean> isLookingAtPlayer = new HashMap<>();
    protected NpcData data;
    protected boolean saveToFile;

    public Npc(NpcData data) {
        this.data = data;
        this.saveToFile = true;
    }

    protected String generateLocalName() {
        String localName = "";
        for (int i = 0; i < 8; i++) {
            localName += "&" + localNameChars[(int) RandomUtils.randomInRange(0, localNameChars.length)];
        }

        localName = ChatColor.translateAlternateColorCodes('&', localName);

        return localName;
    }

    public abstract void create();

    public abstract void spawn(Player player);

    public void spawnForAll() {
        // TODO: check for each player if NPC should be visible (see distance thing - PlayerMoveListener)
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            spawn(onlinePlayer);
        }
    }

    public abstract void remove(Player player);

    public void removeForAll() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            remove(onlinePlayer);
        }
    }

    public abstract void lookAt(Player player, Location location);

    public abstract void update(Player player);

    public void updateForAll() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            update(onlinePlayer);
        }
    }

    public void interact(Player player, boolean isAttack, EquipmentSlot hand, Vector clickLoc) {
        if (!isAttack && (hand == EquipmentSlot.HAND || clickLoc != null)) {
            return;
        }

        NpcInteractEvent npcInteractEvent = new NpcInteractEvent(this, data.getPlayerCommand(), data.getServerCommand(), data.getOnClick(), player);
        npcInteractEvent.callEvent();

        if (npcInteractEvent.isCancelled()) {
            return;
        }

        // onClick
        if (data.getOnClick() != null) {
            data.getOnClick().accept(player);
        }

        // message
        if (data.getMessage() != null && data.getMessage().length() > 0) {
            String msg = data.getMessage();
            if (FancyNpcsPlugin.get().isUsingPlaceholderAPI()) {
                msg = PlaceholderAPI.setPlaceholders(player, msg);
            }

            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
        }

        // serverCommand
        if (data.getServerCommand() != null && data.getServerCommand().length() > 0) {
            String command = data.getServerCommand();
            command = command.replace("{player}", player.getName());

            if (FancyNpcsPlugin.get().isUsingPlaceholderAPI()) {
                command = PlaceholderAPI.setPlaceholders(player, command);
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        // playerCommand
        if (data.getPlayerCommand() != null && data.getPlayerCommand().length() > 0) {
            String command;

            if (FancyNpcsPlugin.get().isUsingPlaceholderAPI()) {
                command = PlaceholderAPI.setPlaceholders(player, data.getPlayerCommand());
            } else {
                command = data.getPlayerCommand();
            }

            if (command.toLowerCase().startsWith("server")) {
                String[] args = data.getPlayerCommand().split(" ");
                if (args.length < 2) {
                    return;
                }
                String server = args[1];

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(server);
                player.sendPluginMessage(FancyNpcsPlugin.get().getPlugin(), "BungeeCord", out.toByteArray());
                return;
            }

            FancyNpcsPlugin.get().getScheduler().runTask(
                    player.getLocation(),
                    () -> player.chat("/" + command)
            );
        }
    }

    protected abstract void refreshEntityData(Player serverPlayer);

    public abstract int getEntityId();

    public NpcData getData() {
        return data;
    }

    public abstract float getEyeHeight();

    public Map<UUID, Boolean> getIsTeamCreated() {
        return isTeamCreated;
    }

    public Map<UUID, Boolean> getIsVisibleForPlayer() {
        return isVisibleForPlayer;
    }

    public Map<UUID, Boolean> getIsLookingAtPlayer() {
        return isLookingAtPlayer;
    }

    public boolean isDirty() {
        return data.isDirty();
    }

    public void setDirty(boolean dirty) {
        data.setDirty(dirty);
    }

    public boolean isSaveToFile() {
        return saveToFile;
    }

    public void setSaveToFile(boolean saveToFile) {
        this.saveToFile = saveToFile;
    }
}
