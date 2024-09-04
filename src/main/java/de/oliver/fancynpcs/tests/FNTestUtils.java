package de.oliver.fancynpcs.tests;

public class FNTestUtils {

    public static void assertEqual(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    public static void assertNotEqual(Object expected, Object actual) {
        if (expected.equals(actual)) {
            throw new AssertionError("Expected not " + expected + " but got " + actual);
        }
    }

    public static void assertNotNull(Object object) {
        if (object == null) {
            throw new AssertionError("Expected not null but got null");
        }
    }

    public static void assertNull(Object object) {
        if (object != null) {
            throw new AssertionError("Expected null but got not null");
        }
    }

    public static void failTest(String message) {
        throw new AssertionError(message);
    }

}
