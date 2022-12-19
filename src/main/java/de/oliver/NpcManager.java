package de.oliver;

import java.util.HashMap;

public class NpcManager {

    private final HashMap<Integer, Npc> npcs; // entityId -> npc

    public NpcManager() {
        npcs = new HashMap<>();
    }

    public void registerNpc(Npc npc){
        npcs.put(npc.getNpc().getId(), npc);
    }

    public void removeNpc(Npc npc){
        npcs.remove(npc.getNpc().getId());
    }

    public Npc getNpc(int entityId){
        if(npcs.containsKey(entityId)){
            return npcs.get(entityId);
        }

        return null;
    }

}
