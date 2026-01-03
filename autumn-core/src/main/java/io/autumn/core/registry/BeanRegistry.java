package io.autumn.core.registry;

import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanRegistry {

    private final Map<Class<?>, BeanDefinition> definitions = new HashMap<>();

    public void register(BeanDefinition definition) {
        definitions.put(definition.type(), definition);
    }

    public void registerClass(Class<?> clazz) {
        String beanName = clazz.getSimpleName();
        Constructor<?> constructor = resolveConstructor(clazz);
        List<Class<?>> paramTypes = Arrays.asList(constructor.getParameterTypes());

        BeanDefinition definition = new BeanDefinition(clazz, beanName, constructor, paramTypes);
        register(definition);
    }

    private Constructor<?> resolveConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        if (constructors.length == 1) {
            constructors[0].setAccessible(true);
            return constructors[0];
        }

        for (Constructor<?> ctor : constructors) {
            if (ctor.isAnnotationPresent(Inject.class)) {
                ctor.setAccessible(true);
                return ctor;
            }
        }

        throw new IllegalStateException(
                "[AUTUMN] Class " + clazz.getName() + " has multiple constructors. Mark one with @Inject."
        );
    }

    public BeanDefinition getDefinition(Class<?> type) {
        return definitions.get(type);
    }

    public Collection<BeanDefinition> getAll() {
        return definitions.values();
    }

    public boolean contains(Class<?> type) {
        return definitions.containsKey(type);
    }

    @Override
    public String toString() {
        return "BeanRegistry{" + "definitions=" + definitions + '}';
    }
}