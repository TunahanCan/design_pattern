package com.can.creational.factorymethod;

public class PushNotification extends AbstractMessageNotification {

    public PushNotification(NotificationSender sender) {
        super(sender);
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.PUSH;
    }

    @Override
    protected String formatPayload(NotificationRequest request) {
        return String.format("[PUSH] User:%s | %s -> %s",
                request.recipient(), request.title(), request.message());
    }
}
