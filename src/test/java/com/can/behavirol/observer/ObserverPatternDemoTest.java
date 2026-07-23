package com.can.behavirol.observer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Observer — ürün bazlı mağaza abonelikleri")
class ObserverPatternDemoTest {

    @Nested
    @DisplayName("Subscriber ürünlere abone olduğunda")
    class SubscriptionManagement {

        @Test
        @DisplayName("yalnız abone olunan ürünün bildirimi alınır")
        void subscriberReceivesOnlyTheSelectedProduct() {
            // Arrange
            Store store = new Store();
            RecordingSubscriber subscriber = new RecordingSubscriber("can");
            RecordingSubscriber consoleSubscriber = new RecordingSubscriber("console");
            store.subscribe("Telefon", subscriber);
            store.subscribe("Konsol", consoleSubscriber);

            // Act
            store.notifySubscribers("Konsol", "Konsol stokta");
            store.notifySubscribers("Telefon", "Telefon stokta");

            // Assert
            assertAll(
                    () -> assertEquals(1, subscriber.events.size()),
                    () -> assertEquals(
                            new Event("Telefon", "Telefon stokta", "can"),
                            subscriber.events.getFirst()
                    ),
                    () -> assertEquals(1, consoleSubscriber.events.size())
            );
        }

        @Test
        @DisplayName("aynı ürüne kayıt sırasıyla birden fazla subscriber bildirilebilir")
        void allSubscribersAreNotifiedInRegistrationOrder() {
            // Arrange
            Store store = new Store();
            List<String> deliveryOrder = new ArrayList<>();
            store.subscribe("Telefon", (product, message) -> deliveryOrder.add("birinci"));
            store.subscribe("Telefon", (product, message) -> deliveryOrder.add("ikinci"));

            // Act
            store.notifySubscribers("Telefon", "Stok geldi");

            // Assert
            assertEquals(List.of("birinci", "ikinci"), deliveryOrder);
        }
    }

    @Nested
    @DisplayName("Subscriber abonelikten çıkarıldığında")
    class Unsubscription {

        @Test
        @DisplayName("çıkarılan subscriber sonraki bildirimi almaz")
        void unsubscribedListenerReceivesNoFurtherEvents() {
            // Arrange
            Store store = new Store();
            RecordingSubscriber can = new RecordingSubscriber("can");
            RecordingSubscriber ayse = new RecordingSubscriber("ayse");
            store.subscribe("Telefon", can);
            store.subscribe("Telefon", ayse);

            // Act
            store.unsubscribe("Telefon", ayse);
            store.notifySubscribers("Telefon", "Yeni stok");

            // Assert
            assertAll(
                    () -> assertEquals(1, can.events.size()),
                    () -> assertTrue(ayse.events.isEmpty())
            );
        }

        @Test
        @DisplayName("olmayan üründen unsubscribe güvenli bir no-op'tur")
        void unsubscribingFromUnknownProductDoesNothing() {
            // Arrange
            Store store = new Store();
            RecordingSubscriber subscriber = new RecordingSubscriber("can");

            // Act
            store.unsubscribe("Olmayan", subscriber);

            // Assert
            assertTrue(subscriber.events.isEmpty());
        }
    }

    @Nested
    @DisplayName("Store restock olayı yayınladığında")
    class RestockNotification {

        @Test
        @DisplayName("ürün adı ve stok adedi mesajla subscriber'a taşınır")
        void restockBuildsAndPublishesTheMessage() {
            // Arrange
            Store store = new Store();
            RecordingSubscriber subscriber = new RecordingSubscriber("can");
            store.subscribe("iPhone 16", subscriber);

            // Act
            store.restockProduct("iPhone 16", 12);

            // Assert
            assertEquals(
                    new Event(
                            "iPhone 16",
                            "iPhone 16 tekrar stokta! Adet: 12",
                            "can"
                    ),
                    subscriber.events.getFirst()
            );
        }

        @Test
        @DisplayName("farklı ürünlerin subscriber listeleri birbirinden bağımsızdır")
        void productChannelsRemainIndependent() {
            // Arrange
            Store store = new Store();
            RecordingSubscriber phoneSubscriber = new RecordingSubscriber("telefon");
            RecordingSubscriber consoleSubscriber = new RecordingSubscriber("konsol");
            store.subscribe("Telefon", phoneSubscriber);
            store.subscribe("Konsol", consoleSubscriber);

            // Act
            store.restockProduct("Konsol", 3);

            // Assert
            assertAll(
                    () -> assertTrue(phoneSubscriber.events.isEmpty()),
                    () -> assertEquals(1, consoleSubscriber.events.size())
            );
        }
    }

