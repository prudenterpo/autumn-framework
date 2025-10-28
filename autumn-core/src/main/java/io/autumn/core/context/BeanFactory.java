package io.autumn.core.context;

import io.autumn.core.injection.DependencyInjector;
import io.autumn.core.lifecycle.LifecycleManager;
import io.autumn.core.registry.BeanRegistry;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {

    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Set<Class<?>> creating = ConcurrentHashMap.newKeySet();

    private final BeanRegistry registry;
    private final DependencyInjector injector;
    private final LifecycleManager lifecycle;

    public BeanFactory(BeanRegistry registry, DependencyInjector injector, LifecycleManager lifecycle) {
        this.registry = registry;
        this.injector = injector;
        this.lifecycle = lifecycle;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCreateBean(Class<T> type) {
        return (T) singletons.computeIfAbsent(type, this::createBeanInternal);
    }

    private Object createBeanInternal(Class<?> type) {
        if (!registry.contains(type)) {
            throw new IllegalStateException("[AUTUMN] No BeanDefinition for type: " + type.getName());
        }
        if (!creating.add(type)) {
            throw new IllegalStateException("[AUTUMN] Circular dependency detected while creating: " + type.getName());
        }

        try {
            Constructor<?> ctor = resolveNoArgConstructor(type);
            Object instance = newInstance(ctor);

            injector.injectDependencies(instance, this);

            lifecycle.postConstruct(instance);

            return instance;
        } finally {
            creating.remove(type);
        }
    }

    private Constructor<?> resolveNoArgConstructor(Class<?> type) {
        try {
            Constructor<?> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("[AUTUMN] Missing no-arg constructor on: " + type.getName());
        }
    }

    private Object newInstance(Constructor<?> ctor) {
        try {
            return ctor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("[AUTUMN] Failed to instantiate: " + ctor.getDeclaringClass().getName(), e);
        }
    }

    public boolean containsBean(Class<?> type) {
        return singletons.containsKey(type);
    }
}
