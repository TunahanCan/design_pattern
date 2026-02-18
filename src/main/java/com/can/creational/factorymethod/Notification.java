package com.can.creational.factorymethod;

public interface Notification {
    NotificationChannel channel();

    void send(NotificationRequest request);
}
