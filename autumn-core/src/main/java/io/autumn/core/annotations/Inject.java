package io.autumn.core.annotations;

import java.lang.annotation.*;

/**
 * Marks a field to be injected with a dependency managed by Autumn.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}
