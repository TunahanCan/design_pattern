package com.can.creational.factorymethod;

import java.util.Objects;

public abstract class AbstractMessageNotification implements Notification {

    private final NotificationSender sender;

    protected AbstractMessageNotification(NotificationSender sender) {
        this.sender = Objects.requireNonNull(sender, "sender cannot be null");
    }

    @Override
    public void send(NotificationRequest request) {
        sender.send(formatPayload(Objects.requireNonNull(request, "request cannot be null")));
    }

    protected abstract String formatPayload(NotificationRequest request);
}
