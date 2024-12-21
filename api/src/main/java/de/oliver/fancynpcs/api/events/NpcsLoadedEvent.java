package de.oliver.fancynpcs.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;

/**
 * Is fired when all NPCs are loaded.
 *
 * Will be removed, once the npc loading is coupled with the loading of worlds! Be aware of that!
 */
@ApiStatus.Experimental()
public class NpcsLoadedEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
