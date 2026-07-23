package com.can.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pattern kataloğu: örnekleri aile ve isimle güvenilir biçimde gruplar")
class PatternCatalogTest {

    @Nested
    @DisplayName("Envanter sözleşmesi")
    class InventoryContract {

        @Test
        @DisplayName("GoF kataloğundaki 22 örneğin tamamı tek kaynaktan sunulur")
        void exposesAllTwentyTwoExamples() {
            assertEquals(22, PatternCatalog.all().size());
        }

        @Test
        @DisplayName("Her slug benzersizdir; komut satırında yanlış demo seçilmez")
        void requiresUniqueSlugs() {
            List<String> slugs = PatternCatalog.all().stream()
                    .map(PatternExample::slug)
                    .toList();

            assertEquals(slugs.size(), new HashSet<>(slugs).size());
        }

        @Test
        @DisplayName("Aileler GoF dağılımını korur: 5 oluşturucu, 7 yapısal, 10 davranışsal")
        void preservesFamilyDistribution() {
            assertEquals(5, PatternCatalog.byFamily(PatternFamily.CREATIONAL).size());
            assertEquals(7, PatternCatalog.byFamily(PatternFamily.STRUCTURAL).size());
            assertEquals(10, PatternCatalog.byFamily(PatternFamily.BEHAVIORAL).size());
        }

        @Test
        @DisplayName("Ana katalog add, set ve clear üzerinden değiştirilemez")
        void catalogIsImmutableThroughEveryListMutationPath() {
            PatternExample first = PatternCatalog.all().getFirst();

            assertThrows(UnsupportedOperationException.class,
                    () -> PatternCatalog.all().add(first));
            assertThrows(UnsupportedOperationException.class,
                    () -> PatternCatalog.all().set(0, first));
            assertThrows(UnsupportedOperationException.class,
                    () -> PatternCatalog.all().clear());
        }

        @Test
        @DisplayName("Aile görünümü de ana katalogla aynı immutability sözleşmesini taşır")
        void familyViewIsImmutable() {
            List<PatternExample> structural = PatternCatalog.byFamily(PatternFamily.STRUCTURAL);
            PatternExample first = structural.getFirst();

            assertThrows(UnsupportedOperationException.class,
                    () -> structural.add(first));
            assertThrows(UnsupportedOperationException.class,
                    () -> structural.set(0, first));
            assertThrows(UnsupportedOperationException.class, structural::clear);
        }
    }

    @Nested
    @DisplayName("Seçim davranışı")
    class Selection {

        @Test
        @DisplayName("'all' ve boş seçim bütün desenleri katalog sırasıyla döndürür")
        void selectsAllExamples() {
            assertEquals(PatternCatalog.all(), PatternCatalog.select("all"));
            assertEquals(PatternCatalog.all(), PatternCatalog.select("  "));
            assertEquals(PatternCatalog.all(), PatternCatalog.select(null));
        }

        @Test
        @DisplayName("Aile slug'ı yalnızca o ailenin örneklerini döndürür")
        void selectsFamily() {
            List<PatternExample> structural = PatternCatalog.select(" STRUCTURAL ");

            assertEquals(7, structural.size());
            assertTrue(structural.stream()
                    .allMatch(example -> example.family() == PatternFamily.STRUCTURAL));
        }

        @Test
        @DisplayName("Türkçe aile adı büyük harfle yazılsa da noktalı/noktasız i dönüşümü korunur")
        void selectsUppercaseTurkishFamilyName() {
            assertEquals(
                    PatternCatalog.byFamily(PatternFamily.BEHAVIORAL),
                    PatternCatalog.select("DAVRANIŞSAL")
            );
            assertEquals(
                    PatternCatalog.byFamily(PatternFamily.STRUCTURAL),
                    PatternCatalog.select("YAPISAL")
            );
        }

        @Test
        @DisplayName("Pattern slug'ı tek ve doğru demoyu seçer")
        void selectsSinglePattern() {
            List<PatternExample> result = PatternCatalog.select("factory-method");

            assertEquals(1, result.size());
            assertEquals("Factory Method", result.getFirst().displayName());
        }

        @Test
        @DisplayName("Bilinmeyen seçim sessizce tüm demoları çalıştırmak yerine hata verir")
        void rejectsUnknownSelection() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> PatternCatalog.select("factory")
            );

