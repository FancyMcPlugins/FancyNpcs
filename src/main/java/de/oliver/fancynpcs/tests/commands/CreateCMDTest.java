package de.oliver.fancynpcs.tests.commands;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.commands.npc.CreateCMD;
import de.oliver.fancynpcs.tests.FancyNpcsTest;
import org.bukkit.entity.Player;

public class CreateCMDTest implements FancyNpcsTest {

    private static final NpcManager NPC_MANAGER = FancyNpcsPlugin.get().getNpcManager();

    private String npcName;
    private Npc createdNpc;

    @Override
    public boolean before(Player player) {
        this.npcName = "test-" + player.getUniqueId().toString().substring(0, 8);
        return true;
    }

    @Override
    public boolean test(Player player) {
        CreateCMD.INSTANCE.onCreate(player, npcName, null, null, null);

        createdNpc = NPC_MANAGER.getNpc(npcName);
        if (createdNpc == null) {
            throw new IllegalStateException("Npc was not created");
        }

        if (createdNpc.getEntityId() < 0) {
            throw new IllegalStateException("Npc entity was not created");
        }

        return true;
    }

    @Override
    public boolean after(Player player) {
        NPC_MANAGER.removeNpc(createdNpc);
        if (NPC_MANAGER.getNpc(npcName) != null) {
            throw new IllegalStateException("Npc was not removed");
        }

        return true;
    }
}
