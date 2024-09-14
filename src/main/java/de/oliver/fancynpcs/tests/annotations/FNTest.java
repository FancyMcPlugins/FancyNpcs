package de.oliver.fancynpcs.tests.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FNTest is a custom annotation designed to be used on methods for marking them as test cases.
 * It helps to identify methods that should be treated as test cases in the testing framework.
 * The annotation's attributes allow for providing a human-readable test name and an optional flag to skip the test.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FNTest {
    
    /**
     * Specifies the name of the test case. This name is used to identify the test case
     * in reports, logs, and other contexts where the test case is referenced.
     *
     * @return the name of the test case
     */
    String name();

    /**
     * Indicates whether the annotated test case should be skipped during test execution.
     *
     * @return true if the test case should be skipped, false otherwise
     */
    boolean skip() default false;
}
