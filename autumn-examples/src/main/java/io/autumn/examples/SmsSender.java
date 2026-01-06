package io.autumn.examples;

import io.autumn.core.annotations.Component;

@Component
public class SmsSender implements NotificationSender {

    @Override
    public void send(String message) {
        System.out.println("[SMS] " + message);
    }
}