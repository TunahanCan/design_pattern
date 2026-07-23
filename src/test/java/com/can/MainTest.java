package com.can;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Main CLI: kullanıcı seçimini güvenilir bir process sözleşmesine dönüştürür")
class MainTest {

    @Nested
    @DisplayName("Yardım ve katalog")
    class HelpAndCatalog {

        @Test
        @DisplayName("--list kataloğu stdout'a basar ve başarı kodu döndürür")
        void listsCatalogSuccessfully() {
            CliResult result = execute("--list");

            assertEquals(0, result.exitCode());
            assertTrue(result.output().contains("factory-method"));
            assertTrue(result.output().contains("visitor"));
            assertTrue(result.output().contains("--help"));
            assertTrue(result.error().isEmpty());
        }

        @Test
        @DisplayName("--help, --list ile aynı keşfedilebilir kataloğu sunar")
        void helpShowsTheCatalog() {
            CliResult result = execute("--help");

            assertEquals(0, result.exitCode());
            assertTrue(result.output().startsWith("Kullanım:"));
            assertTrue(result.error().isEmpty());
        }
    }

    @Nested
    @DisplayName("Geçersiz kullanıcı girdisi")
    class InvalidInput {

        @Test
        @DisplayName("Bilinmeyen selector stderr'e açıklama basar ve kullanım hatası kodu döndürür")
        void rejectsUnknownSelector() {
            CliResult result = execute("does-not-exist");

            assertEquals(2, result.exitCode());
            assertTrue(result.output().isEmpty());
            assertTrue(result.error().contains("Bilinmeyen seçim"));
            assertTrue(result.error().contains("--list"));
        }

        @Test
        @DisplayName("Arity önce doğrulanır; --list sonrasındaki fazla argüman yok sayılmaz")
        void rejectsExtraArgumentEvenAfterListFlag() {
            CliResult result = execute("--list", "extra");

            assertEquals(2, result.exitCode());
            assertTrue(result.output().isEmpty());
            assertTrue(result.error().contains("yalnızca bir aile veya pattern"));
        }
    }

    @Nested
    @DisplayName("Geçerli pattern seçimi")
    class ValidSelection {

        @Test
        @DisplayName("Tek pattern seçimi yalnız bir demoyu çalıştırıp özetler")
        void executesOneSelectedPattern() {
            CliResult result = execute("factory-method");

            assertEquals(0, result.exitCode());
            assertTrue(result.output().contains("--- Factory Method [factory-method] ---"));
            assertTrue(result.output().contains("Toplam 1 pattern demosu çalıştırıldı."));
            assertTrue(result.error().isEmpty());
        }

        @Test
        @DisplayName("Türkçe büyük harfli aile adı da geçerli seçimdir")
        void acceptsUppercaseTurkishFamilyName() {
            CliResult result = execute("YAPISAL");

            assertEquals(0, result.exitCode());
            assertTrue(result.output().contains("=== YAPISAL / STRUCTURAL ==="));
            assertTrue(result.output().contains("Toplam 7 pattern demosu çalıştırıldı."));
            assertTrue(result.error().isEmpty());
        }
    }

    private static CliResult execute(String... args) {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();
        int exitCode = Main.run(
                args,
                new PrintStream(outputBytes, true, StandardCharsets.UTF_8),
                new PrintStream(errorBytes, true, StandardCharsets.UTF_8)
        );
        return new CliResult(
                exitCode,
                outputBytes.toString(StandardCharsets.UTF_8),
                errorBytes.toString(StandardCharsets.UTF_8)
        );
    }

    private record CliResult(int exitCode, String output, String error) {
    }
}
