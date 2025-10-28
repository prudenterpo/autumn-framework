package io.autumn.core.context;

import io.autumn.core.registry.BeanRegistry;
import io.autumn.core.injection.DependencyInjector;
import io.autumn.core.lifecycle.LifecycleManager;

/**
 * Default implementation of ApplicationContext.
 * Responsible for initializing and managing the Autumn IoC container.
 */
public class AutumnContext implements ApplicationContext {

    private final BeanRegistry registry;
    private final BeanFactory factory;
    private final DependencyInjector injector;
    private final LifecycleManager lifecycle;

    public AutumnContext(BeanRegistry registry, BeanFactory factory, DependencyInjector injector, LifecycleManager lifecycle) {
        this.registry = registry;
        this.factory = factory;
        this.injector = injector;
        this.lifecycle = lifecycle;
    }

    @Override
    public <T> T getBean(Class<T> type) {
        return type.cast(factory.getBean(type));
    }

    @Override
    public BeanRegistry getRegistry() {
        return registry;
    }

    public void close() {
        lifecycle.shutdown();
    }
}
