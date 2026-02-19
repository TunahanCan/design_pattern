package com.can.structural.facade;

public class VideoConverterFacade {

    public ConvertedFile convert(String filename, String format) {
        VideoFile file = new VideoFile(filename);
        CompressionCodec sourceCodec = new CodecFactory().extract(file);

        CompressionCodec destinationCodec = "mp4".equalsIgnoreCase(format)
            ? new Mpeg4CompressionCodec()
            : new OggCompressionCodec();

        String buffer = BitrateReader.read(filename, sourceCodec);
        String converted = BitrateReader.convert(buffer, destinationCodec);
        String mixed = new AudioMixer().fix(converted);

        return new ConvertedFile(changeExtension(filename, destinationCodec.getType()), mixed);
    }

    private String changeExtension(String filename, String newExtension) {
        int dotIndex = filename.lastIndexOf('.');
        String baseName = dotIndex >= 0 ? filename.substring(0, dotIndex) : filename;
        return baseName + "." + newExtension;
    }
}
