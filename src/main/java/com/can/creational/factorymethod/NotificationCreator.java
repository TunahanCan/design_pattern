package com.can.creational.factorymethod;

public abstract class NotificationCreator {

    private final NotificationSender sender;

    protected NotificationCreator(NotificationSender sender) {
        this.sender = sender;
    }

    protected NotificationSender sender() {
        return sender;
    }

    public abstract Notification createNotification();

    public void notifyUser(NotificationRequest request) {
        Notification notification = createNotification();
        notification.send(request);
    }
}
