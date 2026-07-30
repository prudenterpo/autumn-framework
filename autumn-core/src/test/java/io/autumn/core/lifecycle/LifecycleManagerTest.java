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

        lifecycle.postConstruct(bean, true);

        assertTrue(bean.isInitialized());
    }

    @Test
    void shouldInvokePreDestroyOnShutdown() {
        PreDestroyBean bean = new PreDestroyBean();
        lifecycle.postConstruct(bean, true);

        assertFalse(bean.isDestroyed());

        lifecycle.shutdown();

        assertTrue(bean.isDestroyed());
    }

    @Test
    void shouldNotTrackPrototypePreDestroy() {
        PrototypeWithPreDestroyBean bean = new PrototypeWithPreDestroyBean();
        lifecycle.postConstruct(bean, false);

        lifecycle.shutdown();

        assertFalse(bean.isDestroyed());
    }

    @Test
    void shouldNotFailOnPostConstructException() {
        FailingPostConstructBean bean = new FailingPostConstructBean();

        assertDoesNotThrow(() -> lifecycle.postConstruct(bean, true));
        assertTrue(bean.isInitialized());
    }

    @Test
    void shouldHandleBothAnnotationsInSinglePass() {
        AnnotatedBean bean = new AnnotatedBean();

        lifecycle.postConstruct(bean, true);

        assertTrue(bean.isPostConstructCalled());

        assertFalse(bean.isPreDestroyCalled());

        lifecycle.shutdown();

        assertTrue(bean.isPreDestroyCalled());
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

    static class FailingPostConstructBean {
        private boolean initialized = false;

        @PostConstruct
        void fail() {
            initialized = true;
            throw new RuntimeException("BANG");
        }

        boolean isInitialized() {
            return initialized;
        }
    }

}
