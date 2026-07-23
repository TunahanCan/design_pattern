package com.can.structural.proxy;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Proxy | Uzak servis çağrılarını şeffaf bir cache katmanıyla yönetme")
class ProxyPatternDemoTest {

    @Nested
    @DisplayName("Subject implementasyonlarının ortak fonksiyonel kontratı")
    class SubjectContract {

        @ParameterizedTest(name = "{0}")
        @MethodSource("subjects")
        @DisplayName("Caching Proxy video kimliğini değiştirmeden Real Subject'e iletmelidir")
        void shouldPreserveTheExactVideoId(
            String implementationName,
            ThirdPartyYouTubeLib subject
        ) {
            // Arrange
            String paddedId = "  proxy-pattern  ";

            // Act & Assert
            assertAll(
                () -> assertEquals(
                    "Video[  proxy-pattern  ] - Proxy pattern anlatımı",
                    subject.getVideoInfo(paddedId)
                ),
                () -> assertEquals(
                    "Downloaded:   proxy-pattern  .mp4",
                    subject.downloadVideo(paddedId)
                ),
                () -> assertEquals(
                    "Video[ ] - Proxy pattern anlatımı",
                    subject.getVideoInfo(" ")
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> subject.getVideoInfo(null)
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> subject.downloadVideo(null)
                )
            );
        }

        static Stream<Arguments> subjects() {
            return Stream.of(
                Arguments.of(
                    "Real Subject",
                    (ThirdPartyYouTubeLib) new ThirdPartyYouTubeClass()
                ),
                Arguments.of(
                    "Caching Proxy",
                    (ThirdPartyYouTubeLib) new CachedYouTubeClass(
                        new ThirdPartyYouTubeClass()
                    )
                )
            );
        }
    }

    @Nested
    @DisplayName("Video listesi cache'i")
    class ListCaching {

        @Test
        @DisplayName("Manager aynı listeyi iki kez isterken gerçek servis yalnız bir kez çağrılmalıdır")
        void shouldCacheListCalls() {
            // Arrange
            ThirdPartyYouTubeClass realService = new ThirdPartyYouTubeClass();
            CachedYouTubeClass proxy = new CachedYouTubeClass(realService);
            YouTubeManager manager = new YouTubeManager(proxy);

            // Act
            String firstPanel = manager.renderListPanel();
            String secondPanel = manager.renderListPanel();

            // Assert
            String expected =
                "ListPanel => design-patterns-intro, proxy-pattern, solid-principles";
            assertAll(
                () -> assertEquals(expected, firstPanel),
                () -> assertEquals(expected, secondPanel),
                () -> assertEquals(1, realService.getListRequestCount())
            );
        }

        @Test
        @DisplayName("Cache listesi kaynaktan kopuk, değiştirilemez bir snapshot olmalıdır")
        void shouldCacheAnImmutableDefensiveSnapshot() {
            // Arrange
            List<String> mutableSource = new ArrayList<>(List.of("first"));
            ThirdPartyYouTubeLib service = new ThirdPartyYouTubeLib() {
                @Override
                public List<String> listVideos() {
                    return mutableSource;
                }

                @Override
                public String getVideoInfo(String id) {
                    return id;
                }

                @Override
                public String downloadVideo(String id) {
                    return id;
                }
            };
            CachedYouTubeClass proxy = new CachedYouTubeClass(service);

            // Act
            List<String> cached = proxy.listVideos();
            mutableSource.add("later");

            // Assert
            assertAll(
                () -> assertEquals(List.of("first"), cached),
                () -> assertThrows(UnsupportedOperationException.class, () -> cached.add("x"))
            );
        }
    }

    @Nested
    @DisplayName("Video bilgisi için anahtar bazlı cache")
    class VideoInfoCaching {

