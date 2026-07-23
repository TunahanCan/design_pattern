package com.can.structural.flyweight;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Flyweight | Çok sayıda ağacın ortak tür verisini paylaşma")
class FlyweightPatternDemoTest {

    @Nested
    @DisplayName("Flyweight havuzunda kimlik paylaşımı")
    class FactoryIdentityReuse {

        @Test
        @DisplayName("Aynı intrinsic state aynı nesneyi, farklı state farklı nesneyi döndürmelidir")
        void shouldReuseTreeTypeInstances() {
            // Arrange
            TreeFactory factory = new TreeFactory();

            // Act
            TreeType pineA = factory.getTreeType("Çam", "Yeşil", "needle-texture");
            TreeType pineB = factory.getTreeType("Çam", "Yeşil", "needle-texture");
            TreeType oak = factory.getTreeType("Meşe", "Koyu Yeşil", "oak-texture");

            // Assert
            assertAll(
                () -> assertSame(pineA, pineB),
                () -> assertNotSame(pineA, oak),
                () -> assertEquals(2, factory.getTreeTypeCount()),
                () -> assertEquals("Çam|Yeşil|needle-texture", pineA.signature())
            );
        }

        @Test
        @DisplayName("Yapısal key, ayraç içeren alanlarda metin birleştirme çakışması üretmemelidir")
        void shouldAvoidDelimitedStringKeyCollisions() {
            // Arrange
            TreeFactory factory = new TreeFactory();

            // Act
            TreeType first = factory.getTreeType("A|B", "C", "D");
            TreeType second = factory.getTreeType("A", "B|C", "D");

            // Assert
            assertAll(
                () -> assertNotSame(first, second),
                () -> assertEquals(2, factory.getTreeTypeCount())
            );
        }

        @Test
        @DisplayName("Paralel aynı-key isteklerinin tamamı tek TreeType identity'sini görmelidir")
        void shouldReuseOneIdentityForConcurrentRequests() throws Exception {
            // Arrange
            int workerCount = 16;
            TreeFactory factory = new TreeFactory();
            ExecutorService executor = Executors.newFixedThreadPool(workerCount);
            CountDownLatch workersReady = new CountDownLatch(workerCount);
            CountDownLatch startTogether = new CountDownLatch(1);
            List<Future<TreeType>> futures = new ArrayList<>();

            try {
                for (int i = 0; i < workerCount; i++) {
                    futures.add(executor.submit(() -> {
                        workersReady.countDown();
                        startTogether.await();
                        return factory.getTreeType(
                            "Çam",
                            "Yeşil",
                            "needle-texture"
                        );
                    }));
                }

                assertTrue(
                    workersReady.await(5, TimeUnit.SECONDS),
                    "Bütün worker'lar başlangıç bariyerine ulaşmalı"
                );

                // Act
                startTogether.countDown();
                TreeType sharedType = futures.getFirst().get(5, TimeUnit.SECONDS);

                // Assert
                for (Future<TreeType> future : futures) {
                    assertSame(sharedType, future.get(5, TimeUnit.SECONDS));
                }
                assertEquals(1, factory.getTreeTypeCount());
            } finally {
                startTogether.countDown();
                executor.shutdownNow();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }
        }
    }

    @Nested
    @DisplayName("Context ile intrinsic state ayrımı")
    class ContextSeparation {

        @Test
        @DisplayName("İki Tree aynı TreeType'ı paylaşırken farklı koordinatlarda çizilebilmelidir")
        void shouldKeepCoordinatesOutsideSharedTreeType() {
            // Arrange
            TreeType pine = new TreeType("Çam", "Yeşil", "needle-texture");
            Tree firstTree = new Tree(1, 2, pine);
            Tree secondTree = new Tree(10, 20, pine);

            // Act
            String firstDrawing = firstTree.draw();
            String secondDrawing = secondTree.draw();

            // Assert
            assertAll(
                () -> assertSame(firstTree.getType(), secondTree.getType()),
                () -> assertEquals(
                    "Çam (renk=Yeşil, texture=needle-texture) -> x=1, y=2",
                    firstDrawing
                ),
                () -> assertEquals(
                    "Çam (renk=Yeşil, texture=needle-texture) -> x=10, y=20",
                    secondDrawing
                )
            );
        }
    }

