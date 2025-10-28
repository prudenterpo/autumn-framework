package io.autumn.core.utils;

import io.autumn.core.annotations.Component;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClassPathScannerTest {

    @Component
    static class DemoComponent {}

    @Test
    void shouldFindComponentClasses() {
        ClassPathScanner scanner = new ClassPathScanner();
        Set<Class<?>> components = scanner.findComponentClasses("io.autumn.core.utils");

        assertTrue(components.contains(DemoComponent.class),
                "Expected DemoComponent to be detected as @Component");
    }
}
