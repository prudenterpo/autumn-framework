package io.autumn.core;

import io.autumn.core.context.AutumnContext;
import io.autumn.core.testfixtures.EmailNotifier;
import io.autumn.core.testfixtures.Notifier;
import io.autumn.core.testfixtures.PrototypeBean;
import io.autumn.core.testfixtures.ServiceA;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AutumnIntegrationTest {

    @Test
    void shouldBootstrapAndInjectDependencies() {
        AutumnContext context = Autumn.start("io.autumn.core.testfixtures");

        ServiceA serviceA = context.getBean(ServiceA.class);

        assertNotNull(serviceA);
        assertNotNull(serviceA.getServiceB());
        assertTrue(serviceA.isInitialized());
    }

    @Test
    void shouldResolveInterfaceWithPrimary() {
        AutumnContext context = Autumn.start("io.autumn.core.testfixtures");

        Notifier notifier = context.getBean(Notifier.class);

        assertNotNull(notifier);
        assertInstanceOf(EmailNotifier.class, notifier);
    }

    @Test
    void shouldRespectPrototypeScope() {
        AutumnContext context = Autumn.start("io.autumn.core.testfixtures");

        PrototypeBean first = context.getBean(PrototypeBean.class);
        PrototypeBean second = context.getBean(PrototypeBean.class);

        assertNotSame(first, second);
    }
}