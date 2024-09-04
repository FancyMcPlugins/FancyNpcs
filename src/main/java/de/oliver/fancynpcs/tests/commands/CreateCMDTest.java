package de.oliver.fancynpcs.tests.commands;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.tests.annotations.FNAfterEach;
import de.oliver.fancynpcs.tests.annotations.FNBeforeEach;
import de.oliver.fancynpcs.tests.annotations.FNTest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

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
        if (NPC_MANAGER.getNpc(npcName) != null) {
            throw new IllegalStateException("Npc was not removed");
        }

        createdNpc = null;
        npcName = null;
    }

    @FNTest(name = "Create npc")
    public void createNpc(Player player) {
        if (!player.performCommand("npc create " + npcName)) {
            throw new IllegalStateException("Command failed");
        }

        createdNpc = NPC_MANAGER.getNpc(npcName);
        if (createdNpc == null) {
            throw new IllegalStateException("Npc was not created");
        }

        if (createdNpc.getEntityId() < 0) {
            throw new IllegalStateException("Npc entity was not created");
        }
    }

    @FNTest(name = "Create npc with type")
    public void createNpcWithType(Player player) {
        if (!player.performCommand("npc create " + npcName + " --type PIG")) {
            throw new IllegalStateException("Command failed");
        }

        createdNpc = NPC_MANAGER.getNpc(npcName);
        if (createdNpc == null) {
            throw new IllegalStateException("Npc was not created");
        }

        if (createdNpc.getEntityId() < 0) {
            throw new IllegalStateException("Npc entity was not created");
        }

        if (createdNpc.getData().getType() != EntityType.PIG) {
            throw new IllegalStateException("Npc entity type is not correct");
        }
    }
}
