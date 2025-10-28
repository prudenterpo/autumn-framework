package io.autumn.core.annotations;

import java.lang.annotation.*;

/**
 * Marks a class as a managed component in the Autumn IoC container.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default "";
}
