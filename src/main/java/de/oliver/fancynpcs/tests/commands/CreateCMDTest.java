package de.oliver.fancynpcs.tests.commands;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.tests.annotations.FNAfterEach;
import de.oliver.fancynpcs.tests.annotations.FNBeforeEach;
import de.oliver.fancynpcs.tests.annotations.FNTest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import static de.oliver.fancynpcs.tests.FNTestUtils.*;

public class CreateCMDTest {

    private static final NpcManager NPC_MANAGER = FancyNpcsPlugin.get().getNpcManager();

    private String npcName;
    private Npc createdNpc;

    @FNBeforeEach
    public void setUp(Player player) {
        npcName = "test-" + player.getUniqueId().toString().substring(0, 8);
        createdNpc = null;
    }

    @FNAfterEach
    public void tearDown(Player player) {
        NPC_MANAGER.removeNpc(createdNpc);
        assertNull(NPC_MANAGER.getNpc(npcName));

        createdNpc = null;
        npcName = null;
    }

    @FNTest(name = "Create npc")
    public void createNpc(Player player) {
        if (!player.performCommand("npc create " + npcName)) {
            failTest("Command failed");
        }

        createdNpc = NPC_MANAGER.getNpc(npcName);
        assertNotNull(createdNpc);

        if (createdNpc.getEntityId() < 0) {
            failTest("Npc entity was not created");
        }
    }

    @FNTest(name = "Create npc with type")
    public void createNpcWithType(Player player) {
        if (!player.performCommand("npc create " + npcName + " --type PIG")) {
            failTest("Command failed");
        }

        createdNpc = NPC_MANAGER.getNpc(npcName);
        assertNotNull(createdNpc);

        if (createdNpc.getEntityId() < 0) {
            failTest("Npc entity was not created");
        }

        assertEqual(createdNpc.getData().getType(), EntityType.PIG);
    }
}
