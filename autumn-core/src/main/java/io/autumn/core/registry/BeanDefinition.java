package io.autumn.core.registry;

/**
 * Metadata representation of a bean managed by the Autumn container.
 */
public class BeanDefinition {
    private final Class<?> type;
    private final String name;

    public BeanDefinition(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
