package de.oliver.fancynpcs.tests.impl;

import de.oliver.fancynpcs.tests.FNTestClass;
import de.oliver.fancynpcs.tests.impl.api.ChatColorHandlerTest;
import de.oliver.fancynpcs.tests.impl.api.CreateNpcTest;
import de.oliver.fancynpcs.tests.impl.commands.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * FancyNpcsTests is a class responsible for managing and running test classes associated with NPC behavior.
 * It maintains a list of test classes and provides methods to add new test classes and run all registered tests.
 */
public class FancyNpcsTests {

    private final List<FNTestClass> tests = new ArrayList<>();

    public FancyNpcsTests() {
        // api tests
        addTest(CreateNpcTest.class);
        addTest(ChatColorHandlerTest.class);

        // command tests
        addTest(CreateCMDTest.class);
        addTest(TypeCMDTest.class);
        addTest(DisplayNameCMDTest.class);
        addTest(TurnToPlayerCMDTest.class);
        addTest(ActionCMDTest.class);
    }

    /**
     * Adds a test class to the list of test classes to be run.
     *
     * @param testClass the test class to be added
     * @return this instance, allowing for method chaining
     */
    public FancyNpcsTests addTest(Class<?> testClass) {
        tests.add(FNTestClass.fromClass(testClass));
        return this;
    }

    /**
     * Runs all registered test classes using the provided player context.
     *
     * @param player The player context to pass to the test methods.
     * @return true if all tests completed successfully, false if any test failed or an unexpected exception occurred.
     */
    public boolean runAllTests(Player player) {
        for (FNTestClass test : tests) {
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

    /**
     * Returns the current count of test classes registered to be run.
     *
     * @return the number of test classes in the list
     */
    public int getTestCount() {
        int count = 0;

        for (FNTestClass test : tests) {
            count += test.testMethods().size();
        }

        return count;
    }
}
