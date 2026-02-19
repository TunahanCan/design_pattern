package com.can.structural.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FacadePatternDemoTest {

    @Test
    void shouldConvertOggToMp4ViaFacade() {
        VideoConverterFacade converter = new VideoConverterFacade();

        ConvertedFile output = converter.convert("funny-cats-video.ogg", "mp4");

        assertEquals("funny-cats-video.mp4", output.getOutputName());
        assertTrue(output.getPayload().contains("audio-fixed"));
        assertTrue(output.getPayload().contains("->mp4"));
    }

    @Test
    void shouldConvertMp4ToOggViaFacade() {
        VideoConverterFacade converter = new VideoConverterFacade();

        ConvertedFile output = converter.convert("podcast.mp4", "ogg");

        assertEquals("podcast.ogg", output.getOutputName());
        assertTrue(output.getPayload().contains("->ogg"));
    }
}
