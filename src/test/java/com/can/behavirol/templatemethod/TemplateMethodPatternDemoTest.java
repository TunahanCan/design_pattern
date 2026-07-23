package com.can.behavirol.templatemethod;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Template Method — doküman madenciliği akışı")
class TemplateMethodPatternDemoTest {

    @Nested
    @DisplayName("Üst sınıftaki template method çalıştığında")
    class AlgorithmSkeleton {

        @Test
        @DisplayName("primitive operation ve hook'lar tanımlı sırayla çağrılır")
        void invokesStepsInTheTemplateOrder() {
            // Arrange
            RecordingMiner miner = new RecordingMiner();

            // Act
            String report = miner.process("veri.demo");

            // Assert
            assertAll(
                    () -> assertEquals(
                            List.of(
                                    "open:veri.demo",
                                    "extract:veri.demo",
                                    "before:raw",
                                    "analyze:raw",
                                    "report:analyzed",
                                    "after:report",
                                    "close:veri.demo"
                            ),
                            miner.calls
                    ),
                    () -> assertEquals("report", report)
            );
        }
    }

    @Nested
    @DisplayName("PDF miner varsayılan adımları kullandığında")
    class PdfProcessing {

        @Test
        @DisplayName("base analyze ve report davranışı PDF verisine uygulanır")
        void producesReportWithDefaultOperations() {
            // Arrange
            DocumentMiningTemplate miner = new PdfDocumentMiner();

            // Act
            String report = miner.process("kurumsal-fatura.pdf");

            // Assert
            assertAll(
                    () -> assertTrue(report.startsWith("RAPOR:")),
                    () -> assertTrue(report.contains("PDF: FATURA KALEMLERI")),
                    () -> assertTrue(report.contains("TOPLAM TUTAR"))
            );
        }
    }

    @Nested
    @DisplayName("CSV miner adımları override ettiğinde")
    class CsvProcessing {

        @Test
        @DisplayName("format özel analiz ve rapor üretimi kullanılır")
        void producesCsvSpecificReport() {
            // Arrange
            DocumentMiningTemplate miner = new CsvDocumentMiner();

            // Act
            String report = miner.process("satis-raporu.csv");

            // Assert
            assertAll(
                    () -> assertTrue(report.startsWith("CSV RAPORU:")),
                    () -> assertTrue(report.contains("CSV özel analizi")),
                    () -> assertTrue(report.contains("satır bazlı normalizasyon"))
            );
        }
    }

    @Nested
    @DisplayName("Akışın ortasında hata oluştuğunda")
    class ResourceSafety {

        @Test
        @DisplayName("template method close adımını finally ile yine de çalıştırır")
        void closesTheFileWhenAnalysisFails() {
            // Arrange
            FailingMiner miner = new FailingMiner();

            // Act
            IllegalStateException error = assertThrows(
                    IllegalStateException.class,
                    () -> miner.process("bozuk.pdf")
            );

            // Assert
            assertAll(
                    () -> assertEquals("Analiz başarısız", error.getMessage()),
                    () -> assertEquals(
                            List.of("open", "extract", "analyze", "close"),
                            miner.calls
                    )
            );
        }

        @Test
        @DisplayName("iş hatası primary kalır, close Error'ı suppressed olarak eklenir")
        void runtimePrimaryKeepsItsIdentityAndSuppressesCloseError() {
            // Arrange
            IllegalStateException primaryFailure = new IllegalStateException("primary");
            AssertionError closeFailure = new AssertionError("close");
            FailureScenarioMiner miner = new FailureScenarioMiner(
                    primaryFailure,
                    closeFailure
            );

            // Act
            IllegalStateException thrown = assertThrows(
                    IllegalStateException.class,
                    () -> miner.process("runtime-primary.demo")
            );

            // Assert
            assertAll(
                    () -> assertSame(primaryFailure, thrown),
                    () -> assertArrayEquals(
                            new Throwable[]{closeFailure},
                            thrown.getSuppressed()
                    ),
                    () -> assertEquals(1, miner.closeCalls)
            );
        }

        @Test
        @DisplayName("iş Error'ı primary kalır, close RuntimeException'ı suppressed olur")
        void errorPrimaryKeepsItsIdentityAndSuppressesCloseRuntimeFailure() {
            // Arrange
            AssertionError primaryFailure = new AssertionError("primary");
            IllegalArgumentException closeFailure = new IllegalArgumentException("close");
            FailureScenarioMiner miner = new FailureScenarioMiner(
                    primaryFailure,
                    closeFailure
            );

            // Act
            AssertionError thrown = assertThrows(
                    AssertionError.class,
                    () -> miner.process("error-primary.demo")
            );

            // Assert
            assertAll(
                    () -> assertSame(primaryFailure, thrown),
                    () -> assertArrayEquals(
                            new Throwable[]{closeFailure},
                            thrown.getSuppressed()
                    ),
                    () -> assertEquals(1, miner.closeCalls)
            );
        }

        @Test
        @DisplayName("yalnız close başarısızsa close hatası kendi identity'siyle primary olur")
        void closeFailureIsPrimaryWhenProcessingSucceeds() {
            // Arrange
            AssertionError closeFailure = new AssertionError("close-only");
            FailureScenarioMiner miner = new FailureScenarioMiner(null, closeFailure);

            // Act
            AssertionError thrown = assertThrows(
                    AssertionError.class,
                    () -> miner.process("close-only.demo")
            );

            // Assert
            assertAll(
                    () -> assertSame(closeFailure, thrown),
                    () -> assertEquals(0, thrown.getSuppressed().length),
                    () -> assertEquals(1, miner.closeCalls)
            );
        }
    }

    private static final class RecordingMiner extends DocumentMiningTemplate {
        private final List<String> calls = new ArrayList<>();

        @Override
        protected void openFile(String fileName) {
            calls.add("open:" + fileName);
        }

        @Override
        protected String extractRawData(String fileName) {
            calls.add("extract:" + fileName);
            return "raw";
        }

        @Override
        protected void beforeAnalyze(String rawData) {
            calls.add("before:" + rawData);
        }

        @Override
        protected String analyzeData(String rawData) {
            calls.add("analyze:" + rawData);
            return "analyzed";
        }

        @Override
        protected String createReport(String analyzedData) {
            calls.add("report:" + analyzedData);
            return "report";
        }

        @Override
        protected void afterReport(String report) {
            calls.add("after:" + report);
        }

        @Override
        protected void closeFile(String fileName) {
            calls.add("close:" + fileName);
        }
    }

    private static final class FailingMiner extends DocumentMiningTemplate {
        private final List<String> calls = new ArrayList<>();

        @Override
        protected void openFile(String fileName) {
            calls.add("open");
        }

        @Override
        protected String extractRawData(String fileName) {
            calls.add("extract");
            return "raw";
        }

        @Override
        protected String analyzeData(String rawData) {
            calls.add("analyze");
            throw new IllegalStateException("Analiz başarısız");
        }

        @Override
        protected void closeFile(String fileName) {
            calls.add("close");
        }
    }

    private static final class FailureScenarioMiner extends DocumentMiningTemplate {
        private final Throwable processingFailure;
        private final Throwable closeFailure;
        private int closeCalls;

        private FailureScenarioMiner(Throwable processingFailure, Throwable closeFailure) {
            this.processingFailure = processingFailure;
            this.closeFailure = closeFailure;
        }

        @Override
        protected void openFile(String fileName) {
        }

        @Override
        protected String extractRawData(String fileName) {
            return "raw";
        }

        @Override
        protected String analyzeData(String rawData) {
            throwIfPresent(processingFailure);
            return "analyzed";
        }

        @Override
        protected void closeFile(String fileName) {
            closeCalls++;
            throwIfPresent(closeFailure);
        }

        private static void throwIfPresent(Throwable failure) {
            if (failure instanceof RuntimeException runtimeFailure) {
                throw runtimeFailure;
            }
            if (failure instanceof Error errorFailure) {
                throw errorFailure;
            }
        }
    }
}
