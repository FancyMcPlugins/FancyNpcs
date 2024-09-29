package de.oliver.fancynpcs.tests.impl.commands;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionManager;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.tests.annotations.FNAfterEach;
import de.oliver.fancynpcs.tests.annotations.FNBeforeEach;
import de.oliver.fancynpcs.tests.annotations.FNTest;
import de.oliver.fancynpcs.tests.impl.api.NpcTestEnv;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static de.oliver.fancynpcs.tests.Expectable.expect;

public class ActionCMDTest {

    private final ActionManager actionManager = FancyNpcs.getInstance().getActionManager();

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

    @FNTest(name = "Add action")
    public void addAction(Player player) {
        ActionTrigger actionTrigger = ActionTrigger.RIGHT_CLICK;
        String actionType = "player_command";
        String actionValue = "say Hello World!";

        expect(player.performCommand("npc action " + npcName + " " + actionTrigger + " add " + actionType + " " + actionValue)).toBe(true);

        expect(npc.getData().getActions(actionTrigger).size()).toEqual(1);
        expect(npc.getData().getActions(actionTrigger).getFirst().action().getName()).toEqual(actionType);
        expect(npc.getData().getActions(actionTrigger).getFirst().value()).toEqual(actionValue);
    }

    @FNTest(name = "Add action before")
    public void addActionBefore(Player player) {
        ActionTrigger actionTrigger = ActionTrigger.RIGHT_CLICK;
        NpcAction.NpcActionData existingAction = new NpcAction.NpcActionData(1, actionManager.getActionByName("player_command"), "say Hello World!");
        npc.getData().setActions(actionTrigger, new ArrayList<>(List.of(existingAction)));

        String actionType = "player_command";
        String actionValue = "say Hello World 2!";

        expect(player.performCommand("npc action " + npcName + " " + actionTrigger + " add_before 1 " + actionType + " " + actionValue)).toBe(true);

        expect(npc.getData().getActions(actionTrigger).size()).toEqual(2);
        expect(npc.getData().getActions(actionTrigger).getFirst().action().getName()).toEqual(actionType);
        expect(npc.getData().getActions(actionTrigger).getFirst().value()).toEqual(actionValue);
        expect(npc.getData().getActions(actionTrigger).getLast().action().getName()).toEqual(existingAction.action().getName());
        expect(npc.getData().getActions(actionTrigger).getLast().value()).toEqual(existingAction.value());
    }

    @FNTest(name = "Add action after")
    public void addActionAfter(Player player) {
        ActionTrigger actionTrigger = ActionTrigger.RIGHT_CLICK;
        NpcAction.NpcActionData existingAction = new NpcAction.NpcActionData(1, actionManager.getActionByName("player_command"), "say Hello World!");
        npc.getData().setActions(actionTrigger, new ArrayList<>(List.of(existingAction)));

        String actionType = "player_command";
        String actionValue = "say Hello World 2!";

        expect(player.performCommand("npc action " + npcName + " " + actionTrigger + " add_after 1 " + actionType + " " + actionValue)).toBe(true);

        expect(npc.getData().getActions(actionTrigger).size()).toEqual(2);
        expect(npc.getData().getActions(actionTrigger).getFirst().action().getName()).toEqual(existingAction.action().getName());
        expect(npc.getData().getActions(actionTrigger).getFirst().value()).toEqual(existingAction.value());
        expect(npc.getData().getActions(actionTrigger).getLast().action().getName()).toEqual(actionType);
        expect(npc.getData().getActions(actionTrigger).getLast().value()).toEqual(actionValue);
    }

    @FNTest(name = "Set action")
    public void setAction(Player player) {
        ActionTrigger actionTrigger = ActionTrigger.RIGHT_CLICK;
        NpcAction.NpcActionData existingAction = new NpcAction.NpcActionData(1, actionManager.getActionByName("player_command"), "say Hello World!");
        npc.getData().setActions(actionTrigger, new ArrayList<>(List.of(existingAction)));

        String actionType = "player_command";
        String actionValue = "say Hello World 2!";

        expect(player.performCommand("npc action " + npcName + " " + actionTrigger + " set 1 " + actionType + " " + actionValue)).toBe(true);

        expect(npc.getData().getActions(actionTrigger).size()).toEqual(1);
        expect(npc.getData().getActions(actionTrigger).getFirst().action().getName()).toEqual(actionType);
        expect(npc.getData().getActions(actionTrigger).getFirst().value()).toEqual(actionValue);
    }

