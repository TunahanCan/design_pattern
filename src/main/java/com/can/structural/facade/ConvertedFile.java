package com.can.structural.facade;

public class ConvertedFile {

    private final String outputName;
    private final String payload;

    public ConvertedFile(String outputName, String payload) {
        this.outputName = outputName;
        this.payload = payload;
    }

    public String getOutputName() {
        return outputName;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return outputName + " => " + payload;
    }
}
