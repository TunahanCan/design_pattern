package com.can.behavirol.chainofresponsibility;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Chain of Responsibility — sipariş istek hattı")
class ChainOfResponsibilityDemoTest {

    @Nested
    @DisplayName("İstek zincire ilk kez girdiğinde")
    class RequestValidation {

        @Test
        @DisplayName("eksik payload authentication'dan önce typed ret sonucuna dönüşür")
        void missingPayloadStopsBeforeAuthentication() {
            // Arrange
            OrderRequest request = new OrderRequest(
                    "can",
                    "1234",
                    "10.0.0.30",
                    OrderOperation.CREATE_ORDER,
                    null
            );

            // Act
            boolean accepted = createChain().handle(request);

            // Assert
            assertAll(
                    () -> assertFalse(accepted),
                    () -> assertEquals(OrderRequestOutcome.REJECTED, request.getOutcome()),
                    () -> assertEquals(
                            "Zorunlu istek alanı eksik: payload.",
                            request.getOutcomeMessage()
                    ),
                    () -> assertNull(request.getAuthenticatedUser())
            );
        }

        @Test
        @DisplayName("eksik operation sonraki handler'larda NPE olmak yerine sınırda reddedilir")
        void missingOperationIsRejectedAtTheBoundary() {
            // Arrange
            OrderRequest request = new OrderRequest(
                    "can",
                    "1234",
                    "10.0.0.31",
                    null,
                    "Sipariş"
            );

            // Act
            boolean accepted = createChain().handle(request);

            // Assert
            assertAll(
                    () -> assertFalse(accepted),
                    () -> assertEquals(OrderRequestOutcome.REJECTED, request.getOutcome()),
                    () -> assertEquals(
                            "Zorunlu istek alanı eksik: operation.",
                            request.getOutcomeMessage()
                    )
            );
        }

        @Test
        @DisplayName("sonuç yazılabilecek request nesnesi yoksa açık hata üretilir")
        void nullRequestFailsFast() {
            // Arrange
            OrderRequestHandler chain = createChain();

            // Act
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> chain.handle(null)
            );

            // Assert
            assertEquals("request cannot be null", error.getMessage());
        }

        @Test
        @DisplayName("geçersiz brute-force eşiği bütün IP'leri yanlışlıkla bloklamadan fail-fast olur")
        void bruteForceThresholdMustBePositive() {
            // Arrange & Act
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> new LoginAttemptService(0)
            );

