package io.autumn.core.lifecycle;

import io.autumn.core.annotations.Component;
import io.autumn.core.annotations.Scope;
import jakarta.annotation.PreDestroy;

@Component
@Scope("prototype")
public class PrototypeWithPreDestroyBean {

    private boolean destroyed = false;

    @PreDestroy
    void cleanup() {
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
