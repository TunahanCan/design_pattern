package com.can.creational.singleton;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("Singleton — normal erişim yolları aynı AppConfig referansına çıkar")
class SingletonDemoTest {

    @AfterEach
    void resetSingletonState() {
        AppConfig.resetForTests();
    }

    @Nested
    @DisplayName("Identity ve yaşam döngüsü")
    class IdentityContract {

        @Test
        @DisplayName("Ardışık getInstance çağrıları aynı referansı döndürür")
        void repeatedCallsReturnTheSameReference() {
            // Arrange & Act
            AppConfig first = AppConfig.getInstance();
            AppConfig second = AppConfig.getInstance();

            // Assert
            assertSame(first, second);
        }

        @Test
        @DisplayName("Test hook state'i sıfırladığında sonraki çağrı yeni referans üretir")
        void resetHookStartsANewTestLifecycle() {
            // Arrange
            AppConfig beforeReset = AppConfig.getInstance();

            // Act
            AppConfig.resetForTests();
            AppConfig afterReset = AppConfig.getInstance();

            // Assert
            assertNotSame(beforeReset, afterReset);
        }
    }

    @Nested
    @DisplayName("Konfigürasyon snapshot'ı")
    class ConfigurationDefaults {

        @Test
        @DisplayName("Singleton beklenen immutable default değerleri sunar")
        void singletonExposesTheExpectedDefaults() {
            // Arrange
            AppConfig config = AppConfig.getInstance();

            // Act
            String description = config.describe();

            // Assert
            assertAll(
                    () -> assertEquals("https://api.example.com", config.getApiBaseUrl()),
                    () -> assertEquals(3_000, config.getConnectionTimeoutMs()),
                    () -> assertEquals(
                            "AppConfig{apiBaseUrl='https://api.example.com', connectionTimeoutMs=3000}",
                            description
                    )
            );
        }

        @Test
        @DisplayName("Aynı config değerleri farklı identity'lerde value-equal kalır")
        void valueEqualityRemainsIndependentFromSingletonIdentity() {
            // Arrange
            AppConfig firstIdentity = AppConfig.getInstance();
            AppConfig.resetForTests();
            AppConfig secondIdentity = AppConfig.getInstance();

            // Act & Assert
            assertAll(
                    () -> assertNotSame(firstIdentity, secondIdentity),
                    () -> assertEquals(firstIdentity, secondIdentity),
                    () -> assertEquals(firstIdentity.hashCode(), secondIdentity.hashCode())
            );
        }
    }

    @Nested
    @DisplayName("Gerçekçi kullanım: global erişim composition root'ta kalır")
    class DependencyBoundary {

        @Test
        @DisplayName("ApiClient singleton config ile kararlı bir istek planı üretir")
        void apiClientUsesTheSingletonAtTheWiringBoundary() {
            // Arrange
            ApiClient client = new ApiClient(AppConfig.getInstance());

            // Act
            ApiRequestPlan plan = client.planGet("/health");

            // Assert
            assertAll(
                    () -> assertEquals("GET", plan.method()),
                    () -> assertEquals("https://api.example.com/health", plan.url()),
                    () -> assertEquals(3_000, plan.timeoutMs()),
                    () -> assertEquals(
                            "GET https://api.example.com/health (timeout=3000ms)",
                            plan.describe()
                    )
            );
        }

        @Test
        @DisplayName("ApiClient testte global state olmadan fake config ile kurulabilir")
        void apiClientCanBeTestedWithAnInjectedConfiguration() {
            // Arrange
            ApiClientConfig testConfig = new TestApiClientConfig("http://localhost:8080/", 250);
            ApiClient client = new ApiClient(testConfig);

            // Act
            ApiRequestPlan plan = client.planGet("orders/42");

            // Assert
            assertAll(
                    () -> assertEquals("http://localhost:8080/orders/42", plan.url()),
                    () -> assertEquals(250, plan.timeoutMs())
            );
        }

        @Test
        @DisplayName("Client host'u değiştirecek absolute URI ve geçersiz timeout'u reddeder")
        void apiClientRejectsUnsafeOrInvalidConfiguration() {
            // Arrange
            ApiClient validClient = new ApiClient(
                    new TestApiClientConfig("https://api.example.com", 500)
            );

            // Act
            IllegalArgumentException absolutePathError = assertThrows(
                    IllegalArgumentException.class,
                    () -> validClient.planGet("https://attacker.example/collect")
            );
            IllegalArgumentException timeoutError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new ApiClient(new TestApiClientConfig("https://api.example.com", 0))
            );

            // Assert
            assertAll(
                    () -> assertEquals(
                            "path must be relative to apiBaseUrl",
                            absolutePathError.getMessage()
                    ),
                    () -> assertEquals(
                            "connectionTimeoutMs must be positive",
                            timeoutError.getMessage()
                    )
            );
        }

