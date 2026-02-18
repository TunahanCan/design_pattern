package com.can.creational.factorymethod;

public class SmsNotification extends AbstractMessageNotification {

    public SmsNotification(NotificationSender sender) {
        super(sender);
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }

    @Override
    protected String formatPayload(NotificationRequest request) {
        return String.format("[SMS] To:%s | %s - %s",
                request.recipient(), request.title(), request.message());
    }
}
