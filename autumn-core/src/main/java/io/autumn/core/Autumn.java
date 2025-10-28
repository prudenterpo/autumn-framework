package io.autumn.core;

import io.autumn.core.context.*;
import io.autumn.core.injection.DependencyInjector;
import io.autumn.core.lifecycle.LifecycleManager;
import io.autumn.core.registry.BeanRegistry;
import io.autumn.core.utils.ClassPathScanner;

import java.util.Set;

/**
 * Entry point for bootstrapping the Autumn IoC container.
 */
public class Autumn {

    public static AutumnContext start(String basePackage) {
        System.out.println("[AUTUMN] Bootstrapping context for package: " + basePackage);

        ClassPathScanner scanner = new ClassPathScanner();
        Set<Class<?>> components = scanner.findComponentClasses(basePackage);
        System.out.println("[AUTUMN] Discovered " + components.size() + "component(s)");

        BeanRegistry registry = new BeanRegistry();
        BeanFactory factory = new BeanFactory();
        DependencyInjector injector = new DependencyInjector();
        LifecycleManager lifecycleManager = new LifecycleManager();

        for (Class<?> comp : components) {
            registry.registerClass(comp);
            System.out.println("[AUTUMN] Registered component: " + comp.getName());
        }

        AutumnContext context = new AutumnContext(registry, factory, injector, lifecycleManager);
        System.out.println("[AUTUMN] Context initialized successfully");

        return context;
    }
}
