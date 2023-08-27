package de.oliver.fancynpcs;

import de.oliver.fancylib.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class FancyNpcConfig {
    private boolean muteVersionNotification;
    private boolean enableAutoSave;
    private int autoSaveInterval;
    private int turnToPlayerDistance;
    private int visibilityDistance;
    private List<String> blockedCommands;
    private int maxNpcsPerPlayer;

    public void reload() {
        FancyNpcs.getInstance().reloadConfig();
        FileConfiguration config = FancyNpcs.getInstance().getConfig();

        muteVersionNotification = (boolean) ConfigHelper.getOrDefault(config, "mute_version_notification", false);
        enableAutoSave = (boolean) ConfigHelper.getOrDefault(config, "enable_autosave", true);
        autoSaveInterval = (int) ConfigHelper.getOrDefault(config, "autosave_interval", 15);
        turnToPlayerDistance = (int) ConfigHelper.getOrDefault(config, "turn_to_player_distance", 5);
        visibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);
        blockedCommands = (List<String>) ConfigHelper.getOrDefault(config, "blocked_commands", Arrays.asList("op", "ban"));
        maxNpcsPerPlayer = (int) ConfigHelper.getOrDefault(config, "max_npcs_per_player", -1);

        FancyNpcs.getInstance().saveConfig();
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

    public int getTurnToPlayerDistance() {
        return turnToPlayerDistance;
    }

    public int getVisibilityDistance() {
        return visibilityDistance;
    }

    public List<String> getBlockedCommands() {
        return blockedCommands;
    }

    public int getMaxNpcsPerPlayer() {
        return maxNpcsPerPlayer == -1 ? Integer.MAX_VALUE : maxNpcsPerPlayer;
    }
}
