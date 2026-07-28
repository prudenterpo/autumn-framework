package io.autumn.core.registry;

import java.lang.reflect.Constructor;
import java.util.List;

public record BeanDefinition(Class<?> type, String name, Constructor<?> constructor,
                             List<Class<?>> constructorParamTypes, boolean singleton) {

    public boolean isSingleton() {
        return singleton;
    }
}