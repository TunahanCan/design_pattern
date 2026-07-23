package com.can.creational.builder;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Builder — adımlar tamamlandığında immutable Report üretilir")
class BuilderDemoTest {

    @Nested
    @DisplayName("Fluent kurulum")
    class FluentConstruction {

        @Test
        @DisplayName("İsimli adımlar bütün Report alanlarını okunabilir biçimde kurar")
        void namedStepsBuildACompleteReport() {
            // Arrange & Act
            Report report = Report.builder("Q2 Satış Raporu")
                    .summary("İkinci çeyrek metrikleri")
                    .sections(List.of("Revenue", "Churn"))
                    .includeChart(true)
                    .author("Analytics Team")
                    .build();

            // Assert
            assertAll(
                    () -> assertEquals("Q2 Satış Raporu", report.title()),
                    () -> assertEquals("İkinci çeyrek metrikleri", report.summary()),
                    () -> assertEquals(List.of("Revenue", "Churn"), report.sections()),
                    () -> assertTrue(report.includeChart()),
                    () -> assertEquals("Analytics Team", report.author()),
                    () -> assertEquals(
                            "[REPORT] Title:Q2 Satış Raporu | Author:Analytics Team"
                                    + " | Summary:İkinci çeyrek metrikleri"
                                    + " | Sections:Revenue, Churn | Chart:YES",
                            report.exportCard()
                    )
            );
        }

        @Test
        @DisplayName("addSection çağrıları ekleme sırasını korur")
        void addSectionPreservesInsertionOrder() {
            // Arrange & Act
            Report report = Report.builder("Incident Review")
                    .addSection("Timeline")
                    .addSection("Root Cause")
                    .addSection("Actions")
                    .build();

            // Assert
            assertEquals(List.of("Timeline", "Root Cause", "Actions"), report.sections());
        }

        @Test
        @DisplayName("Opsiyonel adımlar çağrılmazsa belgelenmiş default'lar kullanılır")
        void omittedOptionalStepsUseDocumentedDefaults() {
            // Arrange & Act
            Report report = Report.builder("Minimal Report").build();

            // Assert
            assertAll(
                    () -> assertEquals("No summary", report.summary()),
                    () -> assertEquals(List.of(), report.sections()),
                    () -> assertFalse(report.includeChart()),
                    () -> assertEquals("Unknown", report.author()),
                    () -> assertTrue(report.exportCard().contains("Sections:No sections"))
            );
        }
    }

    @Nested
    @DisplayName("Normalizasyon ve doğrulama sınırı")
    class ValidationBoundary {

        @Test
        @DisplayName("Title ve fluent String alanları çevre boşluklarını temizler")
        void stringFieldsAreTrimmedAtTheBuilderBoundary() {
            // Arrange & Act
            Report report = Report.builder("  Weekly Report  ")
                    .summary("  Stable systems  ")
                    .addSection("  Availability  ")
                    .author("  SRE Team  ")
                    .build();

            // Assert
            assertAll(
                    () -> assertEquals("Weekly Report", report.title()),
                    () -> assertEquals("Stable systems", report.summary()),
                    () -> assertEquals(List.of("Availability"), report.sections()),
                    () -> assertEquals("SRE Team", report.author())
            );
        }

        @Test
        @DisplayName("Toplu sections adımı her elemanı addSection ile aynı biçimde doğrular")
        void bulkSectionsAreNormalizedWithTheSameRuleAsSingleSections() {
            // Arrange & Act
            Report report = Report.builder("Consistent validation")
                    .sections(List.of("  Overview ", " Risks  "))
                    .build();
            IllegalArgumentException blankSectionError = assertThrows(
                    IllegalArgumentException.class,
                    () -> Report.builder("Invalid").sections(List.of("Overview", "  "))
            );

            // Assert
            assertAll(
                    () -> assertEquals(List.of("Overview", "Risks"), report.sections()),
                    () -> assertEquals("section cannot be blank", blankSectionError.getMessage())
            );
        }

        @Test
        @DisplayName("Blank title, summary, author ve section alan adını taşıyan hata verir")
        void blankStringsProduceFieldSpecificErrors() {
            // Arrange & Act
            IllegalArgumentException titleError = assertThrows(
                    IllegalArgumentException.class,
                    () -> Report.builder("  ")
            );
            IllegalArgumentException summaryError = assertThrows(
                    IllegalArgumentException.class,
                    () -> Report.builder("Valid").summary(" ")
            );
            IllegalArgumentException authorError = assertThrows(
                    IllegalArgumentException.class,
                    () -> Report.builder("Valid").author(" ")
            );
            IllegalArgumentException sectionError = assertThrows(
                    IllegalArgumentException.class,
                    () -> Report.builder("Valid").addSection(" ")
            );

            // Assert
            assertAll(
                    () -> assertEquals("title cannot be blank", titleError.getMessage()),
                    () -> assertEquals("summary cannot be blank", summaryError.getMessage()),
                    () -> assertEquals("author cannot be blank", authorError.getMessage()),
                    () -> assertEquals("section cannot be blank", sectionError.getMessage())
            );
        }

