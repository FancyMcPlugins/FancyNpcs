package de.oliver.fancynpcs.api.actions;

import java.util.List;

public interface ActionManager {

    void registerAction(NpcAction action);

    NpcAction getActionByName(String name);

    void unregisterAction(NpcAction action);

    List<NpcAction> getAllActions();
}
