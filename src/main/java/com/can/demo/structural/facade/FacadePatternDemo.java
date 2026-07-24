package com.can.demo.structural.facade;

import com.can.structural.facade.ConvertedFile;
import com.can.structural.facade.VideoConverterFacade;

/**
 * Executable composition root for the Facade example.
 *
 * <p>The facade and subsystem remain in {@code com.can.structural.facade};
 * this class only drives their common learning scenario.</p>
 */
public final class FacadePatternDemo {

    private FacadePatternDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("=== Facade Pattern ===");

        VideoConverterFacade converter = new VideoConverterFacade();
        ConvertedFile converted = converter.convert("funny-cats-video.ogg", "mp4");

        System.out.println("Dönüştürülen dosya: " + converted.getOutputName());
        System.out.println("İşlem özeti: " + converted.getPayload());
        System.out.println();
    }
}
