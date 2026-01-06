package io.autumn.examples;

import io.autumn.core.annotations.Component;
import io.autumn.core.annotations.Primary;

@Component
@Primary
public class EmailSender implements NotificationSender {

    @Override
    public void send(String message) {
        System.out.println("[EMAIL] " + message);
    }
}