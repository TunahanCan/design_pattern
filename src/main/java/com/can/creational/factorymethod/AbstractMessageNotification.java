package com.can.creational.factorymethod;

public abstract class AbstractMessageNotification implements Notification {

    private final NotificationSender sender;

    protected AbstractMessageNotification(NotificationSender sender) {
        this.sender = sender;
    }

    @Override
    public void send(NotificationRequest request) {
        sender.send(formatPayload(request));
    }

    protected abstract String formatPayload(NotificationRequest request);
}
