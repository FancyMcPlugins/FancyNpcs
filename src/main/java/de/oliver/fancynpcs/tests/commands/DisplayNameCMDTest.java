package de.oliver.fancynpcs.tests.commands;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.tests.api.NpcTestEnv;
import de.oliver.plugintests.annotations.FPAfterEach;
import de.oliver.plugintests.annotations.FPBeforeEach;
import de.oliver.plugintests.annotations.FPTest;
import org.bukkit.entity.Player;

import java.util.List;

import static de.oliver.plugintests.Expectable.expect;

public class DisplayNameCMDTest {

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

    @FPTest(name = "Set display name")
    public void setDisplayName(Player player) {
        String displayName = "<red>Test Display Name";
        expect(player.performCommand("npc displayname " + npcName + " " + displayName)).toBe(true);
        expect(npc.getData().getDisplayName()).toEqual(displayName);
    }

    @FPTest(name = "Set display name to none")
    public void setDisplayNameToNone(Player player) {
        expect(player.performCommand("npc displayname " + npcName + " @none")).toBe(true);
        expect(npc.getData().getDisplayName()).toEqual("<empty>");
    }

    @FPTest(name = "Set display name to empty")
    public void setDisplayNameToEmpty(Player player) {
        expect(player.performCommand("npc displayname " + npcName + " <empty>")).toBe(true);
        expect(npc.getData().getDisplayName()).toEqual("<empty>");
    }

    @FPTest(name = "Set display name with blocked command")
    public void setDisplayNameWithBlockedCommand(Player player) {
        List<String> blockedCommands = FancyNpcs.getInstance().getFancyNpcConfig().getBlockedCommands();
        if (blockedCommands.isEmpty()) {
            return;
        }
        String blockedCommand = blockedCommands.get(0);

        expect(player.performCommand("npc displayname " + npcName + " <click:run_command:'/" + blockedCommand + "'>hello</click>")).toBe(true);

        expect(npc.getData().getDisplayName()).toEqual(npcName);
    }

}
