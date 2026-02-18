package com.can.creational.factorymethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FactoryMethodDemoTest {

    @Nested
    class ProductImplementations {

        // Bu test, her ürünün gönderim çıktısında kendi kanal etiketini kullandığını doğrular.
        @Test
        void shouldPrintCorrectChannelPrefixForEachProduct() {
            String emailOutput = captureOutput(() -> new FactoryMethodDemo.EmailNotification().send("hello"));
            String smsOutput = captureOutput(() -> new FactoryMethodDemo.SmsNotification().send("hello"));
            String pushOutput = captureOutput(() -> new FactoryMethodDemo.PushNotification().send("hello"));

            assertTrue(emailOutput.contains("[Email] hello"));
            assertTrue(smsOutput.contains("[SMS] hello"));
            assertTrue(pushOutput.contains("[Push] hello"));
        }
    }

    @Nested
    class CreatorFactoryMethod {

        // Bu test, her creator sınıfının doğru product türünü ürettiğini doğrular.
        @Test
        void shouldCreateExpectedNotificationType() {
            FactoryMethodDemo.NotificationCreator emailCreator = new FactoryMethodDemo.EmailNotificationCreator();
            FactoryMethodDemo.NotificationCreator smsCreator = new FactoryMethodDemo.SmsNotificationCreator();
            FactoryMethodDemo.NotificationCreator pushCreator = new FactoryMethodDemo.PushNotificationCreator();

            assertInstanceOf(FactoryMethodDemo.EmailNotification.class, emailCreator.createNotification());
            assertInstanceOf(FactoryMethodDemo.SmsNotification.class, smsCreator.createNotification());
            assertInstanceOf(FactoryMethodDemo.PushNotification.class, pushCreator.createNotification());
        }
    }

    @Nested
    class CreatorBusinessFlow {

        // Bu test, creator içindeki iş akışının createNotification + send zincirini doğru çalıştırdığını doğrular.
        @Test
        void shouldNotifyUserWithExpectedMessage() {
            FactoryMethodDemo.NotificationCreator creator = new FactoryMethodDemo.EmailNotificationCreator();

            String output = captureOutput(() -> creator.notifyUser("order-ready"));

            assertEquals("[Email] order-ready", output.trim());
        }
    }

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));
            action.run();
            return outputStream.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOut);
        }
    }
}
