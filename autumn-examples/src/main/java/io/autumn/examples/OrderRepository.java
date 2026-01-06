package io.autumn.examples;

import io.autumn.core.annotations.Component;
import jakarta.annotation.PostConstruct;

@Component
public class OrderRepository {

    @PostConstruct
    public void init() {
        System.out.println("[OrderRepository] Initialized - connecting to database...");
    }

    public String findOrder(Long id) {
        return "Order #" + id;
    }
}