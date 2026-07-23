package com.can.structural.facade;

import java.util.Locale;

public class VideoFile {

    private final String name;
    private final String extension;

    public VideoFile(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename cannot be blank");
        }
        this.name = filename.trim();
        int dotIndex = name.lastIndexOf('.');
        this.extension = dotIndex >= 0
            ? name.substring(dotIndex + 1).toLowerCase(Locale.ROOT)
            : "";
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }
}