    @FNTest(name = "Remove action")
    public void removeAction(Player player) {
        ActionTrigger actionTrigger = ActionTrigger.RIGHT_CLICK;
        NpcAction.NpcActionData existingAction = new NpcAction.NpcActionData(1, actionManager.getActionByName("player_command"), "say Hello World!");
        npc.getData().setActions(actionTrigger, new ArrayList<>(List.of(existingAction)));

        expect(player.performCommand("npc action " + npcName + " " + actionTrigger + " remove 1")).toBe(true);

        expect(npc.getData().getActions(actionTrigger).size()).toEqual(0);
    }

    @FNTest(name = "Move action up")
    public void moveActionUp(Player player) {
        ActionTrigger actionTrigger = ActionTrigger.RIGHT_CLICK;
        NpcAction.NpcActionData existingAction1 = new NpcAction.NpcActionData(1, actionManager.getActionByName("player_command"), "say Hello World!");
        NpcAction.NpcActionData existingAction2 = new NpcAction.NpcActionData(2, actionManager.getActionByName("player_command"), "say Hello World 2!");
        npc.getData().setActions(actionTrigger, new ArrayList<>(List.of(existingAction1, existingAction2)));

        expect(player.performCommand("npc action " + npcName + " " + actionTrigger + " move_up 2")).toBe(true);

        expect(npc.getData().getActions(actionTrigger).size()).toEqual(2);
        expect(npc.getData().getActions(actionTrigger).getFirst().action().getName()).toEqual(existingAction2.action().getName());
        expect(npc.getData().getActions(actionTrigger).getFirst().value()).toEqual(existingAction2.value());
        expect(npc.getData().getActions(actionTrigger).getLast().action().getName()).toEqual(existingAction1.action().getName());
        expect(npc.getData().getActions(actionTrigger).getLast().value()).toEqual(existingAction1.value());
    }

    @FNTest(name = "Move action down")
    public void moveActionDown(Player player) {
        ActionTrigger actionTrigger = ActionTrigger.RIGHT_CLICK;
        NpcAction.NpcActionData existingAction1 = new NpcAction.NpcActionData(1, actionManager.getActionByName("player_command"), "say Hello World!");
        NpcAction.NpcActionData existingAction2 = new NpcAction.NpcActionData(2, actionManager.getActionByName("player_command"), "say Hello World 2!");
        npc.getData().setActions(actionTrigger, new ArrayList<>(List.of(existingAction1, existingAction2)));

        expect(player.performCommand("npc action " + npcName + " " + actionTrigger + " move_down 1")).toBe(true);

        expect(npc.getData().getActions(actionTrigger).size()).toEqual(2);
        expect(npc.getData().getActions(actionTrigger).getFirst().action().getName()).toEqual(existingAction2.action().getName());
        expect(npc.getData().getActions(actionTrigger).getFirst().value()).toEqual(existingAction2.value());
        expect(npc.getData().getActions(actionTrigger).getLast().action().getName()).toEqual(existingAction1.action().getName());
        expect(npc.getData().getActions(actionTrigger).getLast().value()).toEqual(existingAction1.value());
    }

    @FNTest(name = "Clear actions")
    public void clearActions(Player player) {
        ActionTrigger actionTrigger = ActionTrigger.RIGHT_CLICK;
        NpcAction.NpcActionData existingAction = new NpcAction.NpcActionData(1, actionManager.getActionByName("player_command"), "say Hello World!");
        npc.getData().setActions(actionTrigger, new ArrayList<>(List.of(existingAction)));

        expect(player.performCommand("npc action " + npcName + " " + actionTrigger + " clear")).toBe(true);

        expect(npc.getData().getActions(actionTrigger).size()).toEqual(0);
    }

}
