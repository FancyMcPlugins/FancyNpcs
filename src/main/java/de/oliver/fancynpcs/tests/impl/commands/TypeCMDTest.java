package de.oliver.fancynpcs.tests.impl.commands;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.tests.annotations.FNAfterEach;
import de.oliver.fancynpcs.tests.annotations.FNBeforeEach;
import de.oliver.fancynpcs.tests.annotations.FNTest;
import de.oliver.fancynpcs.tests.impl.api.NpcTestEnv;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import static de.oliver.fancynpcs.tests.Expectable.expect;

public class TypeCMDTest {

    private Npc npc;
    private String npcName;

    @FNBeforeEach
    public void setUp(Player player) {
        npc = NpcTestEnv.givenDefaultNpcIsCreated();
        npcName = npc.getData().getName();

        NpcTestEnv.givenNpcIsRegistered(npc);
    }

    @FNAfterEach
    public void tearDown(Player player) {
        NpcTestEnv.givenNpcIsUnregistered(npc);

        npc = null;
        npcName = null;
    }

    @FNTest(name = "Set type to COW")
    public void setTypeToCow(Player player) {
        expect(player.performCommand("npc type " + npcName + " COW")).toBe(true);
        expect(npc.getData().getType()).toBe(EntityType.COW);
    }

    @FNTest(name = "Set type to COW with showInTab")
    public void setTypeToCowWithShowInTab(Player player) {
        npc.getData().setShowInTab(true);
        expect(player.performCommand("npc type " + npcName + " COW")).toBe(true);

        expect(npc.getData().getType()).toBe(EntityType.COW);
        expect(npc.getData().isShowInTab()).toBe(false);
    }
}
