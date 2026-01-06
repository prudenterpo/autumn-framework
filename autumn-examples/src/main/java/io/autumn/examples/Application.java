package io.autumn.examples;

import io.autumn.core.Autumn;
import io.autumn.core.context.AutumnContext;

public class Application {

    public static void main(String[] args) {
        System.out.println("=== Starting Autumn Example ===\n");

        AutumnContext context = Autumn.start("io.autumn.examples");

        System.out.println("\n=== Testing OrderService ===\n");

        OrderService orderService = context.getBean(OrderService.class);
        orderService.processOrder(123L);

        System.out.println("\n=== Testing Prototype Scope ===\n");

        AuditLogger logger1 = context.getBean(AuditLogger.class);
        AuditLogger logger2 = context.getBean(AuditLogger.class);

        logger1.log("First action");
        logger2.log("Second Action");

        System.out.println("\nlogger1 == logger2 ? " + (logger1 == logger2));

        System.out.println("\n=== Example Complete! ===");
    }
}
