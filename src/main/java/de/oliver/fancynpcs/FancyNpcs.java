package de.oliver.fancynpcs;

import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.Metrics;
import de.oliver.fancylib.VersionFetcher;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancynpcs.commands.FancyNpcsCMD;
import de.oliver.fancynpcs.commands.NpcCMD;
import de.oliver.fancynpcs.listeners.PacketReceivedListener;
import de.oliver.fancynpcs.listeners.PlayerChangedWorldListener;
import de.oliver.fancynpcs.listeners.PlayerJoinListener;
import de.oliver.fancynpcs.listeners.PlayerMoveListener;
import de.oliver.fancynpcs.utils.EntityTypes;
import de.oliver.fancynpcs.utils.FoliaScheduler;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyNpcs extends JavaPlugin {

    public static final String SUPPORTED_VERSION = "1.19.4";

    private static FancyNpcs instance;
    private final FancyScheduler scheduler;
    private final NpcManager npcManager;
    private final FancyNpcConfig config;
    private final VersionFetcher versionFetcher;

    public FancyNpcs() {
        instance = this;
        this.scheduler = ServerSoftware.isFolia()
                ? new FoliaScheduler(instance)
                : new BukkitScheduler(instance);
        this.npcManager = new NpcManager();
        this.config = new FancyNpcConfig();
        this.versionFetcher = new VersionFetcher("https://api.modrinth.com/v2/project/fancynpcs/version", "https://modrinth.com/plugin/fancynpcs/versions");
    }

    public static FancyNpcs getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        FancyLib.setPlugin(instance);
        config.reload();

        new Thread(() -> {
            ComparableVersion newestVersion = versionFetcher.getNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(getDescription().getVersion());
            if (newestVersion == null) {
                getLogger().warning("Could not fetch latest plugin version");
            } else if (newestVersion.compareTo(currentVersion) > 0) {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("You are not using the latest version the FancyNpcs plugin.");
                getLogger().warning("Please update to the newest version (" + newestVersion + ").");
                getLogger().warning(versionFetcher.getDownloadUrl());
                getLogger().warning("-------------------------------------------------------");
            }
        }).start();

        PluginManager pluginManager = Bukkit.getPluginManager();
        DedicatedServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();

        String serverVersion = nmsServer.getServerVersion();
        if (!serverVersion.equals(SUPPORTED_VERSION)) {
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Please update the server to " + SUPPORTED_VERSION + ".");
            getLogger().warning("Disabling NPC plugin.");
            getLogger().warning("--------------------------------------------------");
            pluginManager.disablePlugin(this);
            return;
        }

        if (!ServerSoftware.isPaper()) {
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

        // load entity type mappings
        EntityTypes.loadTypes();

        // load config
        scheduler.runTaskLater(null, 20L * 5, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PacketReader packetReader = new PacketReader(onlinePlayer);
                packetReader.inject();
            }

            npcManager.loadNpcs();
        });

        int autosaveInterval = config.getAutoSaveInterval();
        if (config.isEnableAutoSave()) {
            scheduler.runTaskTimerAsynchronously(autosaveInterval * 60L, autosaveInterval * 60L, () -> npcManager.saveNpcs(false));
        }
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        npcManager.saveNpcs(true);
    }

    public FancyScheduler getScheduler() {
        return scheduler;
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    public FancyNpcConfig getFancyNpcConfig() {
        return config;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }
}
