package io.autumn.core.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LifecycleManager {

    private final List<Object> preDestroyCandidates = new CopyOnWriteArrayList<>();

    public void postConstruct(Object bean) {
        Class<?> clazz = bean.getClass();

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(PostConstruct.class)) {
                try {
                    m.setAccessible(true);
                    m.invoke(bean);
                } catch (Exception e) {
                    throw new IllegalStateException("[AUTUMN] Failed to invoke @PostConstruct on " + clazz.getName(), e);
                }
            }
        }

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(PreDestroy.class)) {
                preDestroyCandidates.add(bean);
                break;
            }
        }
    }

    public void shutdown() {
        for (Object bean : preDestroyCandidates) {
            Class<?> clazz = bean.getClass();
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(PreDestroy.class)) {
                    try {
                        m.setAccessible(true);
                        m.invoke(bean);
                    } catch (Exception e) {
                        System.err.println("[AUTUMN] Failed to invoke @PreDestroy on " + clazz.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
        preDestroyCandidates.clear();
    }
}