package com.can.catalog;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Bir desenin okunabilir kimliği ile çalıştırılabilir demosunu aynı katalog kaydında tutar.
 */
public record PatternExample(
        String slug,
        String displayName,
        PatternFamily family,
        Runnable demo
) {

    private static final Pattern VALID_SLUG = Pattern.compile("[a-z0-9]+(?:-[a-z0-9]+)*");

    public PatternExample {
        slug = requireText(slug, "slug");
        displayName = requireText(displayName, "displayName");
        family = Objects.requireNonNull(family, "family boş olamaz");
        demo = Objects.requireNonNull(demo, "demo boş olamaz");

        if (!VALID_SLUG.matcher(slug).matches()) {
            throw new IllegalArgumentException(
                    "slug yalnızca küçük harf, rakam ve tekli tire grupları içermelidir: " + slug
            );
        }
    }

    public void execute() {
        demo.run();
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " boş olamaz");
        String normalized = value.strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " boş olamaz");
        }
        return normalized;
    }
}
