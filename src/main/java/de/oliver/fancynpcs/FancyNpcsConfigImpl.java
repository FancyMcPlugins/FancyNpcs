package de.oliver.fancynpcs;

import de.oliver.fancylib.ConfigHelper;
import de.oliver.fancynpcs.api.FancyNpcsConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

public class FancyNpcsConfigImpl implements FancyNpcsConfig {

    /**
     * Currently active/selected language.
     */
    private String language;

    /**
     * Whether invisible NPCs should not be sent to the player.
     */
    private boolean skipInvisibleNpcs;

    /**
     * Indicates whether interaction cooldown messages are disabled.
     */
    private boolean disabledInteractionCooldownMessage;

    /**
     * Indicates whether version notifications are muted.
     */
    private boolean muteVersionNotification;

    /**
     * Indicates whether autosave is enabled.
     */
    private boolean enableAutoSave;

    /**
     * The interval at which autosave is performed.
     */
    private int autoSaveInterval;

    /**
     * The interval at which the NPC is updated.
     * Only if the skin or displayName is a placeholder.
     */
    private int npcUpdateInterval;

    /**
     * Indicates whether commands should be registered.
     * <p>
     * This is useful for users who want to use the plugin's API only.
     */
    private boolean registerCommands;

    /**
     * The distance at which NPCs turn to the player.
     */
    private int turnToPlayerDistance;

    /**
     * The distance at which NPCs are visible.
     */
    private int visibilityDistance;

    /**
     * The delay in ticks to remove NPCs from the player list.
     * Increase this value if you have problems with skins not loading correctly when joining or switching worlds.
     */
    private int removeNpcsFromPlayerlistDelay;

    /**
     * The commands that are blocked for NPCs in the message.
     */
    private List<String> blockedCommands;

    /**
     * The maximum number of NPCs per permission. (for the 'player-npcs' feature flag only)
     */
    private Map<String, Integer> maxNpcsPerPermission;

    public void reload() {
        FancyNpcs.getInstance().reloadConfig();
        FileConfiguration config = FancyNpcs.getInstance().getConfig();

        language = (String) ConfigHelper.getOrDefault(config, "language", "default");
        config.setInlineComments("language", List.of("Language to use for translatable messages."));

        skipInvisibleNpcs = (boolean) ConfigHelper.getOrDefault(config, "skip_invisible_npcs", true);
        config.setInlineComments("skip_invisible_npcs", List.of("Whether invisible NPCs should not be sent to the player."));

        disabledInteractionCooldownMessage = (boolean) ConfigHelper.getOrDefault(config, "disable_interaction_cooldown_message", false);
        config.setInlineComments("disable_interaction_cooldown_message", List.of("Whether interaction cooldown messages are disabled."));

        muteVersionNotification = (boolean) ConfigHelper.getOrDefault(config, "mute_version_notification", false);
        config.setInlineComments("mute_version_notification", List.of("Whether version notifications are muted."));

        enableAutoSave = (boolean) ConfigHelper.getOrDefault(config, "enable_autosave", true);
        config.setInlineComments("enable_autosave", List.of("Whether autosave is enabled."));

        autoSaveInterval = (int) ConfigHelper.getOrDefault(config, "autosave_interval", 15);
        config.setInlineComments("autosave_interval", List.of("The interval at which autosave is performed in minutes."));

        npcUpdateInterval = (int) ConfigHelper.getOrDefault(config, "npc_update_interval", 30);
        config.setInlineComments("npc_update_skin_interval", List.of("The interval at which the NPC is updated (in minutes). Only if the skin or displayName is a placeholder."));

        registerCommands = (boolean) ConfigHelper.getOrDefault(config, "register_commands", true);
        config.setInlineComments("register_commands", List.of("Whether the plugin should register its commands."));

        turnToPlayerDistance = (int) ConfigHelper.getOrDefault(config, "turn_to_player_distance", 5);
        config.setInlineComments("turn_to_player_distance", List.of("The distance at which NPCs turn to the player."));

        visibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);
        config.setInlineComments("visibility_distance", List.of("The distance at which NPCs are visible."));

        removeNpcsFromPlayerlistDelay = (int) ConfigHelper.getOrDefault(config, "remove_npcs_from_playerlist_delay", 2000);
        config.setInlineComments("remove_npcs_from_playerlist_delay", List.of("The delay in ticks to remove NPCs from the player list. Increase this value if you have problems with skins not loading correctly when joining or switching worlds. You can set it to -1, if you don't have any npcs using the show_in_tab feature."));

        blockedCommands = (List<String>) ConfigHelper.getOrDefault(config, "blocked_commands", Arrays.asList("op", "ban"));
        config.setInlineComments("blocked_commands", List.of("The commands that are blocked for NPCs in the message."));


        if (!config.isSet("max-npcs")) {
            List<Map<String, Integer>> entries = new ArrayList<>();
            entries.add(Map.of("fancynpcs.max-npcs.5", 5));
            entries.add(Map.of("fancynpcs.max-npcs.10", 10));
            config.set("max-npcs", entries);
            config.setInlineComments("max-npcs", List.of("The maximum number of NPCs per permission. (for the 'player-npcs' feature flag only)"));
            this.maxNpcsPerPermission = new HashMap<>();
            this.maxNpcsPerPermission.put("fancynpcs.max-npcs.5", 5);
            this.maxNpcsPerPermission.put("fancynpcs.max-npcs.10", 10);
        } else {
            this.maxNpcsPerPermission = config.getMapList("max-npcs").stream()
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (Integer) entry.getValue()));
        }

        FancyNpcs.getInstance().saveConfig();
    }

    public String getLanguage() {
        return language;
    }

    public boolean isSkipInvisibleNpcs() {
        return skipInvisibleNpcs;
    }

    public boolean isInteractionCooldownMessageDisabled() {
        return disabledInteractionCooldownMessage;
    }

    public boolean isMuteVersionNotification() {
        return muteVersionNotification;
    }

    public boolean isEnableAutoSave() {
        return enableAutoSave;
    }

    public int getAutoSaveInterval() {
        return autoSaveInterval;
    }

    public int getNpcUpdateInterval() {
        return npcUpdateInterval;
    }

    public boolean isRegisterCommands() {
        return registerCommands;
    }

    public int getTurnToPlayerDistance() {
        return turnToPlayerDistance;
    }

    public int getVisibilityDistance() {
        return visibilityDistance;
    }

    public int getRemoveNpcsFromPlayerlistDelay() {
        return removeNpcsFromPlayerlistDelay;
    }

    public List<String> getBlockedCommands() {
        return blockedCommands;
    }

    public Map<String, Integer> getMaxNpcsPerPermission() {
        return maxNpcsPerPermission;
    }
}
