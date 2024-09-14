package de.oliver.fancynpcs.tests.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method that should be executed after each test case in a test class.
 * This annotation is used to identify methods that perform teardown operations, ensuring
 * that the test environment is cleaned up and reset after each individual test method is executed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FNAfterEach {
}
