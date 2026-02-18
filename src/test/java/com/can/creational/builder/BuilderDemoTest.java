package com.can.creational.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BuilderDemoTest {

    @Nested
    class BuildFlow {

        // Bu test, builder ile alanların adım adım inşa edilip immutable report üretildiğini doğrular.
        @Test
        void shouldBuildReportWithExpectedValues() {
            Report report = Report.builder("Q2 Satış Raporu")
                    .summary("İkinci çeyrek metrikleri")
                    .sections(List.of("Revenue", "Churn"))
                    .includeChart(true)
                    .author("Analytics Team")
                    .build();

            assertEquals("Q2 Satış Raporu", report.title());
            assertEquals("İkinci çeyrek metrikleri", report.summary());
            assertEquals(List.of("Revenue", "Churn"), report.sections());
            assertTrue(report.includeChart());
            assertEquals("Analytics Team", report.author());
            assertTrue(report.exportCard().contains("Chart:YES"));
        }

        // Bu test, addSection zinciri ile section listesinin doğru büyüdüğünü doğrular.
        @Test
        void shouldAppendSectionsWithAddSection() {
            Report report = Report.builder("Incident Review")
                    .addSection("Timeline")
                    .addSection("Root Cause")
                    .addSection("Actions")
                    .author("SRE")
                    .build();

            assertEquals(List.of("Timeline", "Root Cause", "Actions"), report.sections());
        }
    }

    @Nested
    class Validation {

        // Bu test, zorunlu alanların boş geçilmesi halinde anlamlı hata fırlatıldığını doğrular.
        @Test
        void shouldThrowWhenRequiredFieldsAreBlank() {
            IllegalArgumentException titleError = assertThrows(IllegalArgumentException.class,
                    () -> Report.builder("  "));

            IllegalArgumentException summaryError = assertThrows(IllegalArgumentException.class,
                    () -> Report.builder("Valid").summary(" "));

            IllegalArgumentException authorError = assertThrows(IllegalArgumentException.class,
                    () -> Report.builder("Valid").author(" "));

            assertEquals("title cannot be blank", titleError.getMessage());
            assertEquals("summary cannot be blank", summaryError.getMessage());
            assertEquals("author cannot be blank", authorError.getMessage());
        }
    }

    @Nested
    class DirectorFlow {

        // Bu test, director sınıfının hazır inşa tarifleri ile beklenen raporu ürettiğini doğrular.
        @Test
        void shouldBuildPreconfiguredReportsFromDirector() {
            ReportDirector director = new ReportDirector();

            Report quarterly = director.createQuarterlySalesReport();
            Report incident = director.createIncidentPostmortemReport("INC-1");

            assertEquals("Q1 Satış Raporu", quarterly.title());
            assertEquals("Sales Analytics Bot", quarterly.author());
            assertTrue(quarterly.includeChart());

            assertEquals("Incident Postmortem - INC-1", incident.title());
            assertEquals(List.of("Timeline", "Root Cause", "Action Items"), incident.sections());
            assertEquals("SRE Team", incident.author());
        }
    }
}
