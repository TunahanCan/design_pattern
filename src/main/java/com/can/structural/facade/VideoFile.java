package com.can.structural.facade;

import java.util.Locale;
import java.util.Objects;

public class VideoFile {

    private final String name;
    private final String extension;
    private final int replacementBoundaryIndex;

    public VideoFile(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename cannot be blank");
        }
        this.name = filename.trim();
        int fileNameStartIndex = Math.max(
            name.lastIndexOf('/'),
            name.lastIndexOf('\\')
        ) + 1;
        int dotIndex = name.lastIndexOf('.');
        boolean hasExtension = dotIndex > fileNameStartIndex
            && dotIndex < name.length() - 1;
        boolean hasTrailingDot = dotIndex > fileNameStartIndex
            && dotIndex == name.length() - 1;
        this.replacementBoundaryIndex = hasExtension || hasTrailingDot
            ? dotIndex
            : -1;
        this.extension = hasExtension
            ? name.substring(dotIndex + 1).toLowerCase(Locale.ROOT)
            : "";
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String replaceExtensionWith(VideoFormat format) {
        VideoFormat destinationFormat = Objects.requireNonNull(
            format,
            "format cannot be null"
        );
        String baseName = replacementBoundaryIndex >= 0
            ? name.substring(0, replacementBoundaryIndex)
            : name;
        return baseName + "." + destinationFormat.extension();
    }
}
