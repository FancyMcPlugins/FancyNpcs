package de.oliver.fancynpcs.tests.impl.commands;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.tests.annotations.FNAfterEach;
import de.oliver.fancynpcs.tests.annotations.FNBeforeEach;
import de.oliver.fancynpcs.tests.annotations.FNTest;
import de.oliver.fancynpcs.tests.impl.api.NpcTestEnv;
import org.bukkit.entity.Player;

import java.util.List;

import static de.oliver.fancynpcs.tests.Expectable.expect;

public class DisplayNameCMDTest {

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

    @FNTest(name = "Set display name")
    public void setDisplayName(Player player) {
        String displayName = "<red>Test Display Name";
        expect(player.performCommand("npc displayname " + npcName + " " + displayName)).toBe(true);
        expect(npc.getData().getDisplayName()).toEqual(displayName);
    }

    @FNTest(name = "Set display name to none")
    public void setDisplayNameToNone(Player player) {
        expect(player.performCommand("npc displayname " + npcName + " @none")).toBe(true);
        expect(npc.getData().getDisplayName()).toEqual("<empty>");
    }

    @FNTest(name = "Set display name to empty")
    public void setDisplayNameToEmpty(Player player) {
        expect(player.performCommand("npc displayname " + npcName + " <empty>")).toBe(true);
        expect(npc.getData().getDisplayName()).toEqual("<empty>");
    }

    @FNTest(name = "Set display name with blocked command")
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
