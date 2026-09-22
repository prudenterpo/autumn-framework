package io.autumn.core.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class AnnotatedBean {

    private boolean postConstructCalled = false;
    private boolean preDestroyCalled = false;

    @PostConstruct
    void init() {
        postConstructCalled = true;
    }

    @PreDestroy
    void cleanup() {
        preDestroyCalled = true;
    }

    public boolean isPostConstructCalled() {
        return postConstructCalled;
    }

    public boolean isPreDestroyCalled() {
        return preDestroyCalled;
    }
}