    @Nested
    @DisplayName("Abonelik yaşam döngüsü callback sırasında değiştiğinde")
    class SubscriptionLifecycle {

        @Test
        @DisplayName("aynı subscriber iki kez eklense bile tek bildirim alır")
        void duplicateSubscriptionIsIdempotent() {
            // Arrange
            Store store = new Store();
            RecordingSubscriber subscriber = new RecordingSubscriber("can");
            store.subscribe("Telefon", subscriber);
            store.subscribe("Telefon", subscriber);

            // Act
            store.notifySubscribers("Telefon", "Stok geldi");

            // Assert
            assertEquals(1, subscriber.events.size());
        }

        @Test
        @DisplayName("subscription handle kapatılınca abonelik idempotent biçimde sona erer")
        void closingSubscriptionHandleUnsubscribesOnce() {
            // Arrange
            Store store = new Store();
            RecordingSubscriber subscriber = new RecordingSubscriber("can");
            Subscription subscription = store.subscribeWithHandle("Telefon", subscriber);

            // Act
            subscription.close();
            subscription.close();
            store.notifySubscribers("Telefon", "Stok geldi");

            // Assert
            assertTrue(subscriber.events.isEmpty());
        }

        @Test
        @DisplayName("iki bağımsız handle'dan biri kapanınca diğeri aboneliği aktif tutar")
        void eachHandleOwnsOneReferenceToTheSubscription() {
            // Arrange
            Store store = new Store();
            RecordingSubscriber subscriber = new RecordingSubscriber("can");
            Subscription first = store.subscribeWithHandle("Telefon", subscriber);
            Subscription second = store.subscribeWithHandle("Telefon", subscriber);

            // Act
            first.close();
            store.notifySubscribers("Telefon", "İlk yayın");
            second.close();
            store.notifySubscribers("Telefon", "İkinci yayın");

            // Assert
            assertEquals(
                    List.of(new Event("Telefon", "İlk yayın", "can")),
                    subscriber.events
            );
        }

        @Test
        @DisplayName("handle kapanışı aynı subscriber'ın doğrudan aboneliğini kaldırmaz")
        void handleAndDirectSubscriptionHaveIndependentOwnership() {
            // Arrange
            Store store = new Store();
            RecordingSubscriber subscriber = new RecordingSubscriber("can");
            store.subscribe("Telefon", subscriber);
            Subscription handle = store.subscribeWithHandle("Telefon", subscriber);

            // Act
            handle.close();
            store.notifySubscribers("Telefon", "Doğrudan abonelik sürüyor");
            store.unsubscribe("Telefon", subscriber);
            store.notifySubscribers("Telefon", "Abonelik bitti");

            // Assert
            assertEquals(
                    List.of(
                            new Event(
                                    "Telefon",
                                    "Doğrudan abonelik sürüyor",
                                    "can"
                            )
                    ),
                    subscriber.events
            );
        }

        @Test
        @DisplayName("subscriber callback içinde kendini çıkarsa da mevcut yayın güvenle tamamlanır")
        void subscriberCanUnsubscribeItselfDuringNotification() {
            // Arrange
            Store store = new Store();
            List<String> deliveries = new ArrayList<>();
            Subscription[] selfSubscription = new Subscription[1];
            selfSubscription[0] = store.subscribeWithHandle("Telefon", (product, message) -> {
                deliveries.add("self");
                selfSubscription[0].close();
            });
            store.subscribe("Telefon", (product, message) -> deliveries.add("other"));

            // Act
            store.notifySubscribers("Telefon", "İlk");
            store.notifySubscribers("Telefon", "İkinci");

            // Assert
            assertEquals(List.of("self", "other", "other"), deliveries);
        }
    }

    private static final class RecordingSubscriber implements Subscriber {
        private final String subscriberName;
        private final List<Event> events = new ArrayList<>();

        private RecordingSubscriber(String subscriberName) {
            this.subscriberName = subscriberName;
        }

        @Override
        public void update(String productName, String message) {
            events.add(new Event(productName, message, subscriberName));
        }
    }

    private record Event(String productName, String message, String subscriberName) {
    }
}
