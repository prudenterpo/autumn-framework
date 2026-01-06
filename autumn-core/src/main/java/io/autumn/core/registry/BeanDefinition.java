package io.autumn.core.registry;

import java.lang.reflect.Constructor;
import java.util.List;

public class BeanDefinition {

    private final Class<?> type;
    private final String name;
    private final Constructor<?> constructor;
    private final List<Class<?>> constructorParamTypes;
    private final boolean singleton;

    public BeanDefinition(Class<?> type, String name, Constructor<?> constructor,
                          List<Class<?>> constructorParamTypes, boolean singleton) {
        this.type = type;
        this.name = name;
        this.constructor = constructor;
        this.constructorParamTypes = constructorParamTypes;
        this.singleton = singleton;
    }

    public Class<?> type() {
        return type;
    }

    public String name() {
        return name;
    }

    public Constructor<?> constructor() {
        return constructor;
    }

    public List<Class<?>> constructorParamTypes() {
        return constructorParamTypes;
    }

    public boolean isSingleton() {
        return singleton;
    }
}