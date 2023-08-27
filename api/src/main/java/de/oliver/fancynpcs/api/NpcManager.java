package de.oliver.fancynpcs.api;

import java.util.Collection;
import java.util.UUID;

public interface NpcManager {

    void registerNpc(Npc npc);

    void removeNpc(Npc npc);

    Npc getNpc(String name);

    Npc getNpc(String name, UUID creator);

    Collection<Npc> getAllNpcs();

    void saveNpcs(boolean force);

    void loadNpcs();

    void reloadNpcs();

}