            // Assert
            assertEquals("maxFailedAttempts must be positive", error.getMessage());
        }
    }

    @Nested
    @DisplayName("İstek bütün kontrolleri geçtiğinde")
    class SuccessfulRequests {

        @Test
        @DisplayName("kimliği doğrulanmış kullanıcı sipariş oluşturabilir ve payload temizlenir")
        void authenticatedUserCanCreateOrderAndPayloadIsSanitized() {
            // Arrange
            OrderRequestHandler chain = createChain();
            OrderRequest request = new OrderRequest(
                    "can",
                    "1234",
                    "10.0.0.1",
                    OrderOperation.CREATE_ORDER,
                    "  <b>Yeni Sipariş</b>  "
            );

            // Act
            boolean result = chain.handle(request);

            // Assert
            assertAll(
                    () -> assertTrue(result),
                    () -> assertEquals("bYeni Sipariş/b", request.getPayload()),
                    () -> assertEquals("can", request.getAuthenticatedUser().username())
            );
        }

        @Test
        @DisplayName("admin kullanıcısı tüm siparişleri görüntüleyebilir")
        void adminCanViewAllOrders() {
            // Arrange
            OrderRequestHandler chain = createChain();
            OrderRequest request = new OrderRequest(
                    "admin",
                    "root",
                    "10.0.0.2",
                    OrderOperation.VIEW_ALL_ORDERS,
                    "Özet"
            );

            // Act
            boolean result = chain.handle(request);

            // Assert
            assertAll(
                    () -> assertTrue(result),
                    () -> assertTrue(request.getAuthenticatedUser().admin())
            );
        }
    }

    @Nested
    @DisplayName("Bir kontrol isteği reddettiğinde")
    class RejectedRequests {

        @Test
        @DisplayName("yanlış parola zinciri authentication adımında durdurur")
        void wrongPasswordStopsTheChain() {
            // Arrange
            OrderRequestHandler chain = createChain();
            OrderRequest request = new OrderRequest(
                    "can",
                    "yanlış",
                    "10.0.0.3",
                    OrderOperation.CREATE_ORDER,
                    "Sipariş"
            );

            // Act
            boolean result = chain.handle(request);

            // Assert
            assertAll(
                    () -> assertFalse(result),
                    () -> assertNull(request.getAuthenticatedUser()),
                    () -> assertEquals("Sipariş", request.getPayload())
            );
        }

        @Test
        @DisplayName("admin olmayan kullanıcı tüm siparişleri görüntüleyemez")
        void nonAdminCannotViewAllOrders() {
            // Arrange
            OrderRequestHandler chain = createChain();
            OrderRequest request = new OrderRequest(
                    "can",
                    "1234",
                    "10.0.0.4",
                    OrderOperation.VIEW_ALL_ORDERS,
                    "Hepsini getir"
            );

            // Act
            boolean result = chain.handle(request);

            // Assert
            assertAll(
                    () -> assertFalse(result),
                    () -> assertEquals("can", request.getAuthenticatedUser().username())
            );
        }

        @Test
        @DisplayName("üç başarısız girişten sonra IP bloklu kabul edilir")
        void ipBecomesBlockedAtConfiguredThreshold() {
            // Arrange
            LoginAttemptService attempts = new LoginAttemptService(3);
            OrderRequestHandler chain = createChain(attempts, new RequestCache());
            String ipAddress = "10.0.0.5";

            // Act
            for (int attempt = 0; attempt < 3; attempt++) {
                chain.handle(new OrderRequest(
                        "can",
                        "yanlış",
                        ipAddress,
                        OrderOperation.CREATE_ORDER,
                        "Sipariş-" + attempt
                ));
            }

            // Assert
            assertTrue(attempts.isBlocked(ipAddress));
        }

        @Test
        @DisplayName("başarılı giriş aynı IP için başarısız deneme sayacını sıfırlar")
        void successfulAuthenticationResetsFailedAttempts() {
            // Arrange
            LoginAttemptService attempts = new LoginAttemptService(3);
            OrderRequestHandler chain = createChain(attempts, new RequestCache());
            String ipAddress = "10.0.0.6";
            chain.handle(new OrderRequest(
                    "can", "yanlış", ipAddress, OrderOperation.CREATE_ORDER, "İlk deneme"
            ));

            // Act
            boolean result = chain.handle(new OrderRequest(
                    "can", "1234", ipAddress, OrderOperation.CREATE_ORDER, "Başarılı deneme"
            ));
            chain.handle(new OrderRequest(
                    "can", "yanlış", ipAddress, OrderOperation.CREATE_ORDER, "Reset sonrası-1"
            ));
            chain.handle(new OrderRequest(
                    "can", "yanlış", ipAddress, OrderOperation.CREATE_ORDER, "Reset sonrası-2"
            ));

            // Assert
            assertAll(
                    () -> assertTrue(result),
                    () -> assertFalse(attempts.isBlocked(ipAddress))
            );
        }

        @Test
        @DisplayName("yalnız işaretlerden oluşan payload temizleme sonrası boşsa işlenmez")
        void payloadThatBecomesBlankAfterSanitizationIsRejected() {
            // Arrange
            OrderRequest request = new OrderRequest(
                    "can",
                    "1234",
                    "10.0.0.32",
                    OrderOperation.CREATE_ORDER,
                    "<>"
            );

            // Act
            boolean accepted = createChain().handle(request);

            // Assert
            assertAll(
                    () -> assertFalse(accepted),
                    () -> assertEquals("", request.getPayload()),
                    () -> assertEquals("can", request.getAuthenticatedUser().username()),
                    () -> assertEquals(OrderRequestOutcome.REJECTED, request.getOutcome()),
                    () -> assertEquals(
                            "Payload temizleme sonrasında boş olamaz.",
                            request.getOutcomeMessage()
                    )
            );
        }
    }

    @Nested
    @DisplayName("Başarılı istek imzası daha önce görüldüğünde")
    class CacheShortCircuit {

        @Test
        @DisplayName("ilk çağrı true, aynı imzalı ikinci çağrı false döner")
        void repeatedSuccessfulRequestIsShortCircuited() {
            // Arrange
            OrderRequestHandler chain = createChain();
            OrderRequest first = adminViewRequest("10.0.0.7", "Özet");
            OrderRequest second = adminViewRequest("10.0.0.7", "Özet");

            // Act
            boolean firstResult = chain.handle(first);
            boolean secondResult = chain.handle(second);

            // Assert
            assertAll(
                    () -> assertTrue(firstResult),
                    () -> assertFalse(secondResult)
            );
        }

        @Test
        @DisplayName("payload değişirse yeni imza zincirin sonuna kadar ilerler")
        void differentPayloadCreatesDifferentSignature() {
            // Arrange
            OrderRequestHandler chain = createChain();

            // Act
            boolean firstResult = chain.handle(adminViewRequest("10.0.0.8", "Özet-1"));
            boolean secondResult = chain.handle(adminViewRequest("10.0.0.8", "Özet-2"));

            // Assert
            assertAll(
                    () -> assertTrue(firstResult),
                    () -> assertTrue(secondResult)
            );
        }
    }

    @Nested
    @DisplayName("Boolean sonucun yanında iş sonucu sorgulandığında")
    class OutcomeSemantics {

        @Test
        @DisplayName("başarılı terminal handler isteği PROCESSED olarak işaretler")
        void successfulRequestHasProcessedOutcome() {
            // Arrange
            OrderRequest request = adminViewRequest("10.0.0.20", "Yeni rapor");

            // Act
            boolean accepted = createChain().handle(request);

            // Assert
            assertAll(
                    () -> assertTrue(accepted),
                    () -> assertEquals(OrderRequestOutcome.PROCESSED, request.getOutcome()),
                    () -> assertEquals(
                            "Sipariş isteği başarıyla işlendi.",
                            request.getOutcomeMessage()
                    )
            );
        }

        @Test
        @DisplayName("order processing terminaldir; sonradan bağlanan handler sonucu çelişkili hale getiremez")
        void orderProcessingIsATerminalHandler() {
            // Arrange
            AtomicBoolean downstreamCalled = new AtomicBoolean();
            OrderRequestHandler unexpectedDownstream = new OrderRequestHandler() {
                @Override
                public OrderRequestHandler setNext(OrderRequestHandler next) {
                    return next;
                }

                @Override
                public boolean handle(OrderRequest request) {
                    downstreamCalled.set(true);
                    return false;
                }
            };
            OrderProcessingHandler processing = new OrderProcessingHandler();
            processing.setNext(unexpectedDownstream);
            OrderRequest request = adminViewRequest("10.0.0.23", "Terminal rapor");

            // Act
            boolean accepted = processing.handle(request);

            // Assert
            assertAll(
                    () -> assertTrue(accepted),
                    () -> assertFalse(downstreamCalled.get()),
                    () -> assertEquals(OrderRequestOutcome.PROCESSED, request.getOutcome())
            );
        }

        @Test
        @DisplayName("yetki reddi ile cache tekrarı aynı false değerine rağmen ayırt edilir")
        void rejectionAndDuplicateHaveDifferentOutcomes() {
            // Arrange
            OrderRequestHandler chain = createChain();
            OrderRequest unauthorized = new OrderRequest(
                    "can",
                    "1234",
                    "10.0.0.21",
                    OrderOperation.VIEW_ALL_ORDERS,
                    "Özet"
            );
            OrderRequest first = adminViewRequest("10.0.0.22", "Aynı rapor");
            OrderRequest duplicate = adminViewRequest("10.0.0.22", "Aynı rapor");

            // Act
            boolean unauthorizedResult = chain.handle(unauthorized);
            chain.handle(first);
            boolean duplicateResult = chain.handle(duplicate);

            // Assert
            assertAll(
                    () -> assertFalse(unauthorizedResult),
                    () -> assertEquals(OrderRequestOutcome.REJECTED, unauthorized.getOutcome()),
                    () -> assertFalse(duplicateResult),
                    () -> assertEquals(OrderRequestOutcome.DUPLICATE, duplicate.getOutcome())
            );
        }
    }

    private static OrderRequest adminViewRequest(String ipAddress, String payload) {
        return new OrderRequest(
                "admin",
                "root",
                ipAddress,
                OrderOperation.VIEW_ALL_ORDERS,
                payload
        );
    }

    private static OrderRequestHandler createChain() {
        return createChain(new LoginAttemptService(3), new RequestCache());
    }

    private static OrderRequestHandler createChain(LoginAttemptService attempts, RequestCache cache) {
        UserRepository userRepository = new UserRepository(Map.of(
                "can", new User("can", "1234", false),
                "admin", new User("admin", "root", true)
        ));
        return ChainOfResponsibilityDemo.buildChain(userRepository, attempts, cache);
    }
}