            assertTrue(exception.getMessage().contains("--list"));
        }
    }

    @Nested
    @DisplayName("Çalıştırma ve çıktı sözleşmesi")
    class Execution {

        @Test
        @DisplayName("Önceden seçilmiş örnekleri sırayla çalıştırır ve aile geçişlerini başlıklandırır")
        void executesPreselectedExamplesInOrder() {
            List<String> calls = new ArrayList<>();
            List<PatternExample> selected = List.of(
                    new PatternExample(
                            "first-creation",
                            "First Creation",
                            PatternFamily.CREATIONAL,
                            () -> calls.add("first")
                    ),
                    new PatternExample(
                            "second-creation",
                            "Second Creation",
                            PatternFamily.CREATIONAL,
                            () -> calls.add("second")
                    ),
                    new PatternExample(
                            "structural-step",
                            "Structural Step",
                            PatternFamily.STRUCTURAL,
                            () -> calls.add("third")
                    )
            );
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            int executed = PatternCatalog.run(
                    selected,
                    new PrintStream(bytes, true, StandardCharsets.UTF_8)
            );

            String output = bytes.toString(StandardCharsets.UTF_8);
            assertEquals(3, executed);
            assertEquals(List.of("first", "second", "third"), calls);
            assertTrue(output.contains("=== OLUŞTURUCU / CREATIONAL ==="));
            assertTrue(output.contains("--- First Creation [first-creation] ---"));
            assertTrue(output.contains("--- Second Creation [second-creation] ---"));
            assertTrue(output.contains("=== YAPISAL / STRUCTURAL ==="));
        }

        @Test
        @DisplayName("Çağıranın mutable seçim listesi execution başında immutable snapshot'a çevrilir")
        void snapshotsMutableSelectionBeforeExecution() {
            List<PatternExample> selected = new ArrayList<>();
            AtomicInteger executionCount = new AtomicInteger();
            selected.add(new PatternExample(
                    "mutating-demo",
                    "Mutating Demo",
                    PatternFamily.CREATIONAL,
                    selected::clear
            ));
            selected.add(new PatternExample(
                    "following-demo",
                    "Following Demo",
                    PatternFamily.CREATIONAL,
                    executionCount::incrementAndGet
            ));

            int executed = PatternCatalog.run(selected, new PrintStream(
                    new ByteArrayOutputStream(),
                    true,
                    StandardCharsets.UTF_8
            ));

            assertEquals(2, executed);
            assertEquals(1, executionCount.get());
            assertTrue(selected.isEmpty());
        }

        @Test
        @DisplayName("Demo IllegalArgumentException atarsa katalog onu selector hatasına dönüştürmez")
        void propagatesDemoFailureWithoutMaskingIt() {
            IllegalArgumentException domainFailure =
                    new IllegalArgumentException("domain doğrulaması başarısız");
            PatternExample failing = new PatternExample(
                    "failing-demo",
                    "Failing Demo",
                    PatternFamily.BEHAVIORAL,
                    () -> {
                        throw domainFailure;
                    }
            );

            IllegalArgumentException thrown = assertThrows(
                    IllegalArgumentException.class,
                    () -> PatternCatalog.run(
                            List.of(failing),
                            new PrintStream(new ByteArrayOutputStream())
                    )
            );

            assertSame(domainFailure, thrown);
        }

        @Test
        @DisplayName("Yazdırılan katalog kullanım seçeneklerini ve 22 slug'ın tamamını gösterir")
        void printsCompleteCatalogContract() {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            PatternCatalog.printCatalog(
                    new PrintStream(bytes, true, StandardCharsets.UTF_8)
            );

            String output = bytes.toString(StandardCharsets.UTF_8);
            assertTrue(output.startsWith(
                    "Kullanım: all | <aile> | <pattern-slug> | --list | --help"
            ));
            PatternCatalog.all().forEach(example ->
                    assertTrue(output.contains(example.slug()), example.slug())
            );
        }
    }

    @Nested
    @DisplayName("Katalog kaydı doğrulaması")
    class EntryValidation {

        @Test
        @DisplayName("Boş görünen adlar daha katalog kurulurken reddedilir")
        void rejectsBlankText() {
            assertThrows(IllegalArgumentException.class,
                    () -> new PatternExample(
                            "valid-slug",
                            " ",
                            PatternFamily.CREATIONAL,
                            () -> {
                            }
                    ));
        }

        @Test
        @DisplayName("URL ve komut dostu olmayan slug reddedilir")
        void rejectsInvalidSlug() {
            assertThrows(IllegalArgumentException.class,
                    () -> new PatternExample(
                            "Factory Method",
                            "Factory Method",
                            PatternFamily.CREATIONAL,
                            () -> {
                            }
                    ));
        }

        @Test
        @DisplayName("Aranan desen yoksa Optional boş döner")
        void missingPatternReturnsEmptyOptional() {
            assertFalse(PatternCatalog.find("not-a-pattern").isPresent());
        }
    }
}
