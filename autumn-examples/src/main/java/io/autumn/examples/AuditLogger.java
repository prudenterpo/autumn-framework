package io.autumn.examples;

import io.autumn.core.annotations.Component;
import io.autumn.core.annotations.Scope;

import java.util.UUID;

@Component
@Scope("prototype")
public class AuditLogger {

    private final String instanceId;

    public AuditLogger() {
        this.instanceId = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("[AuditLogger] New instance created: " + instanceId);
    }

    public void log(String action) {
        System.out.println("[AuditLogger-" + instanceId + "] " + action);
    }
}