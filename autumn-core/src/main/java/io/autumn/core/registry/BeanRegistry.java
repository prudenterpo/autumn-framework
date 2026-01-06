package io.autumn.core.registry;

import io.autumn.core.annotations.Primary;
import io.autumn.core.annotations.Scope;
import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanRegistry {

    private final Map<Class<?>, BeanDefinition> definitions = new HashMap<>();
    private final Map<Class<?>, Class<?>> interfaceToImpl = new HashMap<>();

    public void register(BeanDefinition definition) {
        definitions.put(definition.type(), definition);
        registerInterfaces(definition.type());
    }

    public void registerClass(Class<?> clazz) {
        String beanName = clazz.getSimpleName();
        Constructor<?> constructor = resolveConstructor(clazz);
        List<Class<?>> paramTypes = Arrays.asList(constructor.getParameterTypes());
        boolean singleton = resolveScope(clazz);

        BeanDefinition definition = new BeanDefinition(clazz, beanName, constructor, paramTypes, singleton);
        register(definition);
    }

    private boolean resolveScope(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Scope.class)) {
            String value = clazz.getAnnotation(Scope.class).value();
            return !"prototype".equalsIgnoreCase(value);
        }
        return true;
    }

    private void registerInterfaces(Class<?> clazz) {
        boolean isPrimary = clazz.isAnnotationPresent(Primary.class);

        for (Class<?> iface : clazz.getInterfaces()) {
            Class<?> existing = interfaceToImpl.get(iface);

            if (existing == null) {
                interfaceToImpl.put(iface, clazz);
            } else if (isPrimary) {
                if (existing.isAnnotationPresent(Primary.class)) {
                    throw new IllegalStateException(
                            "[AUTUMN] Multiple @Primary implementations for " + iface.getName() +
                                    ": " + existing.getName() + " and " + clazz.getName()
                    );
                }
                interfaceToImpl.put(iface, clazz);
            } else if (!existing.isAnnotationPresent(Primary.class)) {
                throw new IllegalStateException(
                        "[AUTUMN] Multiple implementations found for " + iface.getName() +
                                ": " + existing.getName() + " and " + clazz.getName() +
                                ". Use @Primary to disambiguate."
                );
            }
        }
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
        BeanDefinition def = definitions.get(type);
        if (def != null) {
            return def;
        }

        Class<?> impl = interfaceToImpl.get(type);
        if (impl != null) {
            return definitions.get(impl);
        }

        return null;
    }

    public Class<?> resolveType(Class<?> type) {
        if (definitions.containsKey(type)) {
            return type;
        }
        return interfaceToImpl.get(type);
    }

    public Collection<BeanDefinition> getAll() {
        return definitions.values();
    }

    public boolean contains(Class<?> type) {
        return definitions.containsKey(type) || interfaceToImpl.containsKey(type);
    }

    @Override
    public String toString() {
        return "BeanRegistry{definitions=" + definitions.size() +
                ", interfaces=" + interfaceToImpl.size() + "}";
    }
}