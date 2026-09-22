package io.autumn.core.testfixtures;

import io.autumn.core.annotations.Component;
import jakarta.annotation.PostConstruct;

@Component
public class FailingPostConstructBean {

    private boolean initialized = false;

    @PostConstruct
    void fail() {
        initialized = true;
        throw new RuntimeException("BANG from @PostConstruct");
    }

    public boolean isInitialized() {
        return initialized;
    }
}
