package com.can.structural.facade;

public class FacadePatternDemo {

    private FacadePatternDemo() {
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
