package de.oliver.fancynpcs.tests.commands;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.tests.api.NpcTestEnv;
import de.oliver.plugintests.annotations.FPAfterEach;
import de.oliver.plugintests.annotations.FPBeforeEach;
import de.oliver.plugintests.annotations.FPTest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import static de.oliver.plugintests.Expectable.expect;

public class TypeCMDTest {

    private Npc npc;
    private String npcName;

    @FPBeforeEach
    public void setUp(Player player) {
        npc = NpcTestEnv.givenDefaultNpcIsCreated();
        npcName = npc.getData().getName();

        NpcTestEnv.givenNpcIsRegistered(npc);
    }

    @FPAfterEach
    public void tearDown(Player player) {
        NpcTestEnv.givenNpcIsUnregistered(npc);

        npc = null;
        npcName = null;
    }

    @FPTest(name = "Set type to COW")
    public void setTypeToCow(Player player) {
        expect(player.performCommand("npc type " + npcName + " COW")).toBe(true);
        expect(npc.getData().getType()).toBe(EntityType.COW);
    }

    @FPTest(name = "Set type to COW with showInTab")
    public void setTypeToCowWithShowInTab(Player player) {
        npc.getData().setShowInTab(true);
        expect(player.performCommand("npc type " + npcName + " COW")).toBe(true);

        expect(npc.getData().getType()).toBe(EntityType.COW);
        expect(npc.getData().isShowInTab()).toBe(false);
    }
}
