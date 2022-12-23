package de.oliver;

import de.oliver.commands.NpcCMD;
import de.oliver.listeners.PacketReceivedListener;
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
        PluginManager pluginManager = Bukkit.getPluginManager();

        if(!getServer().getMinecraftVersion().equals("1.19.3")){
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Disabling plugin.");
            pluginManager.disablePlugin(this);
            return;
        }

        getCommand("npc").setExecutor(new NpcCMD());

        pluginManager.registerEvents(new PlayerJoinListener(), instance);
        pluginManager.registerEvents(new PacketReceivedListener(), instance);

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            npcManager.loadNpcs();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PacketReader packetReader = new PacketReader(onlinePlayer);
                packetReader.inject();

                npcManager.getAllNpcs().forEach(npc -> npc.spawn(onlinePlayer));
            }
        }, 20L*5);
    }

    @Override
    public void onDisable() {
        npcManager.saveNpcs();
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    public static NpcPlugin getInstance() {
        return instance;
    }
}
