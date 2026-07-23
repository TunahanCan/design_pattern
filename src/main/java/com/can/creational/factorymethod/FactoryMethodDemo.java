package com.can.creational.factorymethod;

import java.util.List;
import java.util.Map;

public class FactoryMethodDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("1) Factory Method");

        NotificationSender sender = new ConsoleNotificationSender();
        NotificationService notificationService = new NotificationService(Map.of(
                NotificationChannel.EMAIL, new EmailNotificationCreator(sender),
                NotificationChannel.SMS, new SmsNotificationCreator(sender),
                NotificationChannel.PUSH, new PushNotificationCreator(sender)
        ));

        System.out.println("Temel örnek — tek kanal:");
        notificationService.send(
                NotificationChannel.EMAIL,
                new NotificationRequest("can@example.com", "Order Ready", "Your order has been prepared.")
        );

        System.out.println("Daha gerçekçi örnek — sıralı bildirim işleri:");
        notificationService.sendAll(List.of(
                new NotificationJob(
                        NotificationChannel.SMS,
                        new NotificationRequest("+905551112233", "Shipping Update", "Your cargo is now in transit.")
                ),
                new NotificationJob(
                        NotificationChannel.PUSH,
                        new NotificationRequest("user-42", "Campaign", "A special discount is waiting for you.")
                )
        ));

        System.out.println();
    }
}
