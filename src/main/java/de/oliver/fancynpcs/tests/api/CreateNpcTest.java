package de.oliver.fancynpcs.tests.api;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.tests.FancyNpcsTest;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CreateNpcTest implements FancyNpcsTest {

    private static final NpcManager NPC_MANAGER = FancyNpcsPlugin.get().getNpcManager();

    private String npcName;
    private UUID creatorUUID;
    private Location location;

    private Npc createdNpc;

    @Override
    public boolean before(Player player) {
        this.npcName = "test-" + UUID.randomUUID().toString().substring(0, 8);
        this.creatorUUID = player.getUniqueId();
        this.location = player.getLocation().clone();
        return true;
    }

    @Override
    public boolean test(Player player) {
        NpcData data = new NpcData(npcName, creatorUUID, location);
        createdNpc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);

        if (createdNpc == null) {
            throw new IllegalStateException("Npc was not created");
        }

        createdNpc.create();

        if (createdNpc.getEntityId() < 0) {
            throw new IllegalStateException("Npc was not created");
        }

        NPC_MANAGER.registerNpc(createdNpc);

        if (NPC_MANAGER.getNpc(npcName) == null) {
            throw new IllegalStateException("Npc was not created");
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
