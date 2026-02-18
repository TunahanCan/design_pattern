package com.can.creational.factorymethod;

public class FactoryMethodDemo {

    interface Notification {
        void send(String message);
    }

    static class EmailNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("[Email] " + message);
        }
    }

    static class SmsNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("[SMS] " + message);
        }
    }

    static class PushNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("[Push] " + message);
        }
    }

    static abstract class NotificationCreator {
        public abstract Notification createNotification();

        public void notifyUser(String message) {
            Notification notification = createNotification();
            notification.send(message);
        }
    }

    static class EmailNotificationCreator extends NotificationCreator {
        @Override
        public Notification createNotification() {
            return new EmailNotification();
        }
    }

    static class SmsNotificationCreator extends NotificationCreator {
        @Override
        public Notification createNotification() {
            return new SmsNotification();
        }
    }

    static class PushNotificationCreator extends NotificationCreator {
        @Override
        public Notification createNotification() {
            return new PushNotification();
        }
    }

    public static void run() {
        System.out.println("1) Factory Method");

        NotificationCreator creator = new EmailNotificationCreator();
        creator.notifyUser("Siparişin hazırlandı.");

        creator = new SmsNotificationCreator();
        creator.notifyUser("Kargon yola çıktı.");

        creator = new PushNotificationCreator();
        creator.notifyUser("Yeni kampanya seni bekliyor.");

        System.out.println();
    }
}
