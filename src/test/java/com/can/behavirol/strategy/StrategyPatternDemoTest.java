package com.can.behavirol.strategy;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Strategy — değiştirilebilir hesaplama algoritmaları")
class StrategyPatternDemoTest {

    @Nested
    @DisplayName("Toplama stratejisi seçildiğinde")
    class Addition {

        @Test
        @DisplayName("iki operand toplanır ve strateji adı sunulur")
        void addsOperands() {
            // Arrange
            CalculatorContext context = new CalculatorContext(new AddStrategy());

            // Act
            int result = context.calculate(12, 4);

            // Assert
            assertAll(
                    () -> assertEquals(16, result),
                    () -> assertEquals("Toplama", context.getStrategyName())
            );
        }

        @Test
        @DisplayName("negatif operandlar aynı sözleşmeyle hesaplanır")
        void addsNegativeOperands() {
            // Arrange
            CalculatorContext context = new CalculatorContext(new AddStrategy());

            // Act
            int result = context.calculate(-7, 2);

            // Assert
            assertEquals(-5, result);
        }
    }

    @Nested
    @DisplayName("Çıkarma stratejisi seçildiğinde")
    class Subtraction {

        @Test
        @DisplayName("operand sırası korunarak a eksi b hesaplanır")
        void subtractsSecondOperandFromFirst() {
            // Arrange
            CalculatorContext context = new CalculatorContext(new SubtractStrategy());

            // Act
            int forward = context.calculate(12, 4);
            int reverse = context.calculate(4, 12);

            // Assert
            assertAll(
                    () -> assertEquals(8, forward),
                    () -> assertEquals(-8, reverse),
                    () -> assertEquals("Çıkarma", context.getStrategyName())
            );
        }
    }

    @Nested
    @DisplayName("Çarpma stratejisi seçildiğinde")
    class Multiplication {

        @Test
        @DisplayName("operandlar çarpılır ve sıfır yutan eleman gibi davranır")
        void multipliesOperands() {
            // Arrange
            CalculatorContext context = new CalculatorContext(new MultiplyStrategy());

            // Act
            int product = context.calculate(12, 4);
            int zeroProduct = context.calculate(12, 0);

            // Assert
            assertAll(
                    () -> assertEquals(48, product),
                    () -> assertEquals(0, zeroProduct),
                    () -> assertEquals("Çarpma", context.getStrategyName())
            );
        }
    }

    @Nested
    @DisplayName("Context çalışma anında yeniden yapılandırıldığında")
    class RuntimeSelection {

        @Test
        @DisplayName("aynı calculate çağrısı yeni stratejiye delege edilir")
        void switchesStrategyAtRuntime() {
            // Arrange
            CalculatorContext context = new CalculatorContext(new AddStrategy());
            int addition = context.calculate(12, 4);

            // Act
            context.setStrategy(new SubtractStrategy());
            int subtraction = context.calculate(12, 4);
            context.setStrategy(new MultiplyStrategy());
            int multiplication = context.calculate(12, 4);

            // Assert
            assertAll(
                    () -> assertEquals(16, addition),
                    () -> assertEquals(8, subtraction),
                    () -> assertEquals(48, multiplication)
            );
        }

        @Test
        @DisplayName("context somut sınıfı bilmeden özel stratejiye operandları iletir")
        void delegatesToCustomStrategyThroughTheInterface() {
            // Arrange
            CalculationStrategy maximum = new CalculationStrategy() {
                @Override
                public int execute(int a, int b) {
                    return Math.max(a, b);
                }

                @Override
                public String name() {
                    return "Maksimum";
                }
            };
            CalculatorContext context = new CalculatorContext(maximum);

            // Act
            int result = context.calculate(3, 9);

            // Assert
            assertAll(
                    () -> assertEquals(9, result),
                    () -> assertEquals("Maksimum", context.getStrategyName())
            );
        }
    }

    @Nested
    @DisplayName("Gerçekçi teslimat algoritmaları seçildiğinde")
    class DeliveryPricing {

        @Test
        @DisplayName("aynı gönderi runtime'da standart ve ekspres stratejilerle fiyatlanabilir")
        void deliveryAlgorithmCanBeChangedWithoutChangingTheContext() {
            // Arrange
            Shipment shipment = new Shipment(3, true, false);
            DeliveryPlanner planner = new DeliveryPlanner(new StandardDeliveryStrategy());

            // Act
            DeliveryQuote standard = planner.quote(shipment);
            planner.setStrategy(new ExpressDeliveryStrategy());
            DeliveryQuote express = planner.quote(shipment);

            // Assert
            assertAll(
                    () -> assertEquals(
                            new DeliveryQuote("Standart teslimat", 5_990, 2),
                            standard
                    ),
                    () -> assertEquals(
                            new DeliveryQuote("Ekspres teslimat", 10_990, 1),
                            express
                    )
            );
        }

        @Test
        @DisplayName("premium müşteri avantajı yalnız standart stratejinin iş kuralıdır")
        void premiumPolicyBelongsToTheSelectedStrategy() {
            // Arrange
            Shipment shipment = new Shipment(2, false, true);
            DeliveryPlanner planner = new DeliveryPlanner(new StandardDeliveryStrategy());

            // Act
            DeliveryQuote standard = planner.quote(shipment);
            planner.setStrategy(new ExpressDeliveryStrategy());
            DeliveryQuote express = planner.quote(shipment);

            // Assert
            assertAll(
                    () -> assertEquals(0, standard.feeInCents()),
                    () -> assertEquals(9_990, express.feeInCents()),
                    () -> assertEquals(4, standard.estimatedDays()),
                    () -> assertEquals(2, express.estimatedDays())
            );
        }

        @Test
        @DisplayName("geçersiz gönderi context'e ulaşmadan reddedilir")
        void shipmentRequiresAtLeastOneItem() {
            // Arrange & Act
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Shipment(0, true, false)
            );

            // Assert
            assertEquals("Gönderide en az bir ürün olmalıdır.", error.getMessage());
        }

        @Test
        @DisplayName("Maksimum item sayısı int taşmasına uğramadan long ücret üretir")
        void maximumItemCountDoesNotOverflowTheDeliveryFee() {
            // Arrange
            Shipment shipment = new Shipment(Integer.MAX_VALUE, false, false);
            DeliveryPlanner planner = new DeliveryPlanner(new StandardDeliveryStrategy());

            // Act
            DeliveryQuote standard = planner.quote(shipment);
            planner.setStrategy(new ExpressDeliveryStrategy());
            DeliveryQuote express = planner.quote(shipment);

            // Assert
            assertAll(
                    () -> assertEquals(1_073_741_827_990L, standard.feeInCents()),
                    () -> assertEquals(2_147_483_654_990L, express.feeInCents())
            );
        }

        @Test
        @DisplayName("DeliveryQuote negatif ücreti value object sınırında reddeder")
        void deliveryQuoteRejectsNegativeFees() {
            // Arrange & Act
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> new DeliveryQuote("Geçersiz teklif", -1L, 1)
            );

            // Assert
            assertEquals("feeInCents cannot be negative", error.getMessage());
        }
    }
}
