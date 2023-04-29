package de.oliver.fancynpcs;

import de.oliver.fancylib.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;

public class FancyNpcConfig {
    private boolean muteVersionNotification;
    private boolean enableAutoSave;
    private int turnToPlayerDistance;
    private int visibilityDistance;

    public void reload(){
        FancyNpcs.getInstance().reloadConfig();
        FileConfiguration config = FancyNpcs.getInstance().getConfig();

        muteVersionNotification = (boolean) ConfigHelper.getOrDefault(config, "mute_version_notification", false);
        enableAutoSave = (boolean) ConfigHelper.getOrDefault(config, "enable_autosave", true);
        turnToPlayerDistance = (int) ConfigHelper.getOrDefault(config, "turn_to_player_distance", 5);
        visibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);

        FancyNpcs.getInstance().saveConfig();
    }

    public boolean isMuteVersionNotification() {
        return muteVersionNotification;
    }

    public boolean isEnableAutoSave() {
        return enableAutoSave;
    }

    public int getTurnToPlayerDistance() {
        return turnToPlayerDistance;
    }

    public int getVisibilityDistance() {
        return visibilityDistance;
    }
}
