package io.autumn.core.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BeanRegistry {

    private final Map<Class<?>, BeanDefinition> definitions = new HashMap<>();

    public void register(BeanDefinition definition) {
        definitions.put(definition.type(), definition);
    }

    public void registerClass(Class<?> clazz) {
        String beanName = clazz.getSimpleName();
        BeanDefinition definition = new BeanDefinition(clazz, beanName);
        register(definition);
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
