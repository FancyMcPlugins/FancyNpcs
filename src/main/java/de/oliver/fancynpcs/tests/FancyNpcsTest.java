package de.oliver.fancynpcs.tests;

import org.bukkit.entity.Player;

public interface FancyNpcsTest {

    boolean before(Player player);

    boolean test(Player player);

    boolean after(Player player);

}
