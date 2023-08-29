package de.oliver.fancynpcs;

import de.oliver.fancylib.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

public class FancyNpcConfig {
    private boolean muteVersionNotification;
    private boolean enableAutoSave;
    private int autoSaveInterval;
    private int turnToPlayerDistance;
    private int visibilityDistance;
    private List<String> blockedCommands;
    private Map<String, Integer> maxNpcsPerPermission;

    public void reload() {
        FancyNpcs.getInstance().reloadConfig();
        FileConfiguration config = FancyNpcs.getInstance().getConfig();

        muteVersionNotification = (boolean) ConfigHelper.getOrDefault(config, "mute_version_notification", false);
        enableAutoSave = (boolean) ConfigHelper.getOrDefault(config, "enable_autosave", true);
        autoSaveInterval = (int) ConfigHelper.getOrDefault(config, "autosave_interval", 15);
        turnToPlayerDistance = (int) ConfigHelper.getOrDefault(config, "turn_to_player_distance", 5);
        visibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);
        blockedCommands = (List<String>) ConfigHelper.getOrDefault(config, "blocked_commands", Arrays.asList("op", "ban"));

        if (!config.isSet("max-npcs")) {
            List<Map<String, Integer>> entries = new ArrayList<>();
            entries.add(Map.of("fancynpcs.max-npcs.5", 5));
            entries.add(Map.of("fancynpcs.max-npcs.10", 10));
            config.set("max-npcs", entries);
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

    public Map<String, Integer> getMaxNpcsPerPermission() {
        return maxNpcsPerPermission;
    }
}
