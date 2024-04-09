package de.oliver.fancynpcs;

import de.oliver.fancylib.*;
import de.oliver.fancylib.featureFlags.FeatureFlag;
import de.oliver.fancylib.featureFlags.FeatureFlagConfig;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FoliaScheduler;
import de.oliver.fancylib.translations.TextConfig;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.versionFetcher.MasterVersionFetcher;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.commands.FancyNpcsCMD;
import de.oliver.fancynpcs.commands.npc.NpcCMD;
import de.oliver.fancynpcs.listeners.*;
import de.oliver.fancynpcs.tracker.TurnToPlayerTracker;
import de.oliver.fancynpcs.tracker.VisibilityTracker;
import de.oliver.fancynpcs.v1_19_4.Npc_1_19_4;
import de.oliver.fancynpcs.v1_19_4.PacketReader_1_19_4;
import de.oliver.fancynpcs.v1_20.PacketReader_1_20;
import de.oliver.fancynpcs.v1_20_1.Npc_1_20_1;
import de.oliver.fancynpcs.v1_20_2.Npc_1_20_2;
import de.oliver.fancynpcs.v1_20_4.Npc_1_20_4;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class FancyNpcs extends JavaPlugin implements FancyNpcsPlugin {

    public static final String[] SUPPORTED_VERSIONS = new String[]{"1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4"};
    public static final FeatureFlag PLAYER_NPCS_FEATURE_FLAG = new FeatureFlag("player-npcs", "Every player can only manage the npcs they have created", false);

    private static FancyNpcs instance;
    private final FancyScheduler scheduler;
    private final FancyNpcsConfigImpl config;
    private final LanguageConfig languageConfig;
    private final VersionConfig versionConfig;
    private final FeatureFlagConfig featureFlagConfig;
    private final VersionFetcher versionFetcher;
    private TextConfig textConfig;
    private Translator translator;
    private Function<NpcData, Npc> npcAdapter;
    private NpcManagerImpl npcManager;
    private AttributeManagerImpl attributeManager;
    private VisibilityTracker visibilityTracker;
    private boolean usingPlotSquared;

    public FancyNpcs() {
        instance = this;
        this.scheduler = ServerSoftware.isFolia()
                ? new FoliaScheduler(instance)
                : new BukkitScheduler(instance);
        this.config = new FancyNpcsConfigImpl();
        this.versionFetcher = new MasterVersionFetcher(getName());
        this.languageConfig = new LanguageConfig(this);
        this.versionConfig = new VersionConfig(this, versionFetcher);
        this.featureFlagConfig = new FeatureFlagConfig(this);
    }

    public static FancyNpcs getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        // Load feature flags
        featureFlagConfig.addFeatureFlag(PLAYER_NPCS_FEATURE_FLAG);
        featureFlagConfig.load();


        String mcVersion = Bukkit.getMinecraftVersion();

        switch (mcVersion) {
            case "1.20.3", "1.20.4" -> npcAdapter = Npc_1_20_4::new;
            case "1.20.2" -> npcAdapter = Npc_1_20_2::new;
            case "1.20.1", "1.20" -> npcAdapter = Npc_1_20_1::new;
            case "1.19.4" -> npcAdapter = Npc_1_19_4::new;
            default -> npcAdapter = null;
        }

        npcManager = new NpcManagerImpl(this, npcAdapter);

        PluginManager pluginManager = Bukkit.getPluginManager();

        if (npcAdapter == null) {
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Please update the server to " + String.join(" / ", SUPPORTED_VERSIONS) + ".");
            getLogger().warning("Disabling the FancyNpcs plugin.");
            getLogger().warning("--------------------------------------------------");
            pluginManager.disablePlugin(this);
            return;
        }

        new FileUtils().saveFile(this, "lang.yml");
    }

    @Override
    public void onEnable() {
        if (npcAdapter == null) {
            return;
        }

        FancyLib.setPlugin(instance);

        String mcVersion = Bukkit.getMinecraftVersion();

        config.reload();

        attributeManager = new AttributeManagerImpl();

        // Load language file
        String defaultLang = new FileUtils().readResource("lang.yml");
        if (defaultLang != null) {
            // Update language file
            try {
                FileConfiguration defaultLangConfig = new YamlConfiguration();
                defaultLangConfig.loadFromString(defaultLang);
                for (String key : defaultLangConfig.getConfigurationSection("messages").getKeys(false)) {
                    languageConfig.addDefaultLang(key, defaultLangConfig.getString("messages." + key));
                }
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        languageConfig.load();

        versionConfig.load();

        new Thread(() -> {
            ComparableVersion newestVersion = versionFetcher.fetchNewestVersion();
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

        // register bStats and sentry
        boolean isDevelopmentBuild = !versionConfig.getBuild().equalsIgnoreCase("undefined");

        Metrics metrics = new Metrics(this, 17543);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_npcs", () -> npcManager.getAllNpcs().size()));
        metrics.addCustomChart(new Metrics.SimplePie("update_notifications", () -> config.isMuteVersionNotification() ? "No" : "Yes"));
        metrics.addCustomChart(new Metrics.SimplePie("using_development_build", () -> isDevelopmentBuild ? "Yes" : "No"));

        PluginManager pluginManager = Bukkit.getPluginManager();
        usingPlotSquared = pluginManager.isPluginEnabled("PlotSquared");

        // register commands
        final Collection<Command> commands = Arrays.asList(new FancyNpcsCMD(), new NpcCMD());
        if (config.isRegisterCommands()) {
            commands.forEach(command -> getServer().getCommandMap().register("fancynpcs", command));
        } else {
            commands.stream().filter(Command::isRegistered).forEach(command ->
                    command.unregister(getServer().getCommandMap()));
        }

        // register listeners
        pluginManager.registerEvents(new PlayerJoinListener(), instance);
        pluginManager.registerEvents(new PlayerQuitListener(), instance);
        pluginManager.registerEvents(new PlayerTeleportListener(), instance);
        pluginManager.registerEvents(new PlayerChangedWorldListener(), instance);

        // use packet injection method
        switch (mcVersion) {
            case "1.19.4" -> pluginManager.registerEvents(new PacketReader_1_19_4(), instance);
            case "1.20" -> pluginManager.registerEvents(new PacketReader_1_20(), instance);
            default -> pluginManager.registerEvents(new PlayerUseUnknownEntityListener(), instance);
        }

        if (PLAYER_NPCS_FEATURE_FLAG.isEnabled()) {
            pluginManager.registerEvents(new PlayerNpcsListener(), instance);
        }

        // using bungee plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // load config
        scheduler.runTaskLater(null, 20L * 5, () -> npcManager.loadNpcs());

        visibilityTracker = new VisibilityTracker();

        scheduler.runTaskTimerAsynchronously(0, 1, new TurnToPlayerTracker());
        scheduler.runTaskTimerAsynchronously(0, 20, visibilityTracker);

        int autosaveInterval = config.getAutoSaveInterval();
        if (config.isEnableAutoSave() && config.getAutoSaveInterval() > 0) {
            scheduler.runTaskTimerAsynchronously(60L * 20L, autosaveInterval * 60L * 20L, () -> npcManager.saveNpcs(false));
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
    public FancyScheduler getScheduler() {
        return scheduler;
    }

    public NpcManagerImpl getNpcManagerImpl() {
        return npcManager;
    }

    @Override
    public NpcManager getNpcManager() {
        return npcManager;
    }

    @Override
    public AttributeManagerImpl getAttributeManager() {
        return attributeManager;
    }

    @Override
    public FancyNpcsConfigImpl getFancyNpcConfig() {
        return config;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public VersionConfig getVersionConfig() {
        return versionConfig;
    }

    public Translator getTranslator() {
        return translator;
    }

    public TextConfig getTextConfig() {
        return textConfig;
    }

    public FeatureFlagConfig getFeatureFlagConfig() {
        return featureFlagConfig;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public VisibilityTracker getVisibilityTracker() {
        return visibilityTracker;
    }

    public boolean isUsingPlotSquared() {
        return usingPlotSquared;
    }

    @Override
    public JavaPlugin getPlugin() {
        return instance;
    }
}
