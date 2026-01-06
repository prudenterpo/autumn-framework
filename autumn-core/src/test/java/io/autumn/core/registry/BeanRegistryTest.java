package io.autumn.core.registry;

import io.autumn.core.annotations.Component;
import io.autumn.core.annotations.Primary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeanRegistryTest {

    private BeanRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new BeanRegistry();
    }

    @Test
    void shouldRegisterSimpleComponent() {
        registry.registerClass(SimpleComponent.class);

        assertTrue(registry.contains(SimpleComponent.class));
        assertNotNull(registry.getDefinition(SimpleComponent.class));
    }

    @Test
    void shouldResolveInterfaceToImplementation() {
        registry.registerClass(EmailSender.class);

        Class<?> resolved = registry.resolveType(NotificationSender.class);

        assertEquals(EmailSender.class, resolved);
    }

    @Test
    void shouldThrowWhenMultipleImplementationsWithoutPrimary() {
        registry.registerClass(EmailSender.class);

        assertThrows(IllegalStateException.class, () -> registry.registerClass(SmsSender.class));
    }

    @Test
    void shouldUsePrimaryImplementation() {
        registry.registerClass(SmsSender.class);
        registry.registerClass(PrimaryEmailSender.class);

        Class<?> resolved = registry.resolveType(NotificationSender.class);

        assertEquals(PrimaryEmailSender.class, resolved);
    }

    @Component
    static class SimpleComponent {
    }

    interface NotificationSender {
    }

    @Component
    static class EmailSender implements NotificationSender {
    }

    @Component
    static class SmsSender implements NotificationSender {
    }

    @Component
    @Primary
    static class PrimaryEmailSender implements NotificationSender {
    }
}