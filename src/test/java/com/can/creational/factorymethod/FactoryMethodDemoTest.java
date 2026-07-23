package com.can.creational.factorymethod;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Factory Method — creator ürünü seçer, ortak gönderim akışı değişmez")
class FactoryMethodDemoTest {

    @Nested
    @DisplayName("Creator ve product sözleşmesi")
    class CreatorContract {

        @Test
        @DisplayName("Her concrete creator kendi kanalına ait product'ı üretir")
        void eachConcreteCreatorCreatesItsOwnChannelProduct() {
            // Arrange
            NotificationSender sender = payload -> {
            };
            NotificationCreator emailCreator = new EmailNotificationCreator(sender);
            NotificationCreator smsCreator = new SmsNotificationCreator(sender);
            NotificationCreator pushCreator = new PushNotificationCreator(sender);

            // Act
            Notification email = emailCreator.createNotification();
            Notification sms = smsCreator.createNotification();
            Notification push = pushCreator.createNotification();

            // Assert
            assertAll(
                    () -> assertEquals(NotificationChannel.EMAIL, emailCreator.channel()),
                    () -> assertInstanceOf(EmailNotification.class, email),
                    () -> assertEquals(NotificationChannel.EMAIL, email.channel()),
                    () -> assertEquals(NotificationChannel.SMS, smsCreator.channel()),
                    () -> assertInstanceOf(SmsNotification.class, sms),
                    () -> assertEquals(NotificationChannel.SMS, sms.channel()),
                    () -> assertEquals(NotificationChannel.PUSH, pushCreator.channel()),
                    () -> assertInstanceOf(PushNotification.class, push),
                    () -> assertEquals(NotificationChannel.PUSH, push.channel())
            );
        }

        @Test
        @DisplayName("notifyUser factory method ile ürünü oluşturur ve ortak akışı çalıştırır")
        void notifyUserCreatesThenSendsTheProduct() {
            // Arrange
            InMemoryNotificationSender sender = new InMemoryNotificationSender();
            NotificationCreator creator = new EmailNotificationCreator(sender);
            NotificationRequest request = new NotificationRequest("reader@example.com", "Welcome", "Hello");

            // Act
            creator.notifyUser(request);

            // Assert
            assertEquals(
                    List.of("[EMAIL] To:reader@example.com | Subject:Welcome | Body:Hello"),
                    sender.payloads()
            );
        }

        @Test
        @DisplayName("Eski custom creator kaynak uyumunu korur ve her notify'da yalnız bir product üretir")
        void legacyCustomCreatorRemainsSourceCompatibleAndCreatesOncePerNotification() {
            // Arrange
            InMemoryNotificationSender sender = new InMemoryNotificationSender();
            AtomicInteger creationCount = new AtomicInteger();
            NotificationCreator customCreator = new NotificationCreator(sender) {
                @Override
                public Notification createNotification() {
                    creationCount.incrementAndGet();
                    return new EmailNotification(sender());
                }
            };

            // Act
            customCreator.notifyUser(
                    new NotificationRequest("legacy@example.com", "Compatibility", "Preserved")
            );

            // Assert
            assertAll(
                    () -> assertEquals(1, creationCount.get()),
                    () -> assertEquals(
                            List.of(
                                    "[EMAIL] To:legacy@example.com"
                                            + " | Subject:Compatibility | Body:Preserved"
                            ),
                            sender.payloads()
                    )
            );
        }

        @Test
        @DisplayName("Concrete creator kanal metadata'sını product oluşturmadan verir")
        void concreteCreatorMetadataIsSideEffectFree() {
            // Arrange
            AtomicInteger creationCount = new AtomicInteger();
            NotificationCreator creator = new EmailNotificationCreator(payload -> {
            }) {
                @Override
                public Notification createNotification() {
                    creationCount.incrementAndGet();
                    return super.createNotification();
                }
            };

            // Act
            NotificationChannel first = creator.channel();
            NotificationChannel second = creator.channel();

            // Assert
            assertAll(
                    () -> assertEquals(NotificationChannel.EMAIL, first),
                    () -> assertEquals(NotificationChannel.EMAIL, second),
                    () -> assertEquals(0, creationCount.get())
            );
        }