        @Test
        @DisplayName("Base URL yalnız temiz bir HTTP(S) origin/path olabilir")
        void apiClientRejectsUnsupportedOrAmbiguousBaseUrls() {
            // Arrange & Act
            IllegalArgumentException schemeError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new ApiClient(new TestApiClientConfig("ftp://files.example.com", 500))
            );
            IllegalArgumentException credentialsError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new ApiClient(
                            new TestApiClientConfig("https://user:secret@api.example.com", 500)
                    )
            );
            IllegalArgumentException queryError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new ApiClient(
                            new TestApiClientConfig("https://api.example.com?tenant=42", 500)
                    )
            );
            IllegalArgumentException portError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new ApiClient(
                            new TestApiClientConfig("https://api.example.com:99999", 500)
                    )
            );

            // Assert
            String expected = "apiBaseUrl must be an absolute HTTP(S) URL"
                    + " without credentials, query or fragment and with a valid port";
            assertAll(
                    () -> assertEquals(expected, schemeError.getMessage()),
                    () -> assertEquals(expected, credentialsError.getMessage()),
                    () -> assertEquals(expected, queryError.getMessage()),
                    () -> assertEquals(expected, portError.getMessage())
            );
        }

        @Test
        @DisplayName("Relative path configured base-path altında URI olarak çözülür")
        void apiClientKeepsResolvedPathsInsideTheConfiguredBasePath() {
            // Arrange
            ApiClient client = new ApiClient(
                    new TestApiClientConfig("https://api.example.com/v1/private", 500)
            );

            // Act
            ApiRequestPlan withoutLeadingSlash = client.planGet("orders/42");
            ApiRequestPlan withLeadingSlash = client.planGet("/health");

            // Assert
            assertAll(
                    () -> assertEquals(
                            "https://api.example.com/v1/private/orders/42",
                            withoutLeadingSlash.url()
                    ),
                    () -> assertEquals(
                            "https://api.example.com/v1/private/health",
                            withLeadingSlash.url()
                    )
            );
        }

        @Test
        @DisplayName("Dot-segment path configured base-path dışına çıkamaz")
        void apiClientRejectsRawAndEncodedPathTraversal() {
            // Arrange
            ApiClient client = new ApiClient(
                    new TestApiClientConfig("https://api.example.com/v1/private", 500)
            );

            // Act
            IllegalArgumentException rawTraversal = assertThrows(
                    IllegalArgumentException.class,
                    () -> client.planGet("../../admin")
            );
            IllegalArgumentException encodedTraversal = assertThrows(
                    IllegalArgumentException.class,
                    () -> client.planGet("%2e%2e/%2e%2e/admin")
            );

            // Assert
            assertAll(
                    () -> assertEquals("path must stay within apiBaseUrl", rawTraversal.getMessage()),
                    () -> assertEquals("path must stay within apiBaseUrl", encodedTraversal.getMessage())
            );
        }

        @Test
        @DisplayName("Network-path reference, query ve fragment endpoint path'ine karışamaz")
        void apiClientRejectsAuthorityQueryAndFragmentInjection() {
            // Arrange
            ApiClient client = new ApiClient(
                    new TestApiClientConfig("https://api.example.com/v1", 500)
            );

            // Act
            IllegalArgumentException networkPath = assertThrows(
                    IllegalArgumentException.class,
                    () -> client.planGet("//attacker.example/collect")
            );
            IllegalArgumentException query = assertThrows(
                    IllegalArgumentException.class,
                    () -> client.planGet("orders?admin=true")
            );
            IllegalArgumentException fragment = assertThrows(
                    IllegalArgumentException.class,
                    () -> client.planGet("orders#internal")
            );

            // Assert
            assertAll(
                    () -> assertEquals("path must be relative to apiBaseUrl", networkPath.getMessage()),
                    () -> assertEquals("path must not contain query or fragment", query.getMessage()),
                    () -> assertEquals("path must not contain query or fragment", fragment.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Sınıf yapısı üretim yollarını sınırlar")
    class StructuralConstraints {

        @Test
        @DisplayName("AppConfig final ve tek constructor'ı private'dır")
        void singletonTypeCannotBeNormallyConstructedOrSubclassed() {
            // Arrange
            Constructor<?>[] constructors = AppConfig.class.getDeclaredConstructors();

            // Act
            boolean finalType = Modifier.isFinal(AppConfig.class.getModifiers());
            boolean privateConstructor = constructors.length == 1
                    && Modifier.isPrivate(constructors[0].getModifiers());

            // Assert
            assertAll(
                    () -> assertTrue(finalType, "Subclass yeni üretim yolu açmamalı"),
                    () -> assertTrue(privateConstructor, "Normal client new AppConfig() yazamamalı")
            );
        }
    }

    @Nested
    @DisplayName("Double-checked locking eşzamanlı ilk erişimi korur")
    class ThreadSafety {

        @Test
        @Timeout(value = 5, unit = TimeUnit.SECONDS)
        @DisplayName("Aynı anda başlayan worker'lar tek object identity gözlemler")
        void concurrentFirstAccessPublishesOneIdentity() throws Exception {
            // Arrange
            int threadCount = 16;
            ExecutorService pool = Executors.newFixedThreadPool(threadCount);
            CountDownLatch ready = new CountDownLatch(threadCount);
            CountDownLatch start = new CountDownLatch(1);
            List<Future<AppConfig>> futures = new ArrayList<>();

            try {
                for (int index = 0; index < threadCount; index++) {
                    futures.add(pool.submit(() -> {
                        ready.countDown();
                        start.await();
                        return AppConfig.getInstance();
                    }));
                }

                assertTrue(ready.await(4, TimeUnit.SECONDS), "Bütün worker'lar başlangıç çizgisine gelmeli");

                // Act
                start.countDown();
                Set<AppConfig> identities = Collections.newSetFromMap(new IdentityHashMap<>());
                for (Future<AppConfig> future : futures) {
                    identities.add(future.get());
                }

                // Assert
                assertEquals(1, identities.size(), "Bütün thread'ler aynı object identity'yi görmeli");
            } finally {
                start.countDown();
                pool.shutdownNow();
                pool.awaitTermination(500, TimeUnit.MILLISECONDS);
            }
        }
    }

    private record TestApiClientConfig(
            String apiBaseUrl,
            int connectionTimeoutMs
    ) implements ApiClientConfig {

        @Override
        public String getApiBaseUrl() {
            return apiBaseUrl;
        }

        @Override
        public int getConnectionTimeoutMs() {
            return connectionTimeoutMs;
        }
    }
}
