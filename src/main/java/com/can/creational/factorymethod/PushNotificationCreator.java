package com.can.creational.factorymethod;

public class PushNotificationCreator extends NotificationCreator {

    public PushNotificationCreator(NotificationSender sender) {
        super(sender);
    }

    @Override
    public Notification createNotification() {
        return new PushNotification(sender());
    }
}
