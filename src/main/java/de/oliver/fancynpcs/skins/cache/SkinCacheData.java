package de.oliver.fancynpcs.skins.cache;

import de.oliver.fancynpcs.api.skins.SkinData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the cached skin data. For internal use only.
 *
 * @param skinData    The skin data.
 * @param lastUpdated The timestamp when the skin data was last updated.
 * @param timeToLive  The time to live of the skin data in milliseconds.
 */
@ApiStatus.Internal
public record SkinCacheData(@NotNull SkinData skinData, long lastUpdated, long timeToLive) {
    public boolean isExpired() {
        return timeToLive > 0 && System.currentTimeMillis() - lastUpdated > timeToLive;
    }
}
