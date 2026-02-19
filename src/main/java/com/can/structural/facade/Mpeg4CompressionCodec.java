package com.can.structural.facade;

public class Mpeg4CompressionCodec implements CompressionCodec {

    @Override
    public String getType() {
        return "mp4";
    }
}
