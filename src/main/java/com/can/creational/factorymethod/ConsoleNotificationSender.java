package com.can.creational.factorymethod;

public class ConsoleNotificationSender implements NotificationSender {

    @Override
    public void send(String payload) {
        System.out.println(payload);
    }
}
