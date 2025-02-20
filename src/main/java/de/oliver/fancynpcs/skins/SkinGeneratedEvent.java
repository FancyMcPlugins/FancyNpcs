package de.oliver.fancynpcs.skins;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineskin.data.SkinInfo;

public class SkinGeneratedEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    @NotNull
    private final String id;

    @Nullable
    private final SkinInfo skin;

    public SkinGeneratedEvent(@NotNull String id, @Nullable SkinInfo skin) {
        this.id = id;
        this.skin = skin;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull String getId() {
        return id;
    }

    public @Nullable SkinInfo getSkin() {
        return skin;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