        @Test
        @DisplayName("Null title ve section listesi erken reddedilir")
        void nullRequiredInputsAreRejectedEarly() {
            // Arrange & Act
            NullPointerException titleError = assertThrows(
                    NullPointerException.class,
                    () -> Report.builder(null)
            );
            NullPointerException sectionsError = assertThrows(
                    NullPointerException.class,
                    () -> Report.builder("Valid").sections(null)
            );

            // Assert
            assertAll(
                    () -> assertEquals("title cannot be null", titleError.getMessage()),
                    () -> assertEquals("sections cannot be null", sectionsError.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Immutable product ve defensive copy")
    class ImmutableProduct {

        @Test
        @DisplayName("Kaynak liste değişse bile oluşturulan Report snapshot'ı değişmez")
        void reportDoesNotShareTheInputList() {
            // Arrange
            List<String> sourceSections = new ArrayList<>(List.of("Summary"));
            Report report = Report.builder("Snapshot")
                    .sections(sourceSections)
                    .build();

            // Act
            sourceSections.add("Late mutation");

            // Assert
            assertEquals(List.of("Summary"), report.sections());
        }

        @Test
        @DisplayName("Report sections görünümü dışarıdan değiştirilemez")
        void reportSectionsCannotBeMutatedByTheCaller() {
            // Arrange
            Report report = Report.builder("Immutable")
                    .addSection("Overview")
                    .build();

            // Act & Assert
            assertThrows(UnsupportedOperationException.class, () -> report.sections().add("Injected"));
        }

        @Test
        @DisplayName("Builder tekrar kullanılsa bile önceki Report değişmeden kalır")
        void eachBuildCreatesAnIndependentSnapshot() {
            // Arrange
            Report.Builder builder = Report.builder("Reusable")
                    .addSection("First");
            Report first = builder.build();

            // Act
            Report second = builder.addSection("Second").build();

            // Assert
            assertAll(
                    () -> assertEquals(List.of("First"), first.sections()),
                    () -> assertEquals(List.of("First", "Second"), second.sections())
            );
        }
    }

    @Nested
    @DisplayName("Gerçekçi varyant üretimi")
    class DerivedReportVariants {

        @Test
        @DisplayName("toBuilder mevcut alanları taşır ve yalnız seçilen kararları değiştirir")
        void toBuilderDerivesANewReportWithoutMutatingTheSource() {
            // Arrange
            Report detailed = Report.builder("Q2 Sales")
                    .summary("Detailed quarterly results")
                    .sections(List.of("Revenue", "Regions", "Risks"))
                    .includeChart(true)
                    .author("Analytics")
                    .build();

            // Act
            Report executive = detailed.toBuilder()
                    .summary("One-page executive summary")
                    .sections(List.of("Revenue", "Risks"))
                    .author("Executive Reporting")
                    .build();

            // Assert
            assertAll(
                    () -> assertEquals("Detailed quarterly results", detailed.summary()),
                    () -> assertEquals(List.of("Revenue", "Regions", "Risks"), detailed.sections()),
                    () -> assertEquals("Analytics", detailed.author()),
                    () -> assertEquals("Q2 Sales", executive.title()),
                    () -> assertEquals("One-page executive summary", executive.summary()),
                    () -> assertEquals(List.of("Revenue", "Risks"), executive.sections()),
                    () -> assertTrue(executive.includeChart()),
                    () -> assertEquals("Executive Reporting", executive.author())
            );
        }
    }

    @Nested
    @DisplayName("Director hazır kurulum reçeteleri sunar")
    class DirectorRecipes {

        @Test
        @DisplayName("Quarterly reçetesi satış raporunun bütün kararlarını uygular")
        void quarterlyRecipeBuildsTheExpectedReport() {
            // Arrange
            ReportDirector director = new ReportDirector();

            // Act
            Report report = director.createQuarterlySalesReport();

            // Assert
            assertAll(
                    () -> assertEquals("Q1 Satış Raporu", report.title()),
                    () -> assertEquals("İlk çeyrek satış performansı", report.summary()),
                    () -> assertEquals(List.of("Özet", "Bölgesel Dağılım", "Riskler"), report.sections()),
                    () -> assertTrue(report.includeChart()),
                    () -> assertEquals("Sales Analytics Bot", report.author())
            );
        }

        @Test
        @DisplayName("Incident reçetesi ID'yi başlığa taşır ve chartsız postmortem üretir")
        void incidentRecipeBuildsTheExpectedPostmortem() {
            // Arrange
            ReportDirector director = new ReportDirector();

            // Act
            Report report = director.createIncidentPostmortemReport("INC-1");

            // Assert
            assertAll(
                    () -> assertEquals("Incident Postmortem - INC-1", report.title()),
                    () -> assertEquals(
                            "Olayın kök neden analizi ve iyileştirme aksiyonları",
                            report.summary()
                    ),
                    () -> assertEquals(List.of("Timeline", "Root Cause", "Action Items"), report.sections()),
                    () -> assertFalse(report.includeChart()),
                    () -> assertEquals("SRE Team", report.author())
            );
        }

        @Test
        @DisplayName("Incident reçetesi ID'yi normalize eder; eksik ID'yi erken reddeder")
        void incidentRecipeValidatesAndNormalizesTheIdentifier() {
            // Arrange
            ReportDirector director = new ReportDirector();

            // Act
            Report normalized = director.createIncidentPostmortemReport("  INC-42 ");
            IllegalArgumentException blankError = assertThrows(
                    IllegalArgumentException.class,
                    () -> director.createIncidentPostmortemReport(" ")
            );
            NullPointerException nullError = assertThrows(
                    NullPointerException.class,
                    () -> director.createIncidentPostmortemReport(null)
            );

            // Assert
            assertAll(
                    () -> assertEquals("Incident Postmortem - INC-42", normalized.title()),
                    () -> assertEquals("incidentId cannot be blank", blankError.getMessage()),
                    () -> assertEquals("incidentId cannot be null", nullError.getMessage())
            );
        }
    }
}
