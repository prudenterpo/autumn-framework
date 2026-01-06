package io.autumn.core.context;

import io.autumn.core.lifecycle.LifecycleManager;
import io.autumn.core.registry.BeanDefinition;
import io.autumn.core.registry.BeanRegistry;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {

    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Set<Class<?>> creating = ConcurrentHashMap.newKeySet();

    private final BeanRegistry registry;
    private final LifecycleManager lifecycle;

    public BeanFactory(BeanRegistry registry, LifecycleManager lifecycle) {
        this.registry = registry;
        this.lifecycle = lifecycle;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCreateBean(Class<T> type) {
        Class<?> resolvedType = registry.resolveType(type);
        if (resolvedType == null) {
            throw new IllegalStateException("[AUTUMN] No bean found for type: " + type.getName());
        }

        BeanDefinition definition = registry.getDefinition(resolvedType);

        if (definition.isSingleton()) {
            return (T) singletons.computeIfAbsent(resolvedType, t -> createBeanInternal(definition));
        } else {
            return (T) createBeanInternal(definition);
        }
    }

    public boolean containsBean(Class<?> type) {
        return singletons.containsKey(type) || singletons.containsKey(registry.resolveType(type));
    }

    private Object createBeanInternal(BeanDefinition definition) {
        Class<?> type = definition.type();

        if (!creating.add(type)) {
            throw new IllegalStateException("[AUTUMN] Circular dependency detected while creating: " + type.getName());
        }

        try {
            Object[] dependencies = resolveDependencies(definition.constructorParamTypes());
            Object instance = instantiate(definition.constructor(), dependencies);

            lifecycle.postConstruct(instance);

            return instance;
        } finally {
            creating.remove(type);
        }
    }

    private Object[] resolveDependencies(List<Class<?>> paramTypes) {
        Object[] dependencies = new Object[paramTypes.size()];
        for (int i = 0; i < paramTypes.size(); i++) {
            dependencies[i] = getOrCreateBean(paramTypes.get(i));
        }
        return dependencies;
    }

    private Object instantiate(Constructor<?> constructor, Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "[AUTUMN] Failed to instantiate: " + constructor.getDeclaringClass().getName(), e
            );
        }
    }
}