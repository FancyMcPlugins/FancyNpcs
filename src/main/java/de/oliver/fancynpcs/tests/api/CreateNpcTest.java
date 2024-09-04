package de.oliver.fancynpcs.tests.api;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.tests.annotations.FNAfterEach;
import de.oliver.fancynpcs.tests.annotations.FNBeforeEach;
import de.oliver.fancynpcs.tests.annotations.FNTest;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CreateNpcTest {

    private static final NpcManager NPC_MANAGER = FancyNpcsPlugin.get().getNpcManager();

    private String npcName;
    private UUID creatorUUID;
    private Location location;

    private Npc createdNpc;

    @FNBeforeEach
    public void setUp(Player player) {
        npcName = "test-" + UUID.randomUUID().toString().substring(0, 8);
        creatorUUID = player.getUniqueId();
        location = player.getLocation().clone();
        createdNpc = null;
    }

    @FNAfterEach
    public void tearDown(Player player) {
        NPC_MANAGER.removeNpc(createdNpc);
        if (NPC_MANAGER.getNpc(npcName) != null) {
            throw new IllegalStateException("Npc was not removed");
        }

        createdNpc = null;
        npcName = null;
        creatorUUID = null;
        location = null;
    }

    @FNTest(name = "Create and register npc")
    public void createAndRegisterNpc(Player player) {
        NpcData data = new NpcData(npcName, creatorUUID, location);
        createdNpc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);

        if (createdNpc == null) {
            throw new IllegalStateException("Npc was not created");
        }

        createdNpc.create();

        if (createdNpc.getEntityId() < 0) {
            throw new IllegalStateException("Npc was not created");
        }

        NPC_MANAGER.registerNpc(createdNpc);

        if (NPC_MANAGER.getNpc(npcName) == null) {
            throw new IllegalStateException("Npc was not created");
        }
    }

}
