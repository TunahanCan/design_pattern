package com.can.structural.facade;

import java.util.Objects;

public class VideoConverterFacade {

    private final CodecFactory codecFactory;
    private final AudioMixer audioMixer;

    public VideoConverterFacade() {
        this(new CodecFactory(), new AudioMixer());
    }

    public VideoConverterFacade(CodecFactory codecFactory, AudioMixer audioMixer) {
        this.codecFactory = Objects.requireNonNull(codecFactory, "codecFactory cannot be null");
        this.audioMixer = Objects.requireNonNull(audioMixer, "audioMixer cannot be null");
    }

    public ConvertedFile convert(String filename, String format) {
        return convertTo(filename, VideoFormat.from(format));
    }

    public ConvertedFile convertTo(String filename, VideoFormat format) {
        VideoFormat destinationFormat = Objects.requireNonNull(
            format,
            "format cannot be null"
        );
        VideoFile file = new VideoFile(filename);
        CompressionCodec sourceCodec = codecFactory.extract(file);
        CompressionCodec destinationCodec = codecFactory.create(destinationFormat);

        String buffer = BitrateReader.read(file.getName(), sourceCodec);
        String converted = BitrateReader.convert(buffer, destinationCodec);
        String mixed = audioMixer.fix(converted);

        return new ConvertedFile(
            file.replaceExtensionWith(destinationFormat),
            mixed
        );
    }
}
