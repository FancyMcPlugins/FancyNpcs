package de.oliver;

import de.oliver.commands.FancyNpcsCMD;
import de.oliver.commands.NpcCMD;
import de.oliver.listeners.PacketReceivedListener;
import de.oliver.listeners.PlayerChangedWorldListener;
import de.oliver.listeners.PlayerJoinListener;
import de.oliver.listeners.PlayerMoveListener;
import de.oliver.utils.Metrics;
import de.oliver.utils.VersionFetcher;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class FancyNpcs extends JavaPlugin {

    public static final String SUPPORTED_VERSION = "1.20";

    private static FancyNpcs instance;
    private final NpcManager npcManager;
    private final FancyNpcConfig config;

    public FancyNpcs() {
        // TODO: remove in v1.1.3
        // rename old plugin
        File oldPluginFolder = new File("plugins/NpcPlugin/");
        if(oldPluginFolder.exists() && oldPluginFolder.isDirectory()){
            try {
                oldPluginFolder.renameTo(new File("plugins/FancyNpcs/"));
            } catch (Exception ignored){ }
        }

        instance = this;
        this.npcManager = new NpcManager();
        this.config = new FancyNpcConfig();
    }

    @Override
    public void onEnable() {
        config.reload();

        new Thread(() -> {
            ComparableVersion newestVersion = VersionFetcher.getNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(getDescription().getVersion());
            if(newestVersion == null){
                getLogger().warning("Could not fetch latest plugin version");
            } else if (newestVersion.compareTo(currentVersion) > 0) {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("You are not using the latest version the FancyNpcs plugin.");
                getLogger().warning("Please update to the newest version (" + newestVersion + ").");
                getLogger().warning(VersionFetcher.DOWNLOAD_URL);
                getLogger().warning("-------------------------------------------------------");
            }
        }).start();

        PluginManager pluginManager = Bukkit.getPluginManager();
        DedicatedServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();

        String serverVersion = nmsServer.getServerVersion();
        if(!serverVersion.equals(SUPPORTED_VERSION)){
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Please update the server to " + SUPPORTED_VERSION + ".");
            getLogger().warning("Disabling NPC plugin.");
            getLogger().warning("--------------------------------------------------");
            pluginManager.disablePlugin(this);
            return;
        }

        String serverSoftware = nmsServer.getServerModName();
        if(!serverSoftware.equals("Paper")){
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("It is recommended to use Paper as server software.");
            getLogger().warning("Because you are not using paper, the plugin");
            getLogger().warning("might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        // register bStats
        Metrics metrics = new Metrics(this, 17543);

        // register commands
        getCommand("fancynpcs").setExecutor(new FancyNpcsCMD());
        getCommand("npc").setExecutor(new NpcCMD());

        // register listeners
        pluginManager.registerEvents(new PlayerJoinListener(), instance);
        pluginManager.registerEvents(new PlayerMoveListener(), instance);
        pluginManager.registerEvents(new PlayerChangedWorldListener(), instance);
        pluginManager.registerEvents(new PacketReceivedListener(), instance);

        // using bungee plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // load config
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PacketReader packetReader = new PacketReader(onlinePlayer);
                packetReader.inject();
            }

            npcManager.loadNpcs();
        }, 20L*5);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, () -> npcManager.saveNpcs(false), 20L*60*5, 20L*60*15);
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        npcManager.saveNpcs(true);
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    public FancyNpcConfig getFancyNpcConfig() {
        return config;
    }
    public static FancyNpcs getInstance() {
        return instance;
    }
}
