package com.can.catalog;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * GoF desenlerini, kod tabanındaki paket adlarından bağımsız bir üst başlıkta toplar.
 */
public enum PatternFamily {

    CREATIONAL("creational", "Oluşturucu", "Nesne üretim kararları"),
    STRUCTURAL("structural", "Yapısal", "Nesnelerin bağlanma ve görünüm kararları"),
    BEHAVIORAL("behavioral", "Davranışsal", "Sorumluluk ve iş birliği kararları");

    private final String slug;
    private final String turkishName;
    private final String purpose;
    private static final Locale TURKISH = Locale.forLanguageTag("tr");

    PatternFamily(String slug, String turkishName, String purpose) {
        this.slug = slug;
        this.turkishName = turkishName;
        this.purpose = purpose;
    }

    public String slug() {
        return slug;
    }

    public String turkishName() {
        return turkishName;
    }

    public String purpose() {
        return purpose;
    }

    public static Optional<PatternFamily> from(String value) {
        if (value == null) {
            return Optional.empty();
        }

        String stripped = value.strip();
        String normalizedSlug = stripped.toLowerCase(Locale.ROOT);
        String normalizedTurkishName = stripped.toLowerCase(TURKISH);
        return Arrays.stream(values())
                .filter(family -> family.slug.equals(normalizedSlug)
                        || family.turkishName.toLowerCase(TURKISH).equals(normalizedTurkishName))
                .findFirst();
    }
}