        @Test
        @DisplayName("Aynı id tekrarında cache kullanılmalı, farklı id bağımsız çağrılmalıdır")
        void shouldCacheVideoInfoPerId() {
            // Arrange
            ThirdPartyYouTubeClass realService = new ThirdPartyYouTubeClass();
            YouTubeManager manager = new YouTubeManager(new CachedYouTubeClass(realService));

            // Act
            String firstProxyPage = manager.renderVideoPage("proxy-pattern");
            String secondProxyPage = manager.renderVideoPage("proxy-pattern");
            String solidPage = manager.renderVideoPage("solid-principles");

            // Assert
            assertAll(
                () -> assertEquals(
                    "VideoPage => Video[proxy-pattern] - Proxy pattern anlatımı",
                    firstProxyPage
                ),
                () -> assertEquals(firstProxyPage, secondProxyPage),
                () -> assertEquals(
                    "VideoPage => Video[solid-principles] - Proxy pattern anlatımı",
                    solidPage
                ),
                () -> assertEquals(1, realService.getInfoRequestCount("proxy-pattern")),
                () -> assertEquals(1, realService.getInfoRequestCount("solid-principles"))
            );
        }
    }

    @Nested
    @DisplayName("Download cache'i ve invalidation")
    class DownloadCachingAndReset {

        @Test
        @DisplayName("Download sonucu tekrar kullanılmalı, reset sonrasında servis yeniden çağrılmalıdır")
        void shouldCacheDownloadsAndResetWhenAsked() {
            // Arrange
            ThirdPartyYouTubeClass realService = new ThirdPartyYouTubeClass();
            CachedYouTubeClass proxy = new CachedYouTubeClass(realService);

            // Act
            String first = proxy.downloadVideo("proxy-pattern");
            String second = proxy.downloadVideo("proxy-pattern");
            int requestCountBeforeReset = realService.getDownloadRequestCount("proxy-pattern");
            proxy.reset();
            String afterReset = proxy.downloadVideo("proxy-pattern");

            // Assert
            assertAll(
                () -> assertEquals("Downloaded: proxy-pattern.mp4", first),
                () -> assertEquals(first, second),
                () -> assertEquals(1, requestCountBeforeReset),
                () -> assertEquals(first, afterReset),
                () -> assertEquals(2, realService.getDownloadRequestCount("proxy-pattern"))
            );
        }

        @Test
        @DisplayName("Reset liste, bilgi ve download cache'lerinin tamamını temizlemelidir")
        void shouldResetEveryCacheRegion() {
            // Arrange
            ThirdPartyYouTubeClass realService = new ThirdPartyYouTubeClass();
            CachedYouTubeClass proxy = new CachedYouTubeClass(realService);
            proxy.listVideos();
            proxy.getVideoInfo("proxy-pattern");
            proxy.downloadVideo("proxy-pattern");

            // Act
            proxy.reset();
            proxy.listVideos();
            proxy.getVideoInfo("proxy-pattern");
            proxy.downloadVideo("proxy-pattern");

            // Assert
            assertAll(
                () -> assertEquals(2, realService.getListRequestCount()),
                () -> assertEquals(2, realService.getInfoRequestCount("proxy-pattern")),
                () -> assertEquals(2, realService.getDownloadRequestCount("proxy-pattern"))
            );
        }

        @Test
        @DisplayName("Tek video invalidation'ı yalnız o id'nin bilgi ve download girdilerini silmelidir")
        void shouldInvalidateOneVideoWithoutFlushingOtherEntries() {
            // Arrange
            ThirdPartyYouTubeClass realService = new ThirdPartyYouTubeClass();
            CachedYouTubeClass proxy = new CachedYouTubeClass(realService);
            proxy.getVideoInfo("proxy-pattern");
            proxy.downloadVideo("proxy-pattern");
            proxy.getVideoInfo("solid-principles");

            // Act
            proxy.invalidateVideo("proxy-pattern");
            proxy.getVideoInfo("proxy-pattern");
            proxy.downloadVideo("proxy-pattern");
            proxy.getVideoInfo("solid-principles");

            // Assert
            assertAll(
                () -> assertEquals(2, realService.getInfoRequestCount("proxy-pattern")),
                () -> assertEquals(2, realService.getDownloadRequestCount("proxy-pattern")),
                () -> assertEquals(1, realService.getInfoRequestCount("solid-principles"))
            );
        }
    }

