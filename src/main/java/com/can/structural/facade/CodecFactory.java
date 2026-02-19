package com.can.structural.facade;

public class CodecFactory {

    public CompressionCodec extract(VideoFile file) {
        if ("mp4".equals(file.getExtension())) {
            return new Mpeg4CompressionCodec();
        }
        return new OggCompressionCodec();
    }
}
