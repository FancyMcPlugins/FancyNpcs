package de.oliver.fancynpcs.tests;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FancyNpcsTests implements FancyNpcsTest {

    private final List<FancyNpcsTest> tests;

    public FancyNpcsTests() {
        this.tests = new ArrayList<>();
    }

    public FancyNpcsTests addTest(FancyNpcsTest test) {
        tests.add(test);
        return this;
    }


    @Override
    public boolean before(Player player) {
        return true;
    }

    @Override
    public boolean test(Player player) {
        for (FancyNpcsTest test : tests) {
            long testStart = System.currentTimeMillis();

            try {
                if (!test.before(player)) return false;
                if (!test.test(player)) return false;
                if (!test.after(player)) return false;
            } catch (Exception e) {
                System.out.println("Test " + test.getClass().getSimpleName() + " failed with exception: " + e.getMessage());
                player.sendMessage("Test " + test.getClass().getSimpleName() + " failed with exception: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            long testEnd = System.currentTimeMillis();
            System.out.println("Test " + test.getClass().getSimpleName() + " took " + (testEnd - testStart) + "ms");
            player.sendMessage("Test " + test.getClass().getSimpleName() + " took " + (testEnd - testStart) + "ms");
        }
        return true;
    }

    @Override
    public boolean after(Player player) {
        return true;
    }
}
