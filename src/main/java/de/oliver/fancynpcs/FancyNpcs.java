package de.oliver.fancynpcs;

import de.oliver.fancyanalytics.api.FancyAnalyticsAPI;
import de.oliver.fancyanalytics.api.MetricSupplier;
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
import de.oliver.fancynpcs.commands.CloudCommandManager;
import de.oliver.fancynpcs.listeners.*;
import de.oliver.fancynpcs.tracker.TurnToPlayerTracker;
import de.oliver.fancynpcs.tracker.VisibilityTracker;
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

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class FancyNpcs extends JavaPlugin implements FancyNpcsPlugin {

    public static final String[] SUPPORTED_VERSIONS = new String[]{"1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21"};
    public static final FeatureFlag PLAYER_NPCS_FEATURE_FLAG = new FeatureFlag("player-npcs", "Every player can only manage the npcs they have created", false);
    public static final FeatureFlag USE_FANCYANALYTICS_FEATURE_FLAG = new FeatureFlag("use-fancyanalytics", "Use FancyAnalytics to report plugin usage and errors", false);

    private static FancyNpcs instance;
    private final FancyScheduler scheduler;
    private final FancyNpcsConfigImpl config;
    private final VersionConfig versionConfig;
    private final FeatureFlagConfig featureFlagConfig;
    private final VersionFetcher versionFetcher;
    private CloudCommandManager commandManager;
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
        featureFlagConfig.addFeatureFlag(USE_FANCYANALYTICS_FEATURE_FLAG);
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
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Please update the server to " + String.join(" / ", SUPPORTED_VERSIONS) + ".");
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

        FancyLib.setPlugin(instance, getFile());

        String mcVersion = Bukkit.getMinecraftVersion();

        config.reload();

        attributeManager = new AttributeManagerImpl();

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

                    getLogger().warning("""
                            
                            -------------------------------------------------------
                            You are not using the latest version the FancyNpcs plugin.
                            Please update to the newest version (%s).
                            %s
                            -------------------------------------------------------
                            """.formatted(newest, getVersionFetcher().getDownloadUrl()));
                });

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

        int randomRes = new Random(System.currentTimeMillis()).nextInt(100);
        if (USE_FANCYANALYTICS_FEATURE_FLAG.isEnabled() || isDevelopmentBuild || randomRes < 15) {
            FancyAnalyticsAPI.setDisableLogging(true);
            FancyAnalyticsAPI fancyAnalytics = new FancyAnalyticsAPI("34c5a33d-0ff0-48b1-8b1c-53620a690c6e", "ca2baf32-1fd2-4baa-a38a-f12ed8ab24a4", "Y7EP2jJjYWExZjdmMDkwNTQ5ZmRbIGUI");
            fancyAnalytics.registerDefaultPluginMetrics(instance);
            fancyAnalytics.registerLogger(getLogger());
            fancyAnalytics.registerLogger(Bukkit.getLogger());

            fancyAnalytics.registerStringMetric(new MetricSupplier<>("commit_hash", () -> versionConfig.getHash().substring(0, 7)));

            fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_npcs", () -> (double) npcManager.getAllNpcs().size()));
            fancyAnalytics.registerStringMetric(new MetricSupplier<>("enabled_update_notifications", () -> config.isMuteVersionNotification() ? "false" : "true"));
            fancyAnalytics.registerStringMetric(new MetricSupplier<>("enabled_player_npcs_fflag", () -> PLAYER_NPCS_FEATURE_FLAG.isEnabled() ? "true" : "false"));
            fancyAnalytics.registerStringMetric(new MetricSupplier<>("using_development_build", () -> isDevelopmentBuild ? "true" : "false"));
            fancyAnalytics.registerStringMetric(new MetricSupplier<>("language", selectedLanguage::getLanguageCode));

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


            fancyAnalytics.initialize();
        }

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

        scheduler.runTaskTimerAsynchronously(0, 1, new TurnToPlayerTracker());
        scheduler.runTaskTimerAsynchronously(0, 20, visibilityTracker);

        int autosaveInterval = config.getAutoSaveInterval();
        if (config.isEnableAutoSave() && config.getAutoSaveInterval() > 0) {
            scheduler.runTaskTimerAsynchronously(60L * 20L, autosaveInterval * 60L * 20L, () -> npcManager.saveNpcs(false));
        }
        // Creating new instance of CloudCommandManager and registering all needed components.
        // NOTE: Brigadier is disabled by default. More detailed information about that can be found in CloudCommandManager class.
        commandManager = new CloudCommandManager(this, false)
                .registerArguments()
                .registerExceptionHandlers()
                .registerCommands();
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
