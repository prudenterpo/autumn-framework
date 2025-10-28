package io.autumn.core.annotations;

import java.lang.annotation.*;

/**
 * Method annotated with this will be invoked before container shutdown.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreDestroy {
}
