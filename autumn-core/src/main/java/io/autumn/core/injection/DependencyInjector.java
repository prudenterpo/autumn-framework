package io.autumn.core.injection;

import io.autumn.core.annotations.Inject;
import io.autumn.core.context.BeanFactory;

import java.lang.reflect.Field;

public class DependencyInjector {

    public void injectDependencies(Object target, BeanFactory factory) {
        Class<?> clazz = target.getClass();
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Inject.class)) continue;

            Class<?> depType = f.getType();
            Object dependency = factory.getOrCreateBean(depType);

            try {
                f.setAccessible(true);
                f.set(target, dependency);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("[AUTUMN] Failed to inject dependency: " +
                        depType.getName() + " into " + clazz.getName(), e);
            }
        }
    }
}
