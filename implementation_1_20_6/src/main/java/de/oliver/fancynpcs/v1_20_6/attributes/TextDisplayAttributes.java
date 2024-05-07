package de.oliver.fancynpcs.v1_20_6.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_20_6.ReflectionHelper;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.world.entity.Display;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class TextDisplayAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "text",
                new ArrayList<>(),
                List.of(EntityType.TEXT_DISPLAY),
                TextDisplayAttributes::setText
        ));

        return attributes;
    }

    private static void setText(Npc npc, String value) {
        Display.TextDisplay display = ReflectionHelper.getEntity(npc);

        Component text = MiniMessage.miniMessage().deserialize(value);
        display.setText(PaperAdventure.asVanilla(text));
    }
}
