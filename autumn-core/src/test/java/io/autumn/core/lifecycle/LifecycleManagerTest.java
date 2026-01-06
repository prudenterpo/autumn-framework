package io.autumn.core.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LifecycleManagerTest {

    private LifecycleManager lifecycle;

    @BeforeEach
    void setUp() {
        lifecycle = new LifecycleManager();
    }

    @Test
    void shouldInvokePostConstruct() {
        PostConstructBean bean = new PostConstructBean();

        assertFalse(bean.isInitialized());

        lifecycle.postConstruct(bean);

        assertTrue(bean.isInitialized());
    }

    @Test
    void shouldInvokePreDestroyOnShutdown() {
        PreDestroyBean bean = new PreDestroyBean();
        lifecycle.postConstruct(bean);

        assertFalse(bean.isDestroyed());

        lifecycle.shutdown();

        assertTrue(bean.isDestroyed());
    }

    static class PostConstructBean {
        private boolean initialized = false;

        @PostConstruct
        void init() {
            initialized = true;
        }

        boolean isInitialized() {
            return initialized;
        }
    }

    static class PreDestroyBean {
        private boolean destroyed = false;

        @PreDestroy
        void cleanup() {
            destroyed = true;
        }

        boolean isDestroyed() {
            return destroyed;
        }
    }
}