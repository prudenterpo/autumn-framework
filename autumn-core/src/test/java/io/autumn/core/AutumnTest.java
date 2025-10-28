package io.autumn.core;

import io.autumn.core.annotations.Component;
import io.autumn.core.context.AutumnContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AutumnTest {

    @Component
    static class DemoService {};

    @Test
    void shouldRegisterComponentClasses() {
        AutumnContext context = Autumn.start("io.autumn.core");
        assertNotNull(context.getRegistry());
        assertFalse(context.getRegistry().getAll().isEmpty(),
                "Registry should contain discovered component");
    }
}
