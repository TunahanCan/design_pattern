package com.can.structural.decorator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Decorator | Bildirim kanallarını çalışma anında katmanlama")
class DecoratorPatternDemoTest {

    @Nested
    @DisplayName("Temel bildirim bileşeni")
    class BaseNotification {

        @Test
        @DisplayName("EmailNotifier alıcıları ve mesajı belirlenen formatta üretmelidir")
        void shouldFormatEmailNotificationExactly() {
            // Arrange
            Notifier notifier = new EmailNotifier(List.of("ops@company.com", "owner@company.com"));

            // Act
            String result = notifier.send("Alarm");

            // Assert
            assertEquals(
                "Email -> [ops@company.com, owner@company.com] | mesaj=Alarm",
                result
            );
        }

        @Test
        @DisplayName("EmailNotifier constructor'a verilen alıcı listesinin savunmacı kopyasını tutmalıdır")
        void shouldDefensivelyCopyRecipients() {
            // Arrange
            List<String> recipients = new ArrayList<>(List.of("first@company.com"));
            Notifier notifier = new EmailNotifier(recipients);

            // Act
            recipients.add("later@company.com");
            String result = notifier.send("Alarm");

            // Assert
            assertEquals("Email -> [first@company.com] | mesaj=Alarm", result);
        }
    }

    @Nested
    @DisplayName("Katmanlı decorator bileşimi")
    class DecoratorStack {

        @Test
        @DisplayName("Email, SMS ve Slack çıktıları içten dışa doğru tam sırayla üretilmelidir")
        void shouldSendViaEmailSmsAndSlackInStackOrder() {
            // Arrange
            Notifier notifier = new SlackDecorator(
                new SmsDecorator(
                    new EmailNotifier(List.of("ops@company.com")),
                    "+90 555 111 22 33"
                ),
                "#kritik-alarm"
            );
            String lineSeparator = System.lineSeparator();
            String expected = "Email -> [ops@company.com] | mesaj=Alarm"
                + lineSeparator
                + "SMS -> +90 555 111 22 33 | mesaj=Alarm"
                + lineSeparator
                + "Slack -> #kritik-alarm | mesaj=Alarm";

            // Act
            String result = notifier.send("Alarm");

            // Assert
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Aynı Notifier kontratıyla farklı bir Email ve Facebook stack'i kurulabilmelidir")
        void shouldAllowDifferentDecoratorStack() {
            // Arrange
            Notifier notifier = new FacebookDecorator(
                new EmailNotifier(List.of("marketing@company.com")),
                "company.page"
            );
            String expected = "Email -> [marketing@company.com] | mesaj=Kampanya"
                + System.lineSeparator()
                + "Facebook -> company.page | mesaj=Kampanya";

            // Act
            String result = notifier.send("Kampanya");

            // Assert
            assertAll(
                () -> assertEquals(expected, result),
                () -> assertEquals(2, result.lines().count())
            );
        }

        @Test
        @DisplayName("Dıştaki PriorityDecorator mesajı bütün iç kanallara göndermeden önce zenginleştirmelidir")
        void shouldTransformMessageBeforeDelegatingToEveryChannel() {
            // Arrange
            Notifier notifier = new PriorityDecorator(
                new SlackDecorator(
                    new EmailNotifier(List.of("ops@company.com")),
                    "#incident"
                ),
                "p1"
            );
            String lineSeparator = System.lineSeparator();

            // Act
            String result = notifier.send("Veritabanı erişilemiyor");

            // Assert
            assertEquals(
                "Email -> [ops@company.com] | mesaj=[P1] Veritabanı erişilemiyor"
                    + lineSeparator
                    + "Slack -> #incident | mesaj=[P1] Veritabanı erişilemiyor",
                result
            );
        }
    }

    @Nested
    @DisplayName("Bildirim sınırları")
    class NotificationBoundaries {

        @Test
        @DisplayName("Eksik wrappee, hedef veya mesaj hatası çağrı zincirinin başında görünmelidir")
        void shouldRejectInvalidDecoratorConfigurationAndMessage() {
            // Arrange
            Notifier validNotifier = new EmailNotifier(List.of("ops@company.com"));

            // Act & Assert
            assertAll(
                () -> assertThrows(
                    NullPointerException.class,
                    () -> new SmsDecorator(null, "+90 555")
                ),
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> new SlackDecorator(validNotifier, " ")
                ),
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> new EmailNotifier(List.of())
                ),
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> validNotifier.send(" ")
                )
            );
        }
    }
}
