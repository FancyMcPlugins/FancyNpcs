package de.oliver.fancynpcs.api;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.UUID;

public interface NpcManager {

    void registerNpc(Npc npc);

    void removeNpc(Npc npc);

    @ApiStatus.Internal
    Npc getNpc(int entityId);

    Npc getNpc(String name);

    Npc getNpcById(String id);

    Npc getNpc(String name, UUID creator);

    Collection<Npc> getAllNpcs();

    void saveNpcs(boolean force);

    void loadNpcs();

    void reloadNpcs();

}