        @Test
        @DisplayName("Creator yanlış kanaldan product üretirse ortak akış sessizce devam etmez")
        void creatorRejectsAProductFromAnotherChannel() {
            // Arrange
            NotificationCreator brokenCreator = new NotificationCreator(
                    payload -> {
                    },
                    NotificationChannel.EMAIL
            ) {
                @Override
                public Notification createNotification() {
                    return new SmsNotification(payload -> {
                    });
                }
            };

            // Act
            IllegalStateException error = assertThrows(
                    IllegalStateException.class,
                    () -> brokenCreator.notifyUser(
                            new NotificationRequest("user@example.com", "Title", "Message")
                    )
            );

            // Assert
            assertEquals("Creator channel EMAIL produced SMS", error.getMessage());
        }
    }

    @Nested
    @DisplayName("Kanala özgü payload biçimi")
    class PayloadFormatting {

        @Test
        @DisplayName("Aynı istek EMAIL, SMS ve PUSH ürünlerinde farklı biçimlenir")
        void sameRequestIsFormattedAccordingToProductChannel() {
            // Arrange
            InMemoryNotificationSender sender = new InMemoryNotificationSender();
            NotificationRequest request = new NotificationRequest("can@example.com", "Order", "Ready");

            // Act
            new EmailNotification(sender).send(request);
            new SmsNotification(sender).send(request);
            new PushNotification(sender).send(request);

            // Assert
            assertEquals(List.of(
                    "[EMAIL] To:can@example.com | Subject:Order | Body:Ready",
                    "[SMS] To:can@example.com | Order - Ready",
                    "[PUSH] User:can@example.com | Order -> Ready"
            ), sender.payloads());
        }
    }

    @Nested
    @DisplayName("İstek daha kullanılmadan geçerlidir")
    class RequestValidation {

        @Test
        @DisplayName("Null veya blank recipient reddedilir")
        void nullOrBlankRecipientIsRejected() {
            // Arrange & Act
            IllegalArgumentException nullError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NotificationRequest(null, "Title", "Message")
            );
            IllegalArgumentException blankError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NotificationRequest("   ", "Title", "Message")
            );

