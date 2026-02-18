package com.can.creational.factorymethod;

public class EmailNotificationCreator extends NotificationCreator {

    public EmailNotificationCreator(NotificationSender sender) {
        super(sender);
    }

    @Override
    public Notification createNotification() {
        return new EmailNotification(sender());
    }
}
