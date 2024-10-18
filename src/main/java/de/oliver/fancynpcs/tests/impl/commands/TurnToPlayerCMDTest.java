package de.oliver.fancynpcs.tests.impl.commands;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.tests.annotations.FNAfterEach;
import de.oliver.fancynpcs.tests.annotations.FNBeforeEach;
import de.oliver.fancynpcs.tests.annotations.FNTest;
import de.oliver.fancynpcs.tests.impl.api.NpcTestEnv;
import org.bukkit.entity.Player;

import static de.oliver.fancynpcs.tests.Expectable.expect;

public class TurnToPlayerCMDTest {

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

    @FNTest(name = "Set turnToPlayer to true")
    public void setTurnToPlayerToTrue(Player player) {
        expect(player.performCommand("npc turn_to_player " + npcName + " true")).toBe(true);
        expect(npc.getData().isTurnToPlayer()).toBe(true);
    }

    @FNTest(name = "Set turnToPlayer to false")
    public void setTurnToPlayerToFalse(Player player) {
        npc.getData().setTurnToPlayer(true);

        expect(player.performCommand("npc turn_to_player " + npcName + " false")).toBe(true);
        expect(npc.getData().isTurnToPlayer()).toBe(false);
    }

}
