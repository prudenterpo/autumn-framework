package io.autumn.core.annotations;

import java.lang.annotation.*;

/**
 * Method annotated with this will be invoked after dependency injection.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct {
}