    @Nested
    @DisplayName("Paralel cache miss koordinasyonu")
    class ConcurrentCacheCoordination {

        @Test
        @DisplayName("Paralel liste miss'leri tek immutable snapshot ve tek backend çağrısı üretmelidir")
        void shouldLoadTheVideoListOnceForParallelMisses() throws Exception {
            // Arrange
            ThirdPartyYouTubeClass realService = new ThirdPartyYouTubeClass();
            CachedYouTubeClass proxy = new CachedYouTubeClass(realService);

            // Act
            List<List<String>> results = runConcurrently(16, proxy::listVideos);
            List<String> sharedSnapshot = results.getFirst();

            // Assert
            assertAll(
                () -> assertEquals(1, realService.getListRequestCount()),
                () -> assertEquals(
                    List.of(
                        "design-patterns-intro",
                        "proxy-pattern",
                        "solid-principles"
                    ),
                    sharedSnapshot
                ),
                () -> assertTrue(
                    results.stream().allMatch(result -> result == sharedSnapshot)
                )
            );
        }

        @Test
        @DisplayName("Paralel aynı-id miss'leri tek cached value ve tek backend çağrısı üretmelidir")
        void shouldLoadVideoInfoOnceForParallelSameIdMisses() throws Exception {
            // Arrange
            ThirdPartyYouTubeClass realService = new ThirdPartyYouTubeClass();
            CachedYouTubeClass proxy = new CachedYouTubeClass(realService);

            // Act
            List<String> results = runConcurrently(
                16,
                () -> proxy.getVideoInfo("shared-id")
            );
            String sharedInfo = results.getFirst();

            // Assert
            assertAll(
                () -> assertEquals(1, realService.getInfoRequestCount("shared-id")),
                () -> assertEquals(
                    "Video[shared-id] - Proxy pattern anlatımı",
                    sharedInfo
                ),
                () -> results.forEach(result -> assertSame(sharedInfo, result))
            );
        }
    }

    @Nested
    @DisplayName("Proxy sınır kontratları")
    class ProxyBoundaries {

        @Test
        @DisplayName("Eksik collaborator ve null video kimliği fail-fast reddedilmelidir")
        void shouldRejectMissingCollaboratorsAndNullIds() {
            // Arrange
            CachedYouTubeClass proxy = new CachedYouTubeClass(new ThirdPartyYouTubeClass());

            // Act & Assert
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> new CachedYouTubeClass(null)),
                () -> assertThrows(NullPointerException.class, () -> new YouTubeManager(null)),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> proxy.getVideoInfo(null)
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> proxy.invalidateVideo(null)
                )
            );
        }
    }

    private static <T> List<T> runConcurrently(
        int workerCount,
        Callable<T> operation
    ) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        CountDownLatch workersReady = new CountDownLatch(workerCount);
        CountDownLatch startTogether = new CountDownLatch(1);
        List<Future<T>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < workerCount; i++) {
                futures.add(executor.submit(() -> {
                    workersReady.countDown();
                    startTogether.await();
                    return operation.call();
                }));
            }

            assertTrue(
                workersReady.await(5, TimeUnit.SECONDS),
                "Bütün worker'lar başlangıç bariyerine ulaşmalı"
            );
            startTogether.countDown();

            List<T> results = new ArrayList<>(workerCount);
            for (Future<T> future : futures) {
                results.add(future.get(5, TimeUnit.SECONDS));
            }
            return List.copyOf(results);
        } finally {
            startTogether.countDown();
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
