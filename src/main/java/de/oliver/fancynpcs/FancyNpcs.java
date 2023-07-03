package de.oliver.fancynpcs;

import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.Metrics;
import de.oliver.fancylib.VersionFetcher;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FoliaScheduler;
import de.oliver.fancynpcs.api.*;
import de.oliver.fancynpcs.commands.FancyNpcsCMD;
import de.oliver.fancynpcs.commands.NpcCMD;
import de.oliver.fancynpcs.listeners.PlayerJoinListener;
import de.oliver.fancynpcs.tracker.NpcTracker;
import de.oliver.fancynpcs.v1_19_4.NpcInteractionListener_1_19_4;
import de.oliver.fancynpcs.v1_19_4.Npc_1_19_4;
import de.oliver.fancynpcs.v1_20_1.NpcInteractionListener_1_20_1;
import de.oliver.fancynpcs.v1_20_1.Npc_1_20_1;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public class FancyNpcs extends JavaPlugin implements FancyNpcsPlugin {

    public static final String[] SUPPORTED_VERSIONS = new String[]{"1.19.4", "1.20.1"};

    private static FancyNpcs instance;
    private final FancyScheduler scheduler;
    private final FancyNpcConfig config;
    private final VersionFetcher versionFetcher;
    private NpcManager npcManager;
    private Function<NpcData, Npc> npcAdapter;
    private NpcInteractionListener npcInteractionListener;
    private boolean usingPlaceholderAPI;

    public FancyNpcs() {
        instance = this;
        this.scheduler = ServerSoftware.isFolia()
                ? new FoliaScheduler(instance)
                : new BukkitScheduler(instance);
        this.config = new FancyNpcConfig();
        this.versionFetcher = new VersionFetcher("https://api.modrinth.com/v2/project/fancynpcs/version", "https://modrinth.com/plugin/fancynpcs/versions");
    }

    public static FancyNpcs getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        String mcVersion = Bukkit.getMinecraftVersion();

        switch (mcVersion) {
            case "1.20.1" -> {
                npcAdapter = Npc_1_20_1::new;
                npcInteractionListener = new NpcInteractionListener_1_20_1();
            }

            case "1.19.4" -> {
                npcAdapter = Npc_1_19_4::new;
                npcInteractionListener = new NpcInteractionListener_1_19_4();
            }

            default -> {
                npcAdapter = null;
                npcInteractionListener = null;
            }
        }

        PluginManager pluginManager = Bukkit.getPluginManager();

        if (npcAdapter == null || npcInteractionListener == null) {
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Please update the server to " + String.join(" / ", SUPPORTED_VERSIONS) + ".");
            getLogger().warning("Disabling the FancyNpcs plugin.");
            getLogger().warning("--------------------------------------------------");
            pluginManager.disablePlugin(this);
            return;
        }

        npcManager = new NpcManager(this, npcAdapter);
    }

    @Override
    public void onEnable() {
        if (npcAdapter == null || npcInteractionListener == null) {
            return;
        }

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

        if (!ServerSoftware.isPaper()) {
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("It is recommended to use Paper as server software.");
            getLogger().warning("Because you are not using paper, the plugin");
            getLogger().warning("might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        // register bStats
        Metrics metrics = new Metrics(this, 17543);

        PluginManager pluginManager = Bukkit.getPluginManager();
        usingPlaceholderAPI = pluginManager.isPluginEnabled("PlaceholderAPI");

        // register commands
        getCommand("fancynpcs").setExecutor(new FancyNpcsCMD());
        getCommand("npc").setExecutor(new NpcCMD());

        // register listeners
        pluginManager.registerEvents(new PlayerJoinListener(), instance);
        npcInteractionListener.register();

        // using bungee plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // load config
        scheduler.runTaskLater(null, 20L * 5, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                npcInteractionListener.injectPlayer(onlinePlayer);
            }

            npcManager.loadNpcs();
        });

        scheduler.runTaskTimerAsynchronously(0, 1, new NpcTracker());

        int autosaveInterval = config.getAutoSaveInterval();
        if (config.isEnableAutoSave()) {
            scheduler.runTaskTimerAsynchronously(autosaveInterval * 60L * 20L, autosaveInterval * 60L * 20L, () -> npcManager.saveNpcs(false));
        }
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        if (npcManager != null) {
            npcManager.saveNpcs(true);
        }
    }

    @Override
    public Function<NpcData, Npc> getNpcAdapter() {
        return npcAdapter;
    }

    @Override
    public NpcInteractionListener getNpcInteractionListener() {
        return npcInteractionListener;
    }

    @Override
    public FancyScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public NpcManager getNpcManager() {
        return npcManager;
    }

    public FancyNpcConfig getFancyNpcConfig() {
        return config;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    @Override
    public boolean isUsingPlaceholderAPI() {
        return usingPlaceholderAPI;
    }

    @Override
    public JavaPlugin getPlugin() {
        return instance;
    }
}
