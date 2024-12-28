package de.oliver.fancynpcs.tests.commands;

import de.oliver.fancylib.tests.annotations.FPAfterEach;
import de.oliver.fancylib.tests.annotations.FPBeforeEach;
import de.oliver.fancylib.tests.annotations.FPTest;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.tests.api.NpcTestEnv;
import org.bukkit.entity.Player;

import static de.oliver.fancylib.tests.Expectable.expect;

public class TurnToPlayerCMDTest {

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

    @FPTest(name = "Set turnToPlayer to true")
    public void setTurnToPlayerToTrue(Player player) {
        expect(player.performCommand("npc turn_to_player " + npcName + " true")).toBe(true);
        expect(npc.getData().isTurnToPlayer()).toBe(true);
    }

    @FPTest(name = "Set turnToPlayer to false")
    public void setTurnToPlayerToFalse(Player player) {
        npc.getData().setTurnToPlayer(true);

        expect(player.performCommand("npc turn_to_player " + npcName + " false")).toBe(true);
        expect(npc.getData().isTurnToPlayer()).toBe(false);
    }

}