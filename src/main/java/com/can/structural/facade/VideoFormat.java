package com.can.structural.facade;

import java.util.Locale;

public enum VideoFormat {
    MP4,
    OGG;

    public String extension() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static VideoFormat from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("video format cannot be blank");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported video format: " + value, exception);
        }
    }
}
