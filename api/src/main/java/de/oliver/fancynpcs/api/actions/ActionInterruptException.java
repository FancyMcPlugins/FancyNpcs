package de.oliver.fancynpcs.api.actions;

import de.oliver.fancynpcs.api.Npc;

public class ActionInterruptException extends RuntimeException {

    private final Npc npc;
    private final NpcAction action;

    public ActionInterruptException(Npc npc, NpcAction action) {
        super("An npc action was unexpectedly interrupted");
        this.npc = npc;
        this.action = action;
    }

    public Npc getNpc() {
        return npc;
    }

    public NpcAction getAction() {
        return action;
    }
}
