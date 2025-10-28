package io.autumn.core.context;

import io.autumn.core.registry.BeanRegistry;

/**
 * Central interface providing access to Autumn-managed beans.
 * Responsible for lifecycle management and dependency resolution.
 */
public interface ApplicationContext {

    <T> T getBean(Class<T> type);

    BeanRegistry getRegistry();
}
