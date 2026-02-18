package com.can.creational.singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SingletonDemoTest {

    @AfterEach
    void tearDown() {
        AppConfig.resetForTests();
    }

    @Nested
    class InstanceIdentity {

        // Bu test, ardışık çağrılarda her zaman aynı instance'ın döndüğünü doğrular.
        @Test
        void shouldReturnSameReferenceOnEveryCall() {
            AppConfig first = AppConfig.getInstance();
            AppConfig second = AppConfig.getInstance();

            assertSame(first, second);
        }
    }

    @Nested
    class ConfigurationDefaults {

        // Bu test, singleton'ın varsayılan konfigürasyon değerlerini koruduğunu doğrular.
        @Test
        void shouldExposeExpectedDefaultValues() {
            AppConfig config = AppConfig.getInstance();

            assertEquals("https://api.example.com", config.getApiBaseUrl());
            assertEquals(3_000, config.getConnectionTimeoutMs());
            assertTrue(config.describe().contains("connectionTimeoutMs=3000"));
        }
    }

    @Nested
    class ThreadSafety {

        // Bu test, eşzamanlı erişim altında tek bir instance üretildiğini doğrular.
        @Test
        void shouldCreateSingleInstanceUnderConcurrentAccess() throws Exception {
            int threadCount = 16;
            var pool = Executors.newFixedThreadPool(threadCount);
            CountDownLatch ready = new CountDownLatch(threadCount);
            CountDownLatch start = new CountDownLatch(1);

            List<Future<AppConfig>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(pool.submit(() -> {
                    ready.countDown();
                    start.await();
                    return AppConfig.getInstance();
                }));
            }

            ready.await();
            start.countDown();

            AppConfig expected = AppConfig.getInstance();
            for (Future<AppConfig> future : futures) {
                assertSame(expected, future.get());
            }

            pool.shutdownNow();
        }
    }
}
