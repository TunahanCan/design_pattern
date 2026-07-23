package com.can.structural.adapter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Adapter | Kare çiviyi yuvarlak deliğin diline çevirme")
class AdapterPatternDemoTest {

    @Nested
    @DisplayName("Doğrudan uyumlu yuvarlak çiviler")
    class RoundPegCompatibility {

        @Test
        @DisplayName("Çivi deliğe eşit veya daha küçükse sığmalıdır")
        void shouldFitWhenRoundPegIsNotLargerThanHole() {
            // Arrange
            RoundHole hole = new RoundHole(5);
            RoundPeg exactPeg = new RoundPeg(5);
            RoundPeg smallerPeg = new RoundPeg(3);
            RoundPeg largerPeg = new RoundPeg(6);

            // Act
            boolean exactFit = hole.fits(exactPeg);
            boolean smallerFit = hole.fits(smallerPeg);
            boolean largerFit = hole.fits(largerPeg);

            // Assert
            assertAll(
                () -> assertEquals(5, hole.getRadius()),
                () -> assertEquals(5, exactPeg.getRadius()),
                () -> assertTrue(exactFit),
                () -> assertTrue(smallerFit),
                () -> assertFalse(largerFit)
            );
        }
    }

    @Nested
    @DisplayName("Kare çivinin uyarlanması")
    class SquarePegAdaptation {

        @Test
        @DisplayName("Adapter kare genişliğini yarım köşegen yarıçapına çevirmelidir")
        void shouldTranslateSquareWidthToCircumscribedRadius() {
            // Arrange
            SquarePeg squarePeg = new SquarePeg(8);
            SquarePegAdapter adapter = new SquarePegAdapter(squarePeg);
            double expectedRadius = 8 * Math.sqrt(2) / 2;

            // Act
            double adaptedRadius = adapter.getRadius();

            // Assert
            assertAll(
                () -> assertEquals(8, squarePeg.getWidth()),
                () -> assertEquals(expectedRadius, adaptedRadius, 1.0e-10)
            );
        }

        @Test
        @DisplayName("RoundHole adapter üzerinden SquarePeg'i ayırt etmeden kullanmalıdır")
        void shouldUseAdaptedSquarePegThroughRoundPegContract() {
            // Arrange
            RoundHole hole = new RoundHole(5);
            RoundPeg smallSquareAsRoundPeg = new SquarePegAdapter(new SquarePeg(5));
            RoundPeg largeSquareAsRoundPeg = new SquarePegAdapter(new SquarePeg(10));

            // Act
            boolean smallSquareFits = hole.fits(smallSquareAsRoundPeg);
            boolean largeSquareFits = hole.fits(largeSquareAsRoundPeg);

            // Assert
            assertAll(
                () -> assertTrue(smallSquareFits),
                () -> assertFalse(largeSquareFits)
            );
        }

        @Test
        @DisplayName("Geçerli çok büyük width, ara çarpım taşmasıyla Infinity olmamalıdır")
        void shouldAvoidOverflowWhileCalculatingTheCircumscribedRadius() {
            // Arrange
            SquarePegAdapter adapter = new SquarePegAdapter(
                new SquarePeg(Double.MAX_VALUE)
            );
            RoundHole largestFiniteHole = new RoundHole(Double.MAX_VALUE);
            double expectedRadius = Double.MAX_VALUE / Math.sqrt(2);

            // Act
            double adaptedRadius = adapter.getRadius();

            // Assert
            assertAll(
                () -> assertTrue(Double.isFinite(adaptedRadius)),
                () -> assertEquals(expectedRadius, adaptedRadius),
                () -> assertTrue(largestFiniteHole.fits(adapter))
            );
        }
    }

    @Nested
    @DisplayName("Gerçek sistem entegrasyonu: eski kargo API'si")
    class LegacyCargoIntegration {

        @Test
        @DisplayName("Adapter gramı kilograma, kuruşu TL'ye ve saati güne çevirmelidir")
        void shouldTranslateUnitsAndResponseModel() {
            // Arrange
            ShippingService shippingService = new LegacyCargoAdapter(new LegacyCargoApi());
            Parcel parcel = new Parcel("06000", 2_500);

            // Act
            DeliveryQuote quote = shippingService.quote(parcel);

            // Assert
            assertAll(
                () -> assertEquals("Legacy Cargo", quote.provider()),
                () -> assertEquals(new BigDecimal("96.25"), quote.priceTry()),
                () -> assertEquals(3, quote.estimatedDays())
            );
        }

        @Test
        @DisplayName("Client yalnızca ShippingService target kontratına bağımlı kalmalıdır")
        void shouldHideLegacyApiFromClientCode() {
            // Arrange
            LegacyCargoApi legacyApi = new LegacyCargoApi() {
                @Override
                public LegacyQuote calculate(String postalCode, double weightKilograms) {
                    assertAll(
                        () -> assertEquals("34000", postalCode),
                        () -> assertEquals(1.25, weightKilograms)
                    );
                    return new LegacyQuote(7_550, 25);
                }
            };
            ShippingService service = new LegacyCargoAdapter(legacyApi);

            // Act
            DeliveryQuote quote = service.quote(new Parcel("34000", 1_250));

            // Assert
            assertAll(
                () -> assertEquals(new BigDecimal("75.50"), quote.priceTry()),
                () -> assertEquals(2, quote.estimatedDays())
            );
        }

        @ParameterizedTest(name = "{0} gram için toplam ücret {1} TL olmalıdır")
        @CsvSource({
            "140, 51.75",
            "276, 53.45",
            "280, 53.50"
        })
        @DisplayName("Tam kuruşa denk gelen ağırlıklar double gürültüsüyle yukarı yuvarlanmamalıdır")
        void shouldNotOverchargeAtExactKurusBoundaries(
            int weightGrams,
            String expectedPriceTry
        ) {
            // Arrange
            ShippingService service = new LegacyCargoAdapter(new LegacyCargoApi());

            // Act
            DeliveryQuote quote = service.quote(new Parcel("34000", weightGrams));

            // Assert
            assertEquals(new BigDecimal(expectedPriceTry), quote.priceTry());
        }
    }

    @Nested
    @DisplayName("Sınır ve geçersiz girdi kontratları")
    class InputBoundaries {

        @Test
        @DisplayName("Anlamsız ölçüler ve null bağımlılıklar erken reddedilmelidir")
        void shouldRejectInvalidDimensionsAndDependencies() {
            assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new RoundHole(0)),
                () -> assertThrows(IllegalArgumentException.class, () -> new SquarePeg(-1)),
                () -> assertThrows(NullPointerException.class, () -> new SquarePegAdapter(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Parcel(" ", 100)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Parcel("34000", 0)),
                () -> assertThrows(NullPointerException.class, () -> new LegacyCargoAdapter(null))
            );
        }
    }
}
