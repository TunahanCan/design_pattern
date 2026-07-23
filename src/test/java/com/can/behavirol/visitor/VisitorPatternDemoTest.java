package com.can.behavirol.visitor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Visitor — coğrafi düğümlerde çift dispatch")
class VisitorPatternDemoTest {

    @Nested
    @DisplayName("Element accept çağrısı aldığında")
    class DoubleDispatch {

        @Test
        @DisplayName("her concrete element kendisine ait visit metodunu seçer")
        void eachElementDispatchesToItsMatchingVisitMethod() {
            // Arrange
            DispatchRecordingVisitor visitor = new DispatchRecordingVisitor();
            List<GeoNode> nodes = List.of(
                    new City("Ankara", 5_800_000),
                    new Industry("Aegean Textiles", "textile"),
                    new SightSeeing("Anıtkabir", 900_000)
            );

            // Act
            nodes.forEach(node -> node.accept(visitor));

            // Assert
            assertEquals(
                    List.of("city:Ankara", "industry:Aegean Textiles", "sightseeing:Anıtkabir"),
                    visitor.visits
            );
        }
    }

    @Nested
    @DisplayName("Element domain sınırında")
    class DomainInvariants {

        @Test
        @DisplayName("null veya blank ad ve sektör açık IllegalArgumentException üretir")
        void rejectsMissingNamesAndSectorBeforeAVisitorReceivesThem() {
            // Arrange & Act
            IllegalArgumentException cityNameError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new City(null, 1)
            );
            IllegalArgumentException industryNameError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Industry(" ", "technology")
            );
            IllegalArgumentException sectorError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Industry("Factory", null)
            );
            IllegalArgumentException sightNameError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new SightSeeing(" ", 1)
            );

            // Assert
            assertAll(
                    () -> assertEquals("name cannot be blank", cityNameError.getMessage()),
                    () -> assertEquals("name cannot be blank", industryNameError.getMessage()),
                    () -> assertEquals("sector cannot be blank", sectorError.getMessage()),
                    () -> assertEquals("name cannot be blank", sightNameError.getMessage())
            );
        }

        @Test
        @DisplayName("population ve annualVisitors negatif olamaz")
        void rejectsNegativeCountersBeforeAggregationOrExport() {
            // Arrange & Act
            IllegalArgumentException populationError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new City("Ankara", -1)
            );
            IllegalArgumentException visitorError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new SightSeeing("Museum", -1)
            );

            // Assert
            assertAll(
                    () -> assertEquals(
                            "population cannot be negative",
                            populationError.getMessage()
                    ),
                    () -> assertEquals(
                            "annualVisitors cannot be negative",
                            visitorError.getMessage()
                    )
            );
        }

        @Test
        @DisplayName("geçerli metinler element sınırında trim edilir")
        void normalizesValidTextBeforeVisitorsUseIt() {
            // Arrange & Act
            City city = new City("  Ankara  ", 1);
            Industry industry = new Industry("  Factory  ", "  technology  ");
            SightSeeing sight = new SightSeeing("  Museum  ", 1);

            // Assert
            assertAll(
                    () -> assertEquals("Ankara", city.getName()),
                    () -> assertEquals("Factory", industry.getName()),
                    () -> assertEquals("technology", industry.getSector()),
                    () -> assertEquals("Museum", sight.getName())
            );
        }
    }

    @Nested
    @DisplayName("XML export visitor düğümleri ziyaret ettiğinde")
    class XmlExport {

        @Test
        @DisplayName("üç concrete tip kendi attribute'larıyla XML fragment üretir")
        void exportsEveryConcreteNodeType() {
            // Arrange
            XmlExportVisitor visitor = new XmlExportVisitor();
            List<GeoNode> nodes = List.of(
                    new City("Ankara", 5_800_000),
                    new Industry("Aegean Textiles", "textile"),
                    new SightSeeing("Anitkabir", 900_000)
            );

            // Act
            nodes.forEach(node -> node.accept(visitor));

            // Assert
            assertEquals(
                    List.of(
                            "<city name=\"Ankara\" population=\"5800000\" />",
                            "<industry name=\"Aegean Textiles\" sector=\"textile\" />",
                            "<sightseeing name=\"Anitkabir\" annualVisitors=\"900000\" />"
                    ),
                    visitor.getXmlRows()
            );
        }

        @Test
        @DisplayName("sunulan sonuç listesi dışarıdan değiştirilemez")
        void resultViewIsUnmodifiable() {
            // Arrange
            XmlExportVisitor visitor = new XmlExportVisitor();
            new City("Ankara", 5_800_000).accept(visitor);

            // Act
            List<String> rows = visitor.getXmlRows();

            // Assert
            assertThrows(UnsupportedOperationException.class, () -> rows.add("<invalid />"));
        }

        @Test
        @DisplayName("XML attribute içindeki özel karakterler güvenli biçimde escape edilir")
        void escapesSpecialCharactersInAttributeValues() {
            // Arrange
            XmlExportVisitor visitor = new XmlExportVisitor();

            // Act
            new Industry("Ar-Ge & \"Test\" <Lab>", "food's").accept(visitor);

            // Assert
            assertEquals(
                    "<industry name=\"Ar-Ge &amp; &quot;Test&quot; &lt;Lab&gt;\" "
                            + "sector=\"food&apos;s\" />",
                    visitor.getXmlRows().getFirst()
            );
        }
    }

    @Nested
    @DisplayName("Risk audit visitor eşiklere göre karar verdiğinde")
    class RiskAudit {

        @Test
        @DisplayName("bir milyonun üzerindeki şehir yüksek yoğunluk notu alır")
        void largeCityGetsHighDensityNote() {
            // Arrange
            RiskAuditVisitor visitor = new RiskAuditVisitor();

            // Act
            new City("Ankara", 5_800_000).accept(visitor);

            // Assert
            assertEquals(
                    "Ankara: Yüksek yoğunluk - deprem/tahliye planı kritik.",
                    visitor.getNotes().getFirst()
            );
        }

        @Test
        @DisplayName("chemical sektör karşılaştırması büyük küçük harfe duyarsızdır")
        void chemicalIndustryGetsAdditionalAuditNote() {
            // Arrange
            RiskAuditVisitor visitor = new RiskAuditVisitor();

            // Act
            new Industry("Kimya Tesisi", "CHEMICAL").accept(visitor);

            // Assert
            assertEquals(
                    "Kimya Tesisi: Kimya sektörü - ek güvenlik denetimi gerekli.",
                    visitor.getNotes().getFirst()
            );
        }

        @Test
        @DisplayName("beş yüz binin üzerindeki ziyaretçi trafiği yüksek kabul edilir")
        void popularSightGetsCrowdControlNote() {
            // Arrange
            RiskAuditVisitor visitor = new RiskAuditVisitor();

            // Act
            new SightSeeing("Anıtkabir", 900_000).accept(visitor);

            // Assert
            assertEquals(
                    "Anıtkabir: Yüksek ziyaretçi trafiği - crowd control artırılmalı.",
                    visitor.getNotes().getFirst()
            );
        }

        @Test
        @DisplayName("visitor ziyaret sırasını sonuç notlarında korur")
        void notesAccumulateInVisitOrder() {
            // Arrange
            RiskAuditVisitor visitor = new RiskAuditVisitor();

            // Act
            new City("Küçükşehir", 250_000).accept(visitor);
            new Industry("Tekstil", "textile").accept(visitor);

            // Assert
            assertAll(
                    () -> assertEquals(2, visitor.getNotes().size()),
                    () -> assertEquals(
                            "Küçükşehir: Orta yoğunluk - standart afet planı yeterli.",
                            visitor.getNotes().get(0)
                    ),
                    () -> assertEquals(
                            "Tekstil: textile sektörü - rutin denetim.",
                            visitor.getNotes().get(1)
                    )
            );
        }
    }

    @Nested
    @DisplayName("Yeni bir raporlama operasyonu elementlere eklenmeden tanımlandığında")
    class SummaryVisitor {

        @Test
        @DisplayName("heterojen düğümler tek visitor ile tip güvenli biçimde özetlenir")
        void aggregatesStatisticsAcrossDifferentElementTypes() {
            // Arrange
            GeoSummaryVisitor visitor = new GeoSummaryVisitor();
            List<GeoNode> nodes = List.of(
                    new City("Ankara", 5_800_000),
                    new City("Eskişehir", 900_000),
                    new Industry("Savunma", "technology"),
                    new SightSeeing("Anıtkabir", 900_000),
                    new SightSeeing("Müze", 120_000)
            );

            // Act
            nodes.forEach(node -> node.accept(visitor));

            // Assert
            assertEquals(
                    new GeoSummary(2, 6_700_000, 1, 2, 1_020_000),
                    visitor.getSummary()
            );
        }
    }

    private static final class DispatchRecordingVisitor implements GeoNodeVisitor {
        private final java.util.ArrayList<String> visits = new java.util.ArrayList<>();

        @Override
        public void visitCity(City city) {
            visits.add("city:" + city.getName());
        }

        @Override
        public void visitIndustry(Industry industry) {
            visits.add("industry:" + industry.getName());
        }

        @Override
        public void visitSightSeeing(SightSeeing sightSeeing) {
            visits.add("sightseeing:" + sightSeeing.getName());
        }
    }
}
