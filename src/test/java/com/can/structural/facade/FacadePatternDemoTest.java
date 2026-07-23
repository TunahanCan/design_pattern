package com.can.structural.facade;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Facade | Video dönüştürme alt sistemine sade bir giriş sunma")
class FacadePatternDemoTest {

    @Nested
    @DisplayName("Kaynak dosya bilgisinin çözümlenmesi")
    class SourceFileMetadata {

        @Test
        @DisplayName("VideoFile adı korumalı ve uzantıyı küçük harfe çevirmelidir")
        void shouldExtractNormalizedExtensionFromLastDot() {
            // Arrange
            VideoFile videoFile = new VideoFile("archive.final.OGG");

            // Act
            String name = videoFile.getName();
            String extension = videoFile.getExtension();

            // Assert
            assertAll(
                () -> assertEquals("archive.final.OGG", name),
                () -> assertEquals("ogg", extension)
            );
        }
    }

    @Nested
    @DisplayName("Facade dönüştürme akışı")
    class ConversionWorkflow {

        @Test
        @DisplayName("OGG kaynak MP4 hedefe bütün alt sistem adımlarıyla dönüştürülmelidir")
        void shouldConvertOggToMp4ViaFacade() {
            // Arrange
            VideoConverterFacade converter = new VideoConverterFacade();
            String expectedPayload =
                "converted{buffer{funny-cats-video.ogg:ogg}->mp4}|audio-fixed";

            // Act
            ConvertedFile output = converter.convert("funny-cats-video.ogg", "mp4");

            // Assert
            assertAll(
                () -> assertEquals("funny-cats-video.mp4", output.getOutputName()),
                () -> assertEquals(expectedPayload, output.getPayload()),
                () -> assertEquals(
                    "funny-cats-video.mp4 => " + expectedPayload,
                    output.toString()
                )
            );
        }

        @Test
        @DisplayName("MP4 kaynak OGG hedefe dönüştürülmelidir")
        void shouldConvertMp4ToOggViaFacade() {
            // Arrange
            VideoConverterFacade converter = new VideoConverterFacade();

            // Act
            ConvertedFile output = converter.convert("podcast.mp4", "ogg");

            // Assert
            assertAll(
                () -> assertEquals("podcast.ogg", output.getOutputName()),
                () -> assertEquals(
                    "converted{buffer{podcast.mp4:mp4}->ogg}|audio-fixed",
                    output.getPayload()
                )
            );
        }

        @Test
        @DisplayName("Hedef format karşılaştırması büyük-küçük harfe duyarsız olmalıdır")
        void shouldRecognizeUppercaseMp4Target() {
            // Arrange
            VideoConverterFacade converter = new VideoConverterFacade();

            // Act
            ConvertedFile output = converter.convert("clip.ogg", "MP4");

            // Assert
            assertAll(
                () -> assertEquals("clip.mp4", output.getOutputName()),
                () -> assertEquals(
                    "converted{buffer{clip.ogg:ogg}->mp4}|audio-fixed",
                    output.getPayload()
                )
            );
        }

        @Test
        @DisplayName("Açık isimli typed operasyon aynı facade workflow'unu kullanmalıdır")
        void shouldSupportTypedFormatOperation() {
            // Arrange
            VideoConverterFacade converter = new VideoConverterFacade();

            // Act
            ConvertedFile output = converter.convertTo("clip.ogg", VideoFormat.MP4);

            // Assert
            assertEquals("clip.mp4", output.getOutputName());
        }
    }

    @Nested
    @DisplayName("Alt sistem işbirlikçileri")
    class SubsystemCollaborators {

        @Test
        @DisplayName("CodecFactory bilinen kaynak uzantısına uygun codec üretmelidir")
        void shouldSelectCodecFromSourceExtension() {
            // Arrange
            CodecFactory factory = new CodecFactory();

            // Act
            CompressionCodec mp4 = factory.extract(new VideoFile("clip.mp4"));
            CompressionCodec ogg = factory.extract(new VideoFile("sound.ogg"));

            // Assert
            assertAll(
                () -> assertEquals("mp4", mp4.getType()),
                () -> assertEquals("ogg", ogg.getType())
            );
        }
    }

    @Nested
    @DisplayName("Facade sınır ve hata politikası")
    class BoundaryPolicy {

        @Test
        @DisplayName("Bilinmeyen kaynak veya hedef format sessizce OGG kabul edilmemelidir")
        void shouldRejectUnsupportedFormats() {
            // Arrange
            VideoConverterFacade converter = new VideoConverterFacade();

            // Act & Assert
            assertAll(
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> converter.convert("clip.avi", "mp4")
                ),
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> converter.convert("clip.mp4", "webm")
                ),
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> converter.convert("clip.mp4", null)
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> converter.convertTo("clip.mp4", null)
                )
            );
        }

        @Test
        @DisplayName("Eksik dosya adı ve bağımlılıklar facade kurulurken erken reddedilmelidir")
        void shouldRejectMissingInputAndCollaborators() {
            assertAll(
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> new VideoConverterFacade().convert(" ", "mp4")
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> new VideoConverterFacade(null, new AudioMixer())
                ),
                () -> assertThrows(
                    NullPointerException.class,
                    () -> new VideoConverterFacade(new CodecFactory(), null)
                )
            );
        }
    }
}
