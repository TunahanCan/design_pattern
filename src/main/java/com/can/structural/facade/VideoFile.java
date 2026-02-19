package com.can.structural.facade;

public class VideoFile {

    private final String name;
    private final String extension;

    public VideoFile(String filename) {
        this.name = filename;
        int dotIndex = filename.lastIndexOf('.');
        this.extension = dotIndex >= 0 ? filename.substring(dotIndex + 1).toLowerCase() : "";
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }
}
