package com.can.structural.decorator;

import java.util.Locale;

public class PriorityDecorator extends BaseNotifierDecorator {

    private final String priority;

    public PriorityDecorator(Notifier wrappee, String priority) {
        super(wrappee);
        this.priority = Notifier.requireText(priority, "priority").toUpperCase(Locale.ROOT);
    }

    @Override
    public String send(String message) {
        String prioritizedMessage = "[%s] %s".formatted(
            priority,
            Notifier.requireText(message, "message")
        );
        return super.send(prioritizedMessage);
    }
}