    @Nested
    @DisplayName("Forest içindeki context ve flyweight sayıları")
    class ForestAccounting {

        @Test
        @DisplayName("Çok sayıda Tree context'i az sayıda TreeType ile temsil edilmelidir")
        void shouldKeepManyContextsWithFewFlyweights() {
            // Arrange
            TreeFactory factory = new TreeFactory();
            Forest forest = new Forest(factory);

            // Act
            for (int i = 0; i < 100; i++) {
                forest.plantTree(i, i, "Çam", "Yeşil", "needle-texture");
                forest.plantTree(i, i + 1, "Meşe", "Koyu Yeşil", "oak-texture");
            }

            // Assert
            assertAll(
                () -> assertEquals(200, forest.getTreeCount()),
                () -> assertEquals(2, forest.getUsedTreeTypeCount()),
                () -> assertEquals(2, forest.getUniqueTreeTypeCount()),
                () -> assertEquals(200, forest.drawAll().size())
            );
        }

        @Test
        @DisplayName("drawAll ekim sırasını koruyan değiştirilemez bir sonuç listesi döndürmelidir")
        void shouldReturnDrawingsInPlantingOrderAsUnmodifiableList() {
            // Arrange
            Forest forest = new Forest(new TreeFactory());
            forest.plantTree(1, 2, "Çam", "Yeşil", "needle-texture");
            forest.plantTree(3, 4, "Meşe", "Koyu Yeşil", "oak-texture");

            // Act
            List<String> drawings = forest.drawAll();

            // Assert
            assertAll(
                () -> assertEquals(
                    "Çam (renk=Yeşil, texture=needle-texture) -> x=1, y=2",
                    drawings.getFirst()
                ),
                () -> assertEquals(
                    "Meşe (renk=Koyu Yeşil, texture=oak-texture) -> x=3, y=4",
                    drawings.getLast()
                ),
                () -> assertThrows(UnsupportedOperationException.class, () -> drawings.add("new"))
            );
        }

        @Test
        @DisplayName("Forest kendi kullandığı tür sayısını paylaşılan factory'nin toplamından ayırmalıdır")
        void shouldReportForestLocalAndFactoryWideCountsSeparately() {
            // Arrange
            TreeFactory sharedFactory = new TreeFactory();
            Forest pineForest = new Forest(sharedFactory);
            Forest oakForest = new Forest(sharedFactory);

            // Act
            pineForest.plantTree(1, 1, "Çam", "Yeşil", "needle");
            oakForest.plantTree(2, 2, "Meşe", "Koyu Yeşil", "oak");

            // Assert
            assertAll(
                () -> assertEquals(1, pineForest.getUsedTreeTypeCount()),
                () -> assertEquals(1, oakForest.getUsedTreeTypeCount()),
                () -> assertEquals(2, pineForest.getUniqueTreeTypeCount()),
                () -> assertEquals(2, pineForest.getFactoryTreeTypeCount())
            );
        }
    }

    @Nested
    @DisplayName("Flyweight oluşturma sınırları")
    class FlyweightBoundaries {

        @Test
        @DisplayName("Eksik intrinsic state veya factory bağımlılığı erken reddedilmelidir")
        void shouldRejectIncompleteSharedState() {
            assertAll(
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> new TreeFactory().getTreeType(" ", "Yeşil", "texture")
                ),
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> new TreeType("Çam", null, "texture")
                ),
                () -> assertThrows(NullPointerException.class, () -> new Tree(1, 2, null)),
                () -> assertThrows(NullPointerException.class, () -> new Forest(null))
            );
        }
    }
}
