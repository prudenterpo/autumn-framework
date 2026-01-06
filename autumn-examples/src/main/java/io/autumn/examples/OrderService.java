package io.autumn.examples;

import io.autumn.core.annotations.Component;

@Component
public class OrderService {

    private final OrderRepository repository;
    private final NotificationSender notificationSender;

    public OrderService(OrderRepository repository, NotificationSender notificationSender) {
        this.repository = repository;
        this.notificationSender = notificationSender;
    }

    public void processOrder(Long orderId) {
        String order = repository.findOrder(orderId);
        System.out.println("[OrderService] Processing: " + order);
        notificationSender.send("Your " + order + " has been processed!");
    }
}