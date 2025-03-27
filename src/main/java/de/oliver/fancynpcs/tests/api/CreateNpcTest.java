package de.oliver.fancynpcs.tests.api;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.plugintests.annotations.FPAfterEach;
import de.oliver.plugintests.annotations.FPBeforeEach;
import de.oliver.plugintests.annotations.FPTest;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

import static de.oliver.plugintests.Expectable.expect;

public class CreateNpcTest {

    private static final NpcManager NPC_MANAGER = FancyNpcsPlugin.get().getNpcManager();

    private String npcName;
    private UUID creatorUUID;
    private Location location;

    private Npc createdNpc;

    @FPBeforeEach
    public void setUp(Player player) {
        npcName = "test-" + UUID.randomUUID().toString().substring(0, 8);
        creatorUUID = player.getUniqueId();
        location = player.getLocation().clone();
        createdNpc = null;
    }

    @FPAfterEach
    public void tearDown(Player player) {
        if (createdNpc != null) {
            NPC_MANAGER.removeNpc(createdNpc);
        }

        expect(NPC_MANAGER.getNpc(npcName)).toBeNull();

        createdNpc = null;
        npcName = null;
        creatorUUID = null;
        location = null;
    }

    @FPTest(name = "Create and register npc")
    public void createAndRegisterNpc(Player player) {
        NpcData data = new NpcData(npcName, creatorUUID, location);
        createdNpc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        expect(createdNpc).toBeDefined();

        createdNpc.create();
        expect(createdNpc.getEntityId()).toBeGreaterThan(-1);
        expect(createdNpc.getData().getName()).toEqual(npcName);
        expect(createdNpc.getData().getCreator()).toEqual(creatorUUID);
        expect(createdNpc.getData().getLocation()).toEqual(location);
        expect(createdNpc.getData().getType()).toEqual(EntityType.PLAYER);

        NPC_MANAGER.registerNpc(createdNpc);
        expect(NPC_MANAGER.getNpc(npcName)).toBeDefined();
    }

}