            // Assert
            assertAll(
                    () -> assertEquals("recipient cannot be blank", nullError.getMessage()),
                    () -> assertEquals("recipient cannot be blank", blankError.getMessage())
            );
        }

        @Test
        @DisplayName("Null veya blank title ve message reddedilir")
        void nullOrBlankTitleAndMessageAreRejected() {
            // Arrange & Act
            IllegalArgumentException titleError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NotificationRequest("user-1", " ", "Message")
            );
            IllegalArgumentException messageError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NotificationRequest("user-1", "Title", null)
            );

            // Assert
            assertAll(
                    () -> assertEquals("title cannot be blank", titleError.getMessage()),
                    () -> assertEquals("message cannot be blank", messageError.getMessage())
            );
        }

        @Test
        @DisplayName("Geçerli alanların çevre boşlukları kayıt sınırında temizlenir")
        void requestFieldsAreNormalizedOnceAtTheBoundary() {
            // Arrange & Act
            NotificationRequest request = new NotificationRequest(
                    "  user@example.com ",
                    "  Welcome ",
                    "  Hello there  "
            );

            // Assert
            assertAll(
                    () -> assertEquals("user@example.com", request.recipient()),
                    () -> assertEquals("Welcome", request.title()),
                    () -> assertEquals("Hello there", request.message())
            );
        }
    }

    @Nested
    @DisplayName("Service kanal ile creator arasında yönlendirme yapar")
    class ServiceRouting {

        @Test
        @DisplayName("Seçilen kanal yalnız eşleşen creator üzerinden gönderilir")
        void selectedChannelIsRoutedToItsCreator() {
            // Arrange
            InMemoryNotificationSender emailSender = new InMemoryNotificationSender();
            InMemoryNotificationSender smsSender = new InMemoryNotificationSender();
            NotificationService service = new NotificationService(Map.of(
                    NotificationChannel.EMAIL, new EmailNotificationCreator(emailSender),
                    NotificationChannel.SMS, new SmsNotificationCreator(smsSender)
            ));

            // Act
            service.send(
                    NotificationChannel.EMAIL,
                    new NotificationRequest("a@b.com", "Subject", "Body")
            );

            // Assert
            assertAll(
                    () -> assertEquals(
                            List.of("[EMAIL] To:a@b.com | Subject:Subject | Body:Body"),
                            emailSender.payloads()
                    ),
                    () -> assertEquals(List.of(), smsSender.payloads())
            );
        }

        @Test
        @DisplayName("Service constructor'da creator map'inin değişmez snapshot'ını alır")
        void serviceKeepsASnapshotOfTheCreatorMap() {
            // Arrange
            InMemoryNotificationSender sender = new InMemoryNotificationSender();
            Map<NotificationChannel, NotificationCreator> mutableCreators = new HashMap<>();
            mutableCreators.put(NotificationChannel.EMAIL, new EmailNotificationCreator(sender));
            NotificationService service = new NotificationService(mutableCreators);

            // Act
            mutableCreators.clear();
            service.send(
                    NotificationChannel.EMAIL,
                    new NotificationRequest("snapshot@example.com", "Snapshot", "Protected")
            );

            // Assert
            assertEquals(1, sender.payloads().size());
        }

        @Test
        @DisplayName("Kayıtlı creator yoksa kanal adıyla anlamlı hata verir")
        void missingCreatorProducesAnExplicitError() {
            // Arrange
            NotificationService service = new NotificationService(Map.of());
            NotificationRequest request = new NotificationRequest("user-1", "Campaign", "Discount");

            // Act
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.send(NotificationChannel.PUSH, request)
            );

            // Assert
            assertEquals("No creator found for channel: PUSH", error.getMessage());
        }

        @Test
        @DisplayName("Map anahtarı ile built-in creator metadata'sı uyuşmazsa wiring hatası başta yakalanır")
        void mismatchedCreatorRegistrationFailsFast() {
            // Arrange
            NotificationCreator emailCreator = new EmailNotificationCreator(payload -> {
            });

            // Act
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> new NotificationService(Map.of(NotificationChannel.SMS, emailCreator))
            );

            // Assert
            assertEquals("Creator registered for SMS declares EMAIL", error.getMessage());
        }

        @Test
        @DisplayName("Stateful legacy creator'da service seçimi gerçek product'ın aynı instance'ıyla doğrulanır")
        void selectedChannelIsCheckedAgainstTheSingleActuallyCreatedProduct() {
            // Arrange
            InMemoryNotificationSender sender = new InMemoryNotificationSender();
            AtomicInteger creationCount = new AtomicInteger();
            NotificationCreator alternatingCreator = new NotificationCreator(sender) {
                @Override
                public Notification createNotification() {
                    return creationCount.incrementAndGet() % 2 == 1
                            ? new EmailNotification(sender())
                            : new SmsNotification(sender());
                }
            };
            NotificationService service = new NotificationService(Map.of(
                    NotificationChannel.EMAIL,
                    alternatingCreator
            ));
            NotificationRequest request = new NotificationRequest("stateful@example.com", "State", "Check");

            // Act
            service.send(NotificationChannel.EMAIL, request);
            IllegalStateException error = assertThrows(
                    IllegalStateException.class,
                    () -> service.send(NotificationChannel.EMAIL, request)
            );

            // Assert
            assertAll(
                    () -> assertEquals(2, creationCount.get(), "Her send product'ı tam bir kez üretmeli"),
                    () -> assertEquals(
                            "Creator registered for EMAIL produced SMS",
                            error.getMessage()
                    ),
                    () -> assertEquals(
                            List.of("[EMAIL] To:stateful@example.com | Subject:State | Body:Check"),
                            sender.payloads()
                    )
            );
        }
    }

    @Nested
    @DisplayName("Daha gerçekçi iş kuyruğu")
    class BatchDispatch {

        @Test
        @DisplayName("sendAll işleri verilen sırada kendi creator'larına yönlendirir")
        void jobsAreDispatchedInOrderToTheirOwnCreators() {
            // Arrange
            InMemoryNotificationSender sharedSender = new InMemoryNotificationSender();
            NotificationService service = new NotificationService(Map.of(
                    NotificationChannel.EMAIL, new EmailNotificationCreator(sharedSender),
                    NotificationChannel.SMS, new SmsNotificationCreator(sharedSender)
            ));

            // Act
            service.sendAll(List.of(
                    new NotificationJob(
                            NotificationChannel.SMS,
                            new NotificationRequest("+905551112233", "Cargo", "In transit")
                    ),
                    new NotificationJob(
                            NotificationChannel.EMAIL,
                            new NotificationRequest("reader@example.com", "Invoice", "Ready")
                    )
            ));

            // Assert
            assertEquals(List.of(
                    "[SMS] To:+905551112233 | Cargo - In transit",
                    "[EMAIL] To:reader@example.com | Subject:Invoice | Body:Ready"
            ), sharedSender.payloads());
        }
    }

    private static final class InMemoryNotificationSender implements NotificationSender {
        private final List<String> payloads = new ArrayList<>();

        @Override
        public void send(String payload) {
            payloads.add(payload);
        }

        List<String> payloads() {
            return List.copyOf(payloads);
        }
    }
}
