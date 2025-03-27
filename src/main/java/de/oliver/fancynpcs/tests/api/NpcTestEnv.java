package de.oliver.fancynpcs.tests.api;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

import static de.oliver.plugintests.Expectable.expect;

public class NpcTestEnv {

    public static Npc givenDefaultNpcIsCreated() {
        String name = "test-" + UUID.randomUUID().toString().substring(0, 8);
        UUID creator = UUID.randomUUID();
        Location location = new Location(Bukkit.getWorld("world"), 100, 100, -100);

        NpcData data = new NpcData(name, creator, location);
        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        expect(npc).toBeDefined();

        npc.create();
        expect(npc.getEntityId()).toBeGreaterThan(-1);

        return npc;
    }

    public static void givenNpcIsRegistered(Npc npc) {
        expect(npc).toBeDefined();

        FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
        expect(FancyNpcsPlugin.get().getNpcManager().getNpc(npc.getData().getName())).toBeDefined();
    }

    public static void givenNpcIsUnregistered(Npc npc) {
        expect(npc).toBeDefined();

        FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
        expect(FancyNpcsPlugin.get().getNpcManager().getNpc(npc.getData().getName())).toBeNull();
    }

}
