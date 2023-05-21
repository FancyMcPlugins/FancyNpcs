package de.oliver.fancynpcs.api;

import java.util.Collection;

public interface NpcManager {

    void registerNpc(Npc npc);
    void removeNpc(Npc npc);
    Npc getNpc(int entityId);
    Npc getNpc(String name);
    Collection<Npc> getAllNpcs();

    void saveNpcs(boolean force);
    void loadNpcs();
    void reloadNpcs();

}
