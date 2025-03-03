package de.oliver.fancynpcs.api.skins;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event that is called when a skin is generated
 */
public class SkinGeneratedEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    @NotNull
    private final String id;

    @Nullable
    private final SkinData skin;

    public SkinGeneratedEvent(@NotNull String id, @Nullable SkinData skin) {
        super(!Bukkit.isPrimaryThread());
        this.id = id;
        this.skin = skin;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull String getId() {
        return id;
    }

    /**
     * Get the skin that was generated
     *
     * @return the skin that was generated or null if the skin could not be generated
     */
    public @Nullable SkinData getSkin() {
        return skin;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
