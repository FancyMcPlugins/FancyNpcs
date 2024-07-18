package de.oliver.fancynpcs.api.actions;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NpcAction {

    private final String name;

    public NpcAction(String name) {
        this.name = name;
    }

    public abstract void execute(@NotNull Npc npc, @Nullable Player player, @Nullable String value);

    public String getName() {
        return name;
    }

    public record NpcActionData(NpcAction action, String value) {
    }
}
