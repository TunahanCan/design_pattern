package com.can.structural.facade;

import java.util.Objects;

public class ConvertedFile {

    private final String outputName;
    private final String payload;

    public ConvertedFile(String outputName, String payload) {
        if (outputName == null || outputName.isBlank()) {
            throw new IllegalArgumentException("outputName cannot be blank");
        }
        this.outputName = outputName;
        this.payload = Objects.requireNonNull(payload, "payload cannot be null");
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
