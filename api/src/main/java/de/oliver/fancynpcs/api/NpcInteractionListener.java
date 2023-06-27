package de.oliver.fancynpcs.api;

import de.oliver.fancynpcs.api.events.PacketReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class NpcInteractionListener implements Listener {

    protected final JavaPlugin plugin;

    public NpcInteractionListener() {
        plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("FancyNpcs");
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public abstract void onPacketReceived(PacketReceivedEvent event);

    public abstract boolean injectPlayer(Player player);
}
