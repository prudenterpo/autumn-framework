package io.autumn.core.testfixtures;

import io.autumn.core.annotations.Component;
import jakarta.annotation.PostConstruct;

@Component
public class ServiceA {

    private final ServiceB serviceB;
    private boolean initialized = false;

    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }

    @PostConstruct
    void init() {
        initialized = true;
    }

    public ServiceB getServiceB() {
        return serviceB;
    }

    public boolean isInitialized() {
        return initialized;
    }
}