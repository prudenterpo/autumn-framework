package io.autumn.core;

import io.autumn.core.context.AutumnContext;
import io.autumn.core.context.BeanFactory;
import io.autumn.core.lifecycle.LifecycleManager;
import io.autumn.core.registry.BeanDefinition;
import io.autumn.core.registry.BeanRegistry;
import io.autumn.core.utils.ClassPathScanner;

import java.util.Set;

public class Autumn {

    public static AutumnContext start(String basePackage) {
        System.out.println("[AUTUMN] Bootstrapping: " + basePackage);

        // 1) Discover components
        ClassPathScanner scanner = new ClassPathScanner();
        Set<Class<?>> components = scanner.findComponentClasses(basePackage);
        System.out.println("[AUTUMN] Discovered " + components.size() + " component(s)");

        // 2) Core services
        BeanRegistry registry = new BeanRegistry();
        LifecycleManager lifecycle = new LifecycleManager();
        BeanFactory factory = new BeanFactory(registry, lifecycle);

        // 3) Register bean definitions
        for (Class<?> comp : components) {
            registry.registerClass(comp);
            System.out.println("[AUTUMN] Registered: " + comp.getName());
        }

        // 4) Instantiate all singletons (eager init for MVP)
        for (BeanDefinition def : registry.getAll()) {
            factory.getOrCreateBean(def.type());
        }
        System.out.println("[AUTUMN] All components instantiated.");

        // 5) Build context
        AutumnContext context = new AutumnContext(registry, factory, lifecycle);

        // 6) JVM shutdown hook -> @PreDestroy
        Runtime.getRuntime().addShutdownHook(new Thread(context::close, "autumn-shutdown"));

        System.out.println("[AUTUMN] Context ready.");
        return context;
    }
}