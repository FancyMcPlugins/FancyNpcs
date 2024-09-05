package de.oliver.fancynpcs.tests;

/**
 * A generic class for making assertions on the expected values.
 *
 * @param <T> the type of the value to be asserted.
 */
public class Expectable<T> {

    /**
     * The value that is being wrapped by this Expectable instance.
     * This is the object against which all expectations will be verified.
     */
    private final T t;

    private Expectable(T t) {
        this.t = t;
    }

    /**
     * Creates a new instance of Expectable for the given value.
     *
     * @param <T> the type of the value being tested
     * @param t   the actual value to create an expectation for
     * @return a new Expectable instance for the given value
     */
    public static <T> Expectable<T> expect(T t) {
        return new Expectable<>(t);
    }

    /**
     * Ensures that the actual value is not null.
     * <p>
     * Throws an AssertionError if the value of the field 't' is null,
     * indicating that the actual value is expected to be non-null.
     *
     * @throws AssertionError if the value of the field 't' is null
     */
    public void toBeDefined() {
        if (t == null) {
            throw new AssertionError("Expected not null but got null");
        }
    }

    /**
     * Asserts that the value of the field 't' is null.
     * <p>
     * Throws an AssertionError if the value of 't' is not null,
     * indicating the expectation that the value should be null.
     *
     * @throws AssertionError if the value of 't' is not null
     */
    public void toBeNull() {
        if (t != null) {
            throw new AssertionError("Expected null but got not null");
        }
    }

    /**
     * Asserts that the actual value is equal to the expected value.
     *
     * @param expected the value that the actual value is expected to be equal to
     * @throws AssertionError if the actual value is not equal to the expected value
     */
    public void toBe(T expected) {
        if (t != expected) {
            throw new AssertionError("Expected " + expected + " but got " + t);
        }
    }

    /**
     * Asserts that the actual value is equal to the expected value using the {@code equals} method.
     *
     * @param expected the value that the actual value is expected to be equal to
     * @throws AssertionError if the actual value is not equal to the expected value
     */
    public void toEqual(T expected) {
        if (!t.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but got " + t);
        }
    }

    /**
     * Asserts that the actual value is greater than the expected value.
     *
     * @param expected the value that the actual value is expected to be greater than
     * @throws AssertionError if the actual value is not greater than the expected value,
     *                        or if the type of the actual value is not one of Integer, Long, Float, or Double
     */
    public void toBeGreaterThan(T expected) {
        if (t instanceof Integer) {
            if ((Integer) t <= (Integer) expected) {
                throw new AssertionError("Expected " + t + " to be greater than " + expected);
            } else {
                return;
            }
        } else if (t instanceof Long) {
            if ((Long) t <= (Long) expected) {
                throw new AssertionError("Expected " + t + " to be greater than " + expected);
            } else {
                return;
            }
        } else if (t instanceof Float) {
            if ((Float) t <= (Float) expected) {
                throw new AssertionError("Expected " + t + " to be greater than " + expected);
            } else {
                return;
            }
        } else if (t instanceof Double) {
            if ((Double) t <= (Double) expected) {
                throw new AssertionError("Expected " + t + " to be greater than " + expected);
            } else {
                return;
            }
        }

        throw new AssertionError("toBeGreaterThan can only be used on Integers, Longs, Floats, and Doubles");
    }

    /**
     * Asserts that the actual value is less than the expected value.
     *
     * @param expected the value that the actual value is expected to be less than
     * @throws AssertionError if the actual value is not less than the expected value,
     *                        or if the type of the actual value is not one of Integer, Long, Float, or Double
     */
    public void toBeLessThan(T expected) {
        if (t instanceof Integer) {
            if ((Integer) t >= (Integer) expected) {
                throw new AssertionError("Expected " + t + " to be less than " + expected);
            } else {
                return;
            }
        } else if (t instanceof Long) {
            if ((Long) t >= (Long) expected) {
                throw new AssertionError("Expected " + t + " to be less than " + expected);
            } else {
                return;
            }
        } else if (t instanceof Float) {
            if ((Float) t >= (Float) expected) {
                throw new AssertionError("Expected " + t + " to be less than " + expected);
            } else {
                return;
            }
        } else if (t instanceof Double) {
            if ((Double) t >= (Double) expected) {
                throw new AssertionError("Expected " + t + " to be less than " + expected);
            } else {
                return;
            }
        }

        throw new AssertionError("toBeLessThan can only be used on Integers, Longs, Floats, and Doubles");
    }

    /**
     * Asserts that the actual value is an instance of the expected class.
     * This method checks whether the value held in the field 't' is an instance of the provided Class<?>.
     *
     * @param expected the Class object that the actual value is expected to be an instance of
     * @throws AssertionError if the actual value is not an instance of the expected class
     */
    public void toBeInstanceOf(Class<?> expected) {
        if (!expected.isInstance(t)) {
            throw new AssertionError("Expected " + t + " to be an instance of " + expected);
        }
    }

    /**
     * Asserts that the given expected value is contained within the actual value.
     * <p>
     * This method checks if the expected value is present in a String, Iterable, or Array.
     * If the actual value is a String, it uses the contains method to check if the expected value
     * is a substring. If the actual value is an Iterable, it checks if the expected value is an element.
     * If the actual value is an Array, it checks if the expected value is present in the array.
     *
     * @param expected the value that is expected to be contained within the actual value
     * @throws AssertionError if the expected value is not contained within the actual value
     */
    public void toContain(T expected) {
        if (t instanceof String) {
            if (!((String) t).contains((String) expected)) {
                throw new AssertionError("Expected " + expected + " to be contained in " + t);
            } else {
                return;
            }
        } else if (t instanceof Iterable) {
            if (!((Iterable<?>) t).spliterator().tryAdvance(o -> {
                if (o.equals(expected)) {
                    return;
                }
                throw new AssertionError("Expected " + expected + " to be contained in " + t);
            })) {
                throw new AssertionError("Expected " + expected + " to be contained in " + t);
            } else {
                return;
            }
        } else if (t instanceof Object[]) {
            for (Object o : (Object[]) t) {
                if (o.equals(expected)) {
                    return;
                }
            }
            throw new AssertionError("Expected " + expected + " to be contained in " + t);
        }

        throw new AssertionError("toContain can only be used on Strings, Iterables and Arrays");
    }

    /**
     * Asserts that the actual value has the expected length.
     * This method checks if the actual value is a String, Iterable, or Array,
     * and compares their length or size to the given expected length.
     *
     * @param expected the expected length of the actual value
     * @throws AssertionError if the actual value does not have the expected length,
     *                        or if the actual value is not of type String, Iterable, or Array
     */
    public void toHaveLength(int expected) {
        if (t instanceof String) {
            if (((String) t).length() != expected) {
                throw new AssertionError("Expected " + expected + " but got " + ((String) t).length());
            } else {
                return;
            }
        } else if (t instanceof Iterable) {
            if (((Iterable<?>) t).spliterator().getExactSizeIfKnown() != expected) {
                throw new AssertionError("Expected " + expected + " but got " + ((Iterable<?>) t).spliterator().getExactSizeIfKnown());
            } else {
                return;
            }
        } else if (t instanceof Object[]) {
            if (((Object[]) t).length != expected) {
                throw new AssertionError("Expected " + expected + " but got " + ((Object[]) t).length);
            } else {
                return;
            }
        }

        throw new AssertionError("toHaveLength can only be used on Strings");
    }
}
