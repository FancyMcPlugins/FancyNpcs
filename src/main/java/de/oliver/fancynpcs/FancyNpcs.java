package de.oliver.fancynpcs;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyanalytics.api.FancyAnalyticsAPI;
import de.oliver.fancyanalytics.api.events.Event;
import de.oliver.fancyanalytics.api.metrics.MetricSupplier;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.Metrics;
import de.oliver.fancylib.VersionConfig;
import de.oliver.fancylib.featureFlags.FeatureFlag;
import de.oliver.fancylib.featureFlags.FeatureFlagConfig;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FoliaScheduler;
import de.oliver.fancylib.translations.Language;
import de.oliver.fancylib.translations.TextConfig;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.versionFetcher.MasterVersionFetcher;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.api.actions.types.*;
import de.oliver.fancynpcs.api.utils.SkinCache;
import de.oliver.fancynpcs.api.utils.SkinFetcher;
import de.oliver.fancynpcs.commands.CloudCommandManager;
import de.oliver.fancynpcs.listeners.*;
import de.oliver.fancynpcs.tracker.TurnToPlayerTracker;
import de.oliver.fancynpcs.tracker.VisibilityTracker;
import de.oliver.fancynpcs.utils.SkinCacheYaml;
import de.oliver.fancynpcs.v1_19_4.Npc_1_19_4;
import de.oliver.fancynpcs.v1_19_4.PacketReader_1_19_4;
import de.oliver.fancynpcs.v1_20.PacketReader_1_20;
import de.oliver.fancynpcs.v1_20_1.Npc_1_20_1;
import de.oliver.fancynpcs.v1_20_2.Npc_1_20_2;
import de.oliver.fancynpcs.v1_20_4.Npc_1_20_4;
import de.oliver.fancynpcs.v1_20_6.Npc_1_20_6;
import de.oliver.fancynpcs.v1_21_1.Npc_1_21_1;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class FancyNpcs extends JavaPlugin implements FancyNpcsPlugin {

    public static final FeatureFlag PLAYER_NPCS_FEATURE_FLAG = new FeatureFlag("player-npcs", "Every player can only manage the npcs they have created", false);

    private static FancyNpcs instance;
    private final ExtendedFancyLogger fancyLogger;
    private final ScheduledExecutorService npcThread;
    private final FancyScheduler scheduler;
    private final FancyNpcsConfigImpl config;
    private final VersionConfig versionConfig;
    private final FeatureFlagConfig featureFlagConfig;
    private final VersionFetcher versionFetcher;
    private final FancyAnalyticsAPI fancyAnalytics;
    private CloudCommandManager commandManager;
    private TextConfig textConfig;
    private Translator translator;
    private Function<NpcData, Npc> npcAdapter;
    private NpcManagerImpl npcManager;
    private AttributeManagerImpl attributeManager;
    private SkinCacheYaml skinCache;
    private ActionManagerImpl actionManager;
    private VisibilityTracker visibilityTracker;
    private boolean usingPlotSquared;

    public FancyNpcs() {
        instance = this;

        Appender consoleAppender = new ConsoleAppender("[{loggerName}] ({threadName}) {logLevel}: {message}");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        File logsFile = new File("plugins/FancyNpcs/logs/FN-logs-" + date + ".txt");
        if (!logsFile.exists()) {
            try {
                logsFile.getParentFile().mkdirs();
                logsFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JsonAppender jsonAppender = new JsonAppender(false, false, true, logsFile.getPath());
        this.fancyLogger = new ExtendedFancyLogger("FancyNpcs", LogLevel.INFO, List.of(consoleAppender, jsonAppender), new ArrayList<>());

        this.npcThread = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder()
                        .setNameFormat("FancyNpcs-Npcs")
                        .build()
        );
        this.scheduler = ServerSoftware.isFolia()
                ? new FoliaScheduler(instance)
                : new BukkitScheduler(instance);
        this.config = new FancyNpcsConfigImpl();
        this.versionFetcher = new MasterVersionFetcher(getName());
        this.versionConfig = new VersionConfig(this, versionFetcher);

        fancyAnalytics = new FancyAnalyticsAPI("ca2baf32-1fd2-4baa-a38a-f12ed8ab24a4", "Y7EP2jJjYWExZjdmMDkwNTQ5ZmRbIGUI");
        fancyAnalytics.getConfig().setDisableLogging(true);

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
            case "1.21", "1.21.1" -> npcAdapter = Npc_1_21_1::new;
            case "1.20.5", "1.20.6" -> npcAdapter = Npc_1_20_6::new;
            case "1.20.3", "1.20.4" -> npcAdapter = Npc_1_20_4::new;
            case "1.20.2" -> npcAdapter = Npc_1_20_2::new;
            case "1.20.1", "1.20" -> npcAdapter = Npc_1_20_1::new;
            case "1.19.4" -> npcAdapter = Npc_1_19_4::new;
            default -> npcAdapter = null;
        }

        npcManager = new NpcManagerImpl(this, npcAdapter);

        PluginManager pluginManager = Bukkit.getPluginManager();

        if (npcAdapter == null) {
            fancyAnalytics.sendEvent(new Event("pluginLoadingWithUnsupportedVersion")
                    .withProperty("version", mcVersion)
                    .withProperty("pluginVersion", getPluginMeta().getVersion())
            );

            fancyLogger.error("Unsupported minecraft server version.");
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("This plugin only supports 1.19.4 - 1.21.1");
            getLogger().warning("Disabling the FancyNpcs plugin.");
            getLogger().warning("--------------------------------------------------");
            pluginManager.disablePlugin(this);
            return;
        }
    }

    @Override
    public void onEnable() {
        if (npcAdapter == null) {
            return;
        }

        new FancyLib(instance); // Initialize FancyLib

        String mcVersion = Bukkit.getMinecraftVersion();

        config.reload();

        attributeManager = new AttributeManagerImpl();
        actionManager = new ActionManagerImpl();
        actionManager.registerAction(new MessageAction());
        actionManager.registerAction(new PlayerCommandAction());
        actionManager.registerAction(new PlayerCommandAsOpAction());
        actionManager.registerAction(new ConsoleCommandAction());
        actionManager.registerAction(new SendToServerAction());
        actionManager.registerAction(new WaitAction());
        actionManager.registerAction(new ExecuteRandomActionAction());
        actionManager.registerAction(new BlockUntilDoneAction());

        skinCache = new SkinCacheYaml();
        skinCache.loadAndInsertToSkinFetcher();

        textConfig = new TextConfig("#E33239", "#AD1D23", "#81E366", "#E3CA66", "#E36666", "");
        translator = new Translator(textConfig);
        translator.loadLanguages(getDataFolder().getAbsolutePath());
        final Language selectedLanguage = translator.getLanguages().stream()
                .filter(language -> language.getLanguageName().equals(config.getLanguage()))
                .findFirst().orElse(translator.getFallbackLanguage());
        translator.setSelectedLanguage(selectedLanguage);

        versionConfig.load();

        final ComparableVersion currentVersion = new ComparableVersion(versionConfig.getVersion());
        supplyAsync(getVersionFetcher()::fetchNewestVersion)
                .thenApply(Objects::requireNonNull)
                .whenComplete((newest, error) -> {
                    if (error != null || newest.compareTo(currentVersion) <= 0) {
                        return; // could not get the newest version or already on latest
                    }

                    fancyLogger.warn("You are not using the latest version of the FancyNpcs plugin.");
                    getLogger().warning("""
                            
                            -------------------------------------------------------
                            You are not using the latest version of the FancyNpcs plugin.
                            Please update to the newest version (%s).
                            %s
                            -------------------------------------------------------
                            """.formatted(newest, getVersionFetcher().getDownloadUrl()));
                });

        if (!ServerSoftware.isPaper()) {
            fancyLogger.warn("You are not using Paper as server software.");
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("It is recommended to use Paper as server software.");
            getLogger().warning("Because you are not using paper, the plugin");
            getLogger().warning("might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        registerMetrics();
        checkIfPluginVersionUpdated();

        PluginManager pluginManager = Bukkit.getPluginManager();
        usingPlotSquared = pluginManager.isPluginEnabled("PlotSquared");

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

        npcThread.scheduleAtFixedRate(new TurnToPlayerTracker(), 0, 50, TimeUnit.MILLISECONDS);
        npcThread.scheduleAtFixedRate(visibilityTracker, 0, 1, TimeUnit.SECONDS);

        int autosaveInterval = config.getAutoSaveInterval();
        if (config.isEnableAutoSave() && config.getAutoSaveInterval() > 0) {
            scheduler.runTaskTimerAsynchronously(60L * 20L, autosaveInterval * 60L * 20L, () -> npcManager.saveNpcs(false));
        }

        int npcUpdateInterval = config.getNpcUpdateInterval();
        npcThread.scheduleAtFixedRate(() -> {
            List<Npc> npcs = new ArrayList<>(npcManager.getAllNpcs());
            for (Npc npc : npcs) {
                boolean skinUpdated = npc.getData().getSkin() != null &&
                        !npc.getData().getSkin().identifier().isEmpty() &&
                        SkinFetcher.isPlaceholder(npc.getData().getSkin().identifier());

                boolean displayNameUpdated = npc.getData().getDisplayName() != null &&
                        !npc.getData().getDisplayName().isEmpty() &&
                        SkinFetcher.isPlaceholder(npc.getData().getDisplayName());

                if (skinUpdated || displayNameUpdated) {
                    npc.removeForAll();
                    npc.create();
                    npc.spawnForAll();
                }
            }
        }, 3, npcUpdateInterval, TimeUnit.MINUTES);

        // Creating new instance of CloudCommandManager and registering all needed components.
        // NOTE: Brigadier is disabled by default. More detailed information about that can be found in CloudCommandManager class.
        commandManager = new CloudCommandManager(this, false)
                .registerArguments()
                .registerExceptionHandlers()
                .registerCommands();

        fancyLogger.info("FancyNpcs (" + versionConfig.getVersion() + ") has been enabled.");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        if (npcManager != null) {
            npcManager.saveNpcs(true);
        }

        fancyLogger.info("FancyNpcs has been disabled.");
    }

    private void registerMetrics() {
        boolean isDevelopmentBuild = !versionConfig.getBuild().equalsIgnoreCase("undefined");

        Metrics metrics = new Metrics(this, 17543);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_npcs", () -> npcManager.getAllNpcs().size()));
        metrics.addCustomChart(new Metrics.SimplePie("update_notifications", () -> config.isMuteVersionNotification() ? "No" : "Yes"));
        metrics.addCustomChart(new Metrics.SimplePie("using_development_build", () -> isDevelopmentBuild ? "Yes" : "No"));

        int randomRes = new Random(System.currentTimeMillis()).nextInt(100);
        if (isDevelopmentBuild || randomRes < 30) {
            fancyAnalytics.registerMinecraftPluginMetrics(instance);
            fancyAnalytics.getExceptionHandler().registerLogger(getLogger());
            fancyAnalytics.getExceptionHandler().registerLogger(Bukkit.getLogger());

            fancyAnalytics.registerStringMetric(new MetricSupplier<>("commit_hash", () -> versionConfig.getHash().substring(0, 7)));

            fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_npcs", () -> (double) npcManager.getAllNpcs().size()));
            fancyAnalytics.registerStringMetric(new MetricSupplier<>("enabled_update_notifications", () -> config.isMuteVersionNotification() ? "false" : "true"));
            fancyAnalytics.registerStringMetric(new MetricSupplier<>("enabled_player_npcs_fflag", () -> PLAYER_NPCS_FEATURE_FLAG.isEnabled() ? "true" : "false"));
            fancyAnalytics.registerStringMetric(new MetricSupplier<>("using_development_build", () -> isDevelopmentBuild ? "true" : "false"));
            fancyAnalytics.registerStringMetric(new MetricSupplier<>("language", () -> translator.getSelectedLanguage().getLanguageCode()));

            fancyAnalytics.registerNumberMetric(new MetricSupplier<>("avg_interaction_cooldown", () -> {
                double sum = 0;
                int count = 0;
                for (Npc npc : npcManager.getAllNpcs()) {
                    if (npc.getData().getInteractionCooldown() > 0) {
                        sum += npc.getData().getInteractionCooldown();
                        count++;
                    }
                }

                if (count == 0) {
                    return 0.0;
                }

                return sum / count;
            }));

            fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_npcs_interaction_cooldown_longer_than_5min", () -> {
                long count = npcManager.getAllNpcs().stream()
                        .filter(npc -> npc.getData().getInteractionCooldown() > 300)
                        .count();

                return (double) count;
            }));

            fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_non_persistent_npcs", () -> {
                long count = npcManager.getAllNpcs().stream()
                        .filter(npc -> !npc.isSaveToFile())
                        .count();

                return (double) count;
            }));

            fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_not_player_npcs", () -> {
                long count = npcManager.getAllNpcs().stream()
                        .filter(npc -> npc.getData().getType() != EntityType.PLAYER)
                        .count();

                return (double) count;
            }));

            fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_npcs_having_attributes", () -> {
                long count = npcManager.getAllNpcs().stream()
                        .filter(npc -> !npc.getData().getAttributes().isEmpty())
                        .count();

                return (double) count;
            }));

            fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_npc_actions", () -> {
                long count = 0;

                for (Npc npc : npcManager.getAllNpcs()) {
                    count += npc.getData().getActions().values().size();
                }

                return (double) count;
            }));


            fancyAnalytics.initialize();
        }
    }

    private void checkIfPluginVersionUpdated() {
        String currentVersion = versionConfig.getVersion();
        String lastVersion = "N/A";

        File versionFile = new File(getDataFolder(), "version.yml");
        if (!versionFile.exists()) {
            try {
                Files.write(versionFile.toPath(), currentVersion.getBytes());
            } catch (IOException e) {
                fancyLogger.warn("Could not write version file.");
                return;
            }
        } else {
            try {
                lastVersion = new String(Files.readAllBytes(versionFile.toPath()));
            } catch (IOException e) {
                fancyLogger.warn("Could not read version file.");
                return;
            }
        }

        if (!lastVersion.equals(currentVersion)) {
            fancyLogger.info("Plugin has been updated from version " + lastVersion + " to " + currentVersion + ".");
            fancyAnalytics.sendEvent(
                    new Event("PluginVersionUpdated")
                            .withProperty("from", lastVersion)
                            .withProperty("to", currentVersion)
            );

            try {
                Files.write(versionFile.toPath(), currentVersion.getBytes());
            } catch (IOException e) {
                fancyLogger.warn("Could not write version file.");
            }
        }
    }

    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    @Override
    public ScheduledExecutorService getNpcThread() {
        return npcThread;
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

    public SkinCacheYaml getSkinCacheYaml() {
        return skinCache;
    }

    @Override
    public SkinCache getSkinCache() {
        return skinCache;
    }

    @Override
    public ActionManagerImpl getActionManager() {
        return actionManager;
    }

    @Override
    public FancyNpcsConfigImpl getFancyNpcConfig() {
        return config;
    }

    public VersionConfig getVersionConfig() {
        return versionConfig;
    }

    public CloudCommandManager getCommandManager() {
        return commandManager;
    }

    @Override
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
