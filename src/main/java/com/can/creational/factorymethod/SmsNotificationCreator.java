package com.can.creational.factorymethod;

public class SmsNotificationCreator extends NotificationCreator {

    public SmsNotificationCreator(NotificationSender sender) {
        super(sender);
    }

    @Override
    public Notification createNotification() {
        return new SmsNotification(sender());
    }
}
