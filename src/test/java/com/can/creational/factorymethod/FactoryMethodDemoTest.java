package com.can.creational.factorymethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FactoryMethodDemoTest {

    @Nested
    class CreatorFactoryMethod {

        // Bu test, her creator sınıfının doğru product nesnesini ürettiğini doğrular.
        @Test
        void shouldCreateExpectedNotificationImplementation() {
            NotificationSender sender = payload -> {
            };

            NotificationCreator emailCreator = new EmailNotificationCreator(sender);
            NotificationCreator smsCreator = new SmsNotificationCreator(sender);
            NotificationCreator pushCreator = new PushNotificationCreator(sender);

            assertInstanceOf(EmailNotification.class, emailCreator.createNotification());
            assertInstanceOf(SmsNotification.class, smsCreator.createNotification());
            assertInstanceOf(PushNotification.class, pushCreator.createNotification());
        }
    }

    @Nested
    class NotificationFormatting {

        // Bu test, ürünlerin beklenen formatta payload ürettiğini doğrular.
        @Test
        void shouldFormatPayloadByChannel() {
            InMemoryNotificationSender sender = new InMemoryNotificationSender();
            NotificationRequest request = new NotificationRequest("can@example.com", "Order", "Ready");

            new EmailNotification(sender).send(request);
            new SmsNotification(sender).send(request);
            new PushNotification(sender).send(request);

            assertEquals("[EMAIL] To:can@example.com | Subject:Order | Body:Ready", sender.payloads().get(0));
            assertEquals("[SMS] To:can@example.com | Order - Ready", sender.payloads().get(1));
            assertEquals("[PUSH] User:can@example.com | Order -> Ready", sender.payloads().get(2));
        }
    }

    @Nested
    class ServiceFlow {

        // Bu test, servis katmanının doğru creator ile bildirimi gönderdiğini doğrular.
        @Test
        void shouldRouteRequestToMatchingCreator() {
            InMemoryNotificationSender sender = new InMemoryNotificationSender();

            NotificationService service = new NotificationService(Map.of(
                    NotificationChannel.EMAIL, new EmailNotificationCreator(sender),
                    NotificationChannel.SMS, new SmsNotificationCreator(sender)
            ));

            service.send(NotificationChannel.EMAIL, new NotificationRequest("a@b.com", "Subject", "Body"));

            assertEquals(1, sender.payloads().size());
            assertEquals("[EMAIL] To:a@b.com | Subject:Subject | Body:Body", sender.payloads().getFirst());
        }

        // Bu test, creator tanımlı olmayan kanal için anlamlı hata fırlatıldığını doğrular.
        @Test
        void shouldThrowWhenCreatorIsMissing() {
            NotificationService service = new NotificationService(Map.of());

            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> service.send(NotificationChannel.PUSH,
                            new NotificationRequest("user-1", "Campaign", "Discount")));

            assertEquals("No creator found for channel: PUSH", error.getMessage());
        }
    }

    private static final class InMemoryNotificationSender implements NotificationSender {
        private final List<String> payloads = new ArrayList<>();

        @Override
        public void send(String payload) {
            payloads.add(payload);
        }

        List<String> payloads() {
            return payloads;
        }
    }
}
