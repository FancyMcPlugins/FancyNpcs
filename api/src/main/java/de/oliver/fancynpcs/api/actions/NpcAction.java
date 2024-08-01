package de.oliver.fancynpcs.api.actions;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NpcAction {

    private final String name;
    private final boolean requiresValue;

    public NpcAction(String name, boolean requiresValue) {
        this.name = name;
        this.requiresValue = requiresValue;
    }

    public abstract void execute(@NotNull Npc npc, @Nullable Player player, @Nullable String value);

    public String getName() {
        return name;
    }

    public boolean requiresValue() {
        return requiresValue;
    }

    public record NpcActionData(NpcAction action, String value) {
    }
}
