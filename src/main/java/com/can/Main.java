package com.can;

import com.can.catalog.PatternCatalog;
import com.can.catalog.PatternExample;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

/**
 * Komut satırı ile pattern kataloğu arasındaki ince composition root.
 */
public class Main {

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /**
     * Argümanları işler ve process'ten bağımsız biçimde CLI exit kodunu döndürür.
     *
     * <p>Başarılı çağrılar {@code 0}, kullanıcı kaynaklı argüman hataları {@code 2}
     * döndürür. Seçilmiş demonun kendi hatası burada yakalanmaz; böylece domain
     * hataları yanlışlıkla selector hatası gibi gösterilmez.</p>
     */
    public static int run(String[] args, PrintStream output, PrintStream error) {
        Objects.requireNonNull(args, "args boş olamaz");
        Objects.requireNonNull(output, "output boş olamaz");
        Objects.requireNonNull(error, "error boş olamaz");

        if (args.length > 1) {
            error.println("Aynı anda yalnızca bir aile veya pattern seçilebilir.");
            PatternCatalog.printCatalog(error);
            return 2;
        }

        String selection = args.length == 0 ? "all" : args[0];
        if ("--list".equals(selection) || "--help".equals(selection)) {
            PatternCatalog.printCatalog(output);
            return 0;
        }

        List<PatternExample> selected;
        try {
            selected = PatternCatalog.select(selection);
        } catch (IllegalArgumentException exception) {
            error.println(exception.getMessage());
            PatternCatalog.printCatalog(error);
            return 2;
        }

        int executed = PatternCatalog.run(selected, output);
        output.printf("Toplam %d pattern demosu çalıştırıldı.%n", executed);
        return 0;
    }
}
