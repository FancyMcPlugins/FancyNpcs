package de.oliver;

import org.bukkit.configuration.file.FileConfiguration;

public class FancyNpcConfig {

    private boolean muteVersionNotification;
    private int turnToPlayerDistance;
    private int visibilityDistance;


    public void reload(){
        FancyNpcs.getInstance().reloadConfig();
        FileConfiguration config = FancyNpcs.getInstance().getConfig();

        muteVersionNotification = (boolean) getOrDefault(config, "mute_version_notification", false);
        turnToPlayerDistance = (int) getOrDefault(config, "turn_to_player_distance", 5);
        visibilityDistance = (int) getOrDefault(config, "visibility_distance", 20);

        FancyNpcs.getInstance().saveConfig();
    }

    public boolean isMuteVersionNotification() {
        return muteVersionNotification;
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
