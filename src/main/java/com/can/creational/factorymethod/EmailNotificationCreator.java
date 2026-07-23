package com.can.creational.factorymethod;

public class EmailNotificationCreator extends NotificationCreator {

    public EmailNotificationCreator(NotificationSender sender) {
        super(sender, NotificationChannel.EMAIL);
    }

    @Override
    public Notification createNotification() {
        return new EmailNotification(sender());
    }
}
