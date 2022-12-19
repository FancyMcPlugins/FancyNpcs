package de.oliver;

import de.oliver.commands.TestCMD;
import de.oliver.events.PacketReceivedListener;
import de.oliver.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NpcPlugin extends JavaPlugin {

    private static NpcPlugin instance;
    private final NpcManager npcManager;

    public NpcPlugin() {
        instance = this;
        this.npcManager = new NpcManager();
    }

    @Override
    public void onEnable() {
        getCommand("test").setExecutor(new TestCMD());

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), instance);
        pluginManager.registerEvents(new PacketReceivedListener(), instance);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PacketReader packetReader = new PacketReader(onlinePlayer);
            packetReader.inject();
        }
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    public static NpcPlugin getInstance() {
        return instance;
    }
}
