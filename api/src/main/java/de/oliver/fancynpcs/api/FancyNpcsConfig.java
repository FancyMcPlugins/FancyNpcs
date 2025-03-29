package de.oliver.fancynpcs.api;

import java.util.List;
import java.util.Map;

public interface FancyNpcsConfig {

    boolean isSkipInvisibleNpcs();

    boolean isInteractionCooldownMessageDisabled();

    boolean isMuteVersionNotification();

    boolean isEnableAutoSave();

    int getAutoSaveInterval();

    int getNpcUpdateInterval();

    int getNpcUpdateVisibilityInterval();

    int getTurnToPlayerDistance();
    
    /**
     * Sets the distance at which NPCs turn to the player.
     * 
     * @param distance The new distance value
     * @return true if the distance was updated successfully, false otherwise
     */
    boolean setTurnToPlayerDistance(int distance);

    boolean isTurnToPlayerResetToInitialDirection();

    int getVisibilityDistance();

    int getRemoveNpcsFromPlayerlistDelay();

    String getMineSkinApiKey();

    List<String> getBlockedCommands();

    Map<String, Integer> getMaxNpcsPerPermission();

}
