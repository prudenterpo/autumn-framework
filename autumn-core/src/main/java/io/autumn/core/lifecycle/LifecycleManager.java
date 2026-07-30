package io.autumn.core.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LifecycleManager {

    private final List<Object> preDestroyCandidates = new CopyOnWriteArrayList<>();

    public void postConstruct(Object bean, boolean singleton) {
        Class<?> clazz = bean.getClass();
        boolean hasPreDestroy = false;

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(PostConstruct.class)) {
                try {
                    m.setAccessible(true);
                    m.invoke(bean);
                } catch (Exception e) {
                    System.err.println("[AUTUMN] Failed to invoke @PostConstruct on " + clazz.getName() + ": " + e.getMessage());
                }
            }
            if (!hasPreDestroy && m.isAnnotationPresent(PreDestroy.class)) {
                hasPreDestroy = true;
            }
        }

        if (singleton && hasPreDestroy) {
            preDestroyCandidates.add(bean);
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
