package com.can.structural.facade;

public class BitrateReader {

    private BitrateReader() {
    }

    public static String read(String filename, CompressionCodec codec) {
        return "buffer{" + filename + ":" + codec.getType() + "}";
    }

    public static String convert(String buffer, CompressionCodec codec) {
        return "converted{" + buffer + "->" + codec.getType() + "}";
    }
}
