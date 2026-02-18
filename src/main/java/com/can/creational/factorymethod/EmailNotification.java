package com.can.creational.factorymethod;

public class EmailNotification extends AbstractMessageNotification {

    public EmailNotification(NotificationSender sender) {
        super(sender);
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    protected String formatPayload(NotificationRequest request) {
        return String.format("[EMAIL] To:%s | Subject:%s | Body:%s",
                request.recipient(), request.title(), request.message());
    }
}
