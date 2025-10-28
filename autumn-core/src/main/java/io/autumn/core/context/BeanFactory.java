package io.autumn.core.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Responsible for creating and caching bean instances.
 * Equivalent to Spring's DefaultListableBeanFactory.
 */
public class BeanFactory {

    private final Map<Class<?>, Object> singletonCache = new ConcurrentHashMap<>();

    public Object getBean(Class<?> type) {
        return singletonCache.get(type);
    }

    public void registerBean(Class<?> type, Object instance) {
        singletonCache.put(type, instance);
    }

    public boolean containsBean(Class<?> type) {
        return singletonCache.containsKey(type);
    }
}
