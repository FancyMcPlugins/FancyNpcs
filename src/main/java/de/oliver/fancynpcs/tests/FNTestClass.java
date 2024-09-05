package de.oliver.fancynpcs.tests;

import de.oliver.fancynpcs.tests.annotations.FNAfterEach;
import de.oliver.fancynpcs.tests.annotations.FNBeforeEach;
import de.oliver.fancynpcs.tests.annotations.FNTest;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * FNTestClass is a record that encapsulates information about a test class and its associated test methods.
 * This class supports running tests annotated with {@link FNTest}.
 *
 * @param testClass   the test class to run tests for (must be annotated with {@link FNTest})
 * @param beforeEach  the method annotated with {@link FNBeforeEach} to run before each test
 * @param afterEach   the method annotated with {@link FNAfterEach} to run after each test
 * @param testMethods the list of test methods annotated with {@link FNTest}
 */
public record FNTestClass(
        Class<?> testClass,
        Method beforeEach,
        Method afterEach,
        List<Method> testMethods
) {

    private static final Logger logger = Logger.getLogger(FNTestClass.class.getName());

    /**
     * Creates an instance of FNTestClass by inspecting the provided test class for methods annotated
     * with FNTest, FNBeforeEach, and FNAfterEach annotations.
     * These methods are used to define the setup, teardown, and test methods for the class.
     *
     * @param testClass the class to be inspected for annotated methods
     * @return an instance of FNTestClass containing the test class and its annotated methods
     */
    public static FNTestClass fromClass(Class<?> testClass) {
        Method beforeEach = null;
        Method afterEach = null;
        List<Method> testMethods = new ArrayList<>();

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(FNTest.class)) {
                if (method.getParameterCount() != 1) continue;
                if (method.getParameterTypes()[0] != Player.class) continue;

                testMethods.add(method);
                continue;
            }

            if (method.isAnnotationPresent(FNBeforeEach.class)) {
                if (method.getParameterCount() != 1) continue;
                if (method.getParameterTypes()[0] != Player.class) continue;

                beforeEach = method;
                continue;
            }

            if (method.isAnnotationPresent(FNAfterEach.class)) {
                if (method.getParameterCount() != 1) continue;
                if (method.getParameterTypes()[0] != Player.class) continue;

                afterEach = method;
            }
        }

        return new FNTestClass(testClass, beforeEach, afterEach, testMethods);
    }

    /**
     * Runs the test methods belonging to the test class, performing any necessary setup and teardown operations.
     *
     * @param player The player context to pass to the test methods.
     * @return true if all tests completed successfully, false if any test failed or an unexpected exception occurred.
     */
    public boolean runTests(Player player) {
        logger.info("Running tests for " + testClass.getSimpleName());
        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Running tests for " + testClass.getSimpleName()));

        for (Method testMethod : testMethods) {
            Object testClassObj;
            try {
                testClassObj = testClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                logger.warning("Failed to create test class instance: " + e.getMessage());
                return false;
            }

            FNTest fnTest = testMethod.getAnnotation(FNTest.class);
            if (fnTest.skip()) {
                logger.info("Skipping test " + displayName(testMethod));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gold>Skipping test " + displayName(testMethod)));
                continue;
            }


            long testStart = System.currentTimeMillis();

            try {
                if (beforeEach != null) beforeEach.invoke(testClassObj, player);

                testMethod.invoke(testClassObj, player);

                if (afterEach != null) afterEach.invoke(testClassObj, player);
            } catch (InvocationTargetException e) {
                logger.warning("Test " + displayName(testMethod) + " failed with exception: " + e.getCause().getMessage());
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Test " + displayName(testMethod) + " failed with exception: " + e.getCause().getMessage()));
                return false;
            } catch (Exception e) {
                logger.warning("Unexpected exception in test " + fnTest.name() + ": " + e.getMessage());
                return false;
            }

            long testEnd = System.currentTimeMillis();
            logger.info("Test " + displayName(testMethod) + " took " + (testEnd - testStart) + "ms");
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Test " + displayName(testMethod) + " took " + (testEnd - testStart) + "ms"));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.warning("Thread interrupted while waiting between tests: " + e.getMessage());
            }
        }

        return true;
    }

    /**
     * Generates a display name for a given test method, incorporating annotation details if present.
     *
     * @param m the method for which to generate the display name
     * @return a display name that includes the test class and method name, and optionally the value of the FNTest annotation's name attribute if the annotation is present
     */
    public String displayName(Method m) {
        if (!m.isAnnotationPresent(FNTest.class)) {
            return testClass.getSimpleName() + "#" + m.getName();
        }

        FNTest fnTest = m.getAnnotation(FNTest.class);
        return testClass.getSimpleName() + "#" + m.getName() + " (" + fnTest.name() + ")";
    }

}
