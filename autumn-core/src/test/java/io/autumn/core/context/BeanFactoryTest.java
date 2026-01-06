package io.autumn.core.context;

import io.autumn.core.annotations.Component;
import io.autumn.core.annotations.Scope;
import io.autumn.core.lifecycle.LifecycleManager;
import io.autumn.core.registry.BeanRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeanFactoryTest {

    private BeanRegistry registry;
    private BeanFactory factory;

    @BeforeEach
    void setUp() {
        registry = new BeanRegistry();
        LifecycleManager lifecycle = new LifecycleManager();
        factory = new BeanFactory(registry, lifecycle);
    }

    @Test
    void shouldCreateSingletonByDefault() {
        registry.registerClass(SingletonComponent.class);

        Object first = factory.getOrCreateBean(SingletonComponent.class);
        Object second = factory.getOrCreateBean(SingletonComponent.class);

        assertSame(first, second);
    }

    @Test
    void shouldCreateNewInstanceForPrototype() {
        registry.registerClass(PrototypeComponent.class);

        Object first = factory.getOrCreateBean(PrototypeComponent.class);
        Object second = factory.getOrCreateBean(PrototypeComponent.class);

        assertNotSame(first, second);
    }

    @Test
    void shouldInjectDependenciesViaConstructor() {
        registry.registerClass(Repository.class);
        registry.registerClass(Service.class);

        Service service = factory.getOrCreateBean(Service.class);

        assertNotNull(service);
        assertNotNull(service.getRepository());
    }

    @Test
    void shouldThrowOnCircularDependency() {
        registry.registerClass(CircularA.class);
        registry.registerClass(CircularB.class);

        assertThrows(IllegalStateException.class, () -> {
            factory.getOrCreateBean(CircularA.class);
        });
    }

    @Component
    static class SingletonComponent {
    }

    @Component
    @Scope("prototype")
    static class PrototypeComponent {
    }

    @Component
    static class Repository {
    }

    @Component
    static class Service {
        private final Repository repository;

        public Service(Repository repository) {
            this.repository = repository;
        }

        public Repository getRepository() {
            return repository;
        }
    }

    @Component
    static class CircularA {
        public CircularA(CircularB b) {
        }
    }

    @Component
    static class CircularB {
        public CircularB(CircularA a) {
        }
    }
}