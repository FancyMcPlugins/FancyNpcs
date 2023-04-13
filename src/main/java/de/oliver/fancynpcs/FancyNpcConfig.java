package de.oliver.fancynpcs;

import org.bukkit.configuration.file.FileConfiguration;

public class FancyNpcConfig {

    private final String prefix = "<color:#3b3f8c>[</color><gradient:#9666e3:#6696e3>FancyNpcs</gradient><color:#3b3f8c>]</color>";
    private final String primaryColor = "#6696e3";
    private final String successColor = "#81e366";
    private final String warningColor = "#e3ca66";
    private final String errorColor = "#e36666";

    private boolean muteVersionNotification;
    private boolean enableAutoSave;
    private int turnToPlayerDistance;
    private int visibilityDistance;

    public void reload(){
        FancyNpcs.getInstance().reloadConfig();
        FileConfiguration config = FancyNpcs.getInstance().getConfig();

        muteVersionNotification = (boolean) getOrDefault(config, "mute_version_notification", false);
        enableAutoSave = (boolean) getOrDefault(config, "enable_autosave", true);
        turnToPlayerDistance = (int) getOrDefault(config, "turn_to_player_distance", 5);
        visibilityDistance = (int) getOrDefault(config, "visibility_distance", 20);

        FancyNpcs.getInstance().saveConfig();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getSuccessColor() {
        return successColor;
    }

    public String getWarningColor() {
        return warningColor;
    }

    public String getErrorColor() {
        return errorColor;
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

    public static Object getOrDefault(FileConfiguration config, String path, Object defaultVal){
        if(!config.contains(path)){
            config.set(path, defaultVal);
            return defaultVal;
        }

        return config.get(path);
    }
}
