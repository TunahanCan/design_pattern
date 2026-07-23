package com.can.structural.facade;

public class CodecFactory {

    public CompressionCodec extract(VideoFile file) {
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        return create(VideoFormat.from(file.getExtension()));
    }

    public CompressionCodec create(VideoFormat format) {
        if (format == null) {
            throw new NullPointerException("format cannot be null");
        }
        return switch (format) {
            case MP4 -> new Mpeg4CompressionCodec();
            case OGG -> new OggCompressionCodec();
        };
    }
}
