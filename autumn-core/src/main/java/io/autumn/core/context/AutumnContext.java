package io.autumn.core.context;

import io.autumn.core.lifecycle.LifecycleManager;
import io.autumn.core.registry.BeanRegistry;

public class AutumnContext implements ApplicationContext {

    private final BeanRegistry registry;
    private final BeanFactory factory;
    private final LifecycleManager lifecycle;

    public AutumnContext(BeanRegistry registry, BeanFactory factory, LifecycleManager lifecycle) {
        this.registry = registry;
        this.factory = factory;
        this.lifecycle = lifecycle;
    }

    @Override
    public <T> T getBean(Class<T> type) {
        return factory.getOrCreateBean(type);
    }

    @Override
    public BeanRegistry getRegistry() {
        return registry;
    }

    public void close() {
        lifecycle.shutdown();
    }
}