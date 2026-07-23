package com.can.structural.composite;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Composite | Ürün ve kutuları tek bir ağaç kontratıyla fiyatlama")
class CompositePatternDemoTest {

    @Nested
    @DisplayName("Yaprak ve boş bileşik davranışı")
    class LeafAndEmptyComposite {

        @Test
        @DisplayName("Product kendi adı ve fiyatını doğrudan sunmalıdır")
        void shouldExposeLeafValuesThroughComponentContract() {
            // Arrange
            OrderComponent product = new Product("Keyboard", 1200);

            // Act
            String name = product.getName();
            double price = product.getPrice();

            // Assert
            assertAll(
                () -> assertEquals("Keyboard", name),
                () -> assertEquals(1200, price)
            );
        }

        @Test
        @DisplayName("Boş Box yalnızca kendi paketleme maliyetini döndürmelidir")
        void shouldReturnPackagingCostForEmptyBox() {
            // Arrange
            OrderComponent emptyBox = new Box("Empty Box", 25);

            // Act
            double total = emptyBox.getPrice();

            // Assert
            assertAll(
                () -> assertEquals("Empty Box", emptyBox.getName()),
                () -> assertEquals(25, total)
            );
        }
    }

    @Nested
    @DisplayName("Özyinelemeli fiyat toplama")
    class RecursivePriceAggregation {

        @Test
        @DisplayName("İç içe kutulardaki ürünler ve her paketleme maliyeti bir kez toplanmalıdır")
        void shouldCalculateNestedBoxTotalPrice() {
            // Arrange
            Product keyboard = new Product("Keyboard", 1200);
            Product mouse = new Product("Mouse", 800);
            Product cable = new Product("Cable", 150);

            Box accessoryBox = new Box("Accessory Box", 40);
            accessoryBox.add(mouse);
            accessoryBox.add(cable);

            Box mainOrderBox = new Box("Main Box", 75);
            mainOrderBox.add(keyboard);
            mainOrderBox.add(accessoryBox);

            // Act
            double total = mainOrderBox.getPrice();

            // Assert
            assertEquals(2265, total);
        }
    }

    @Nested
    @DisplayName("Çocuk bileşen yönetimi")
    class ChildManagement {

        @Test
        @DisplayName("Bir çocuk çıkarıldığında toplam fiyat yeniden hesaplanmalıdır")
        void shouldUpdateTotalWhenRemovingChild() {
            // Arrange
            Box box = new Box("Box", 10);
            Product firstProduct = new Product("P1", 100);
            Product secondProduct = new Product("P2", 200);
            box.add(firstProduct);
            box.add(secondProduct);

            // Act
            double beforeRemoval = box.getPrice();
            box.remove(firstProduct);
            double afterRemoval = box.getPrice();

            // Assert
            assertAll(
                () -> assertEquals(310, beforeRemoval),
                () -> assertEquals(210, afterRemoval),
                () -> assertEquals(List.of(secondProduct), box.getChildren())
            );
        }

        @Test
        @DisplayName("Yeni snapshot API'si sonraki Box değişikliklerinden etkilenmemelidir")
        void shouldExposeChildrenAsUnmodifiableSnapshot() {
            // Arrange
            Box box = new Box("Box", 10);
            box.add(new Product("P1", 100));
            List<OrderComponent> children = box.getChildrenSnapshot();

            // Act
            box.add(new Product("P2", 200));
            Runnable externalMutation = () -> children.add(new Product("P2", 200));

            // Assert
            assertAll(
                () -> assertEquals(1, children.size()),
                () -> assertEquals(2, box.getChildren().size()),
                () -> assertThrows(UnsupportedOperationException.class, externalMutation::run)
            );
        }

        @Test
        @DisplayName("Geriye uyumlu getChildren görünümü canlı fakat dışarıdan değiştirilemez kalmalıdır")
        void shouldPreserveTheUnmodifiableLiveChildrenView() {
            // Arrange
            Box box = new Box("Box", 10);
            box.add(new Product("P1", 100));
            List<OrderComponent> liveChildren = box.getChildren();

            // Act
            box.add(new Product("P2", 200));

            // Assert
            assertAll(
                () -> assertEquals(2, liveChildren.size()),
                () -> assertThrows(
                    UnsupportedOperationException.class,
                    () -> liveChildren.clear()
                )
            );
        }
    }

    @Nested
    @DisplayName("Gerçek sipariş invariant'ları")
    class ProductionInvariants {

        @Test
        @DisplayName("Parasal toplam BigDecimal ile ondalık kayıp olmadan hesaplanmalıdır")
        void shouldAggregateMoneyExactly() {
            // Arrange
            Box order = new Box("Order", new BigDecimal("0.10"));
            order.add(new Product("Small item", new BigDecimal("0.20")));

            // Act
            BigDecimal total = order.getPriceAmount();

            // Assert
            assertEquals(new BigDecimal("0.30"), total);
        }

        @Test
        @DisplayName("Kutu kendisini veya dolaylı atasını çocuk olarak alamamalıdır")
        void shouldPreventDirectAndIndirectCycles() {
            // Arrange
            Box parent = new Box("Parent", 10);
            Box child = new Box("Child", 5);
            parent.add(child);

            // Act & Assert
            assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> parent.add(parent)),
                () -> assertThrows(IllegalArgumentException.class, () -> child.add(parent))
            );
        }

        @Test
        @DisplayName("Negatif maliyet, boş ad ve null çocuk erken reddedilmelidir")
        void shouldRejectInvalidDomainState() {
            // Arrange
            Box validBox = new Box("Valid", 0);

            // Act & Assert
            assertAll(
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> new Product("Product", new BigDecimal("-0.01"))
                ),
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> new Box(" ", BigDecimal.ZERO)
                ),
                () -> assertThrows(NullPointerException.class, () -> validBox.add(null))
            );
        }
    }
}
