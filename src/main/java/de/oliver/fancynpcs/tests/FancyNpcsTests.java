package de.oliver.fancynpcs.tests;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FancyNpcsTests {

    private final List<FNTestClass> tests = new ArrayList<>();

    public FancyNpcsTests() {

    }

    public FancyNpcsTests addTest(Class<?> testClass) {
        tests.add(FNTestClass.fromClass(testClass));
        return this;
    }

    public boolean runAllTests(Player player) {
        for (FNTestClass test : tests) {
            System.out.println("Running tests for " + test.testClass().getSimpleName());
            try {
                if (!test.runTests(player)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
