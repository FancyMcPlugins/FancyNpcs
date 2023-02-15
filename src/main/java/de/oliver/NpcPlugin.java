package de.oliver;

import de.oliver.commands.NpcCMD;
import de.oliver.listeners.PacketReceivedListener;
import de.oliver.listeners.PlayerChangedWorldListener;
import de.oliver.listeners.PlayerJoinListener;
import de.oliver.utils.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NpcPlugin extends JavaPlugin {

    public static final String SUPPORTED_VERSION = "1.19.3";

    private static NpcPlugin instance;
    private final NpcManager npcManager;

    public NpcPlugin() {
        instance = this;
        this.npcManager = new NpcManager();
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();


        String serverVersion = ((CraftServer) Bukkit.getServer()).getServer().getServerVersion();
        if(!serverVersion.equals(SUPPORTED_VERSION)){
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Please update the server to " + SUPPORTED_VERSION + ".");
            getLogger().warning("Disabling NPC plugin.");
            getLogger().warning("--------------------------------------------------");
            pluginManager.disablePlugin(this);
            return;
        }

        String serverSoftware = Bukkit.getServer().getName();

        if(!serverSoftware.equals("Paper")){
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("It is recommended to use Paper as server software.");
            getLogger().warning("Because you are not using paper, the plugin");
            getLogger().warning("might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        // register bStats
        Metrics metrics = new Metrics(this, 17543);

        getCommand("npc").setExecutor(new NpcCMD());

        pluginManager.registerEvents(new PlayerJoinListener(), instance);
        pluginManager.registerEvents(new PlayerChangedWorldListener(), instance);
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
