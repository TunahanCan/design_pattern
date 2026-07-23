package com.can.structural.bridge;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Bridge | Kumanda soyutlamasını cihaz uygulamalarından ayırma")
class BridgePatternDemoTest {

    @Nested
    @DisplayName("Temel kumanda operasyonları")
    class BasicRemoteOperations {

        @Test
        @DisplayName("Aynı kumanda TV'nin güç, ses ve kanal durumunu yönetmelidir")
        void shouldControlTvWithBasicRemote() {
            // Arrange
            Device tv = new Tv();
            RemoteControl remote = new RemoteControl(tv);

            // Act
            remote.togglePower();
            remote.volumeUp();
            remote.channelUp();

            // Assert
            assertAll(
                () -> assertTrue(tv.isEnabled()),
                () -> assertEquals(40, tv.getVolume()),
                () -> assertEquals(2, tv.getChannel()),
                () -> assertEquals("TV -> power=ON, volume=40, channel=2", tv.status())
            );
        }

        @Test
        @DisplayName("Tekrar toggle edildiğinde cihaz kapanmalı ve durumunu korumalıdır")
        void shouldTogglePowerWithoutChangingOtherDeviceState() {
            // Arrange
            Device tv = new Tv();
            RemoteControl remote = new RemoteControl(tv);

            // Act
            remote.togglePower();
            remote.togglePower();

            // Assert
            assertAll(
                () -> assertFalse(tv.isEnabled()),
                () -> assertEquals(30, tv.getVolume()),
                () -> assertEquals(1, tv.getChannel())
            );
        }
    }

    @Nested
    @DisplayName("Gelişmiş kumanda operasyonları")
    class AdvancedRemoteOperations {

        @Test
        @DisplayName("Mute işlemi Radio açık kalırken sesini sıfırlamalıdır")
        void shouldMuteUsingAdvancedRemote() {
            // Arrange
            Device radio = new Radio();
            AdvancedRemoteControl remote = new AdvancedRemoteControl(radio);

            // Act
            remote.togglePower();
            remote.volumeUp();
            remote.mute();

            // Assert
            assertAll(
                () -> assertTrue(radio.isEnabled()),
                () -> assertEquals(0, radio.getVolume()),
                () -> assertEquals(88, radio.getChannel())
            );
        }

        @Test
        @DisplayName("Preset yalnız kaydedildiği cihaz kimliğinde seçilebilmelidir")
        void shouldScopePresetsToTheCurrentDeviceIdentity() {
            // Arrange
            Device radio = new Radio();
            Device tv = new Tv();
            AdvancedRemoteControl remote = new AdvancedRemoteControl(radio);
            radio.setChannel(101);
            remote.saveChannelPreset("haber");

            // Act
            radio.setChannel(105);
            remote.selectChannelPreset("haber");
            int restoredRadioChannel = radio.getChannel();
            remote.switchDevice(tv);
            tv.setChannel(7);
            IllegalArgumentException crossDeviceSelection = assertThrows(
                IllegalArgumentException.class,
                () -> remote.selectChannelPreset("haber")
            );
            int tvPresetCountBeforeSave = remote.getPresetCount();
            remote.saveChannelPreset("haber");
            tv.setChannel(9);
            remote.selectChannelPreset("haber");

            // Assert
            assertAll(
                () -> assertEquals(101, restoredRadioChannel),
                () -> assertEquals(7, tv.getChannel()),
                () -> assertEquals(0, tvPresetCountBeforeSave),
                () -> assertTrue(
                    crossDeviceSelection.getMessage().contains("Unknown channel preset")
                ),
                () -> assertEquals(1, remote.getPresetCount())
            );
        }

        @Test
        @DisplayName("Aynı concrete tipteki iki cihaz da ayrı preset alanlarına sahip olmalıdır")
        void shouldNotSharePresetsBetweenTwoInstancesOfTheSameDeviceType() {
            // Arrange
            Device livingRoomTv = new Tv();
            Device meetingRoomTv = new Tv();
            AdvancedRemoteControl remote = new AdvancedRemoteControl(livingRoomTv);
            livingRoomTv.setChannel(12);
            remote.saveChannelPreset("sunum");

            // Act
            remote.switchDevice(meetingRoomTv);

            // Assert
            assertAll(
                () -> assertEquals(0, remote.getPresetCount()),
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> remote.selectChannelPreset("sunum")
                )
            );
        }

        @Test
        @DisplayName("Bilinmeyen veya boş preset adı sessizce yok sayılmamalıdır")
        void shouldRejectInvalidPresetRequests() {
            // Arrange
            AdvancedRemoteControl remote = new AdvancedRemoteControl(new Tv());

            // Act & Assert
            assertAll(
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> remote.saveChannelPreset(" ")
                ),
                () -> assertThrows(
                    IllegalArgumentException.class,
                    () -> remote.selectChannelPreset("yok")
                )
            );
        }
    }

    @Nested
    @DisplayName("Köprünün çalışma anında değiştirilmesi")
    class RuntimeDeviceSwitching {

        @Test
        @DisplayName("Kumanda yeni cihaza geçmeli, eski cihazın durumu korunmalıdır")
        void shouldSwitchImplementationAtRuntime() {
            // Arrange
            Device tv = new Tv();
            Device radio = new Radio();
            AdvancedRemoteControl remote = new AdvancedRemoteControl(radio);

            // Act
            remote.togglePower();
            remote.volumeUp();
            remote.switchDevice(tv);
            remote.togglePower();
            remote.channelUp();

            // Assert
            assertAll(
                () -> assertTrue(radio.isEnabled()),
                () -> assertEquals(30, radio.getVolume()),
                () -> assertTrue(tv.isEnabled()),
                () -> assertEquals(2, tv.getChannel())
            );
        }
    }

    @Nested
    @DisplayName("Cihazların sınır kontratları")
    class DeviceBoundaryContracts {

        @Test
        @DisplayName("TV sesi 0-100 aralığında ve kanalı en az 1'de tutulmalıdır")
        void shouldClampTvValuesToItsCurrentDomainLimits() {
            // Arrange
            Device tv = new Tv();

            // Act
            tv.setVolume(-50);
            tv.setChannel(-10);

            // Assert
            assertAll(
                () -> assertEquals(0, tv.getVolume()),
                () -> assertEquals(1, tv.getChannel())
            );

            // Act
            tv.setVolume(500);

            // Assert
            assertEquals(100, tv.getVolume());
        }

        @Test
        @DisplayName("Radio frekansı örnek domain'in 80-108 aralığında tutulmalıdır")
        void shouldClampRadioFrequencyToSupportedBand() {
            // Arrange
            Device radio = new Radio();

            // Act
            radio.setChannel(1);
            int lowerBound = radio.getChannel();
            radio.setChannel(500);
            int upperBound = radio.getChannel();

            // Assert
            assertAll(
                () -> assertEquals(80, lowerBound),
                () -> assertEquals(108, upperBound)
            );
        }

        @Test
        @DisplayName("Bridge null implementor ile kurulamaz veya null'a geçirilemez")
        void shouldRejectMissingImplementor() {
            // Arrange
            RemoteControl remote = new RemoteControl(new Tv());

            // Act & Assert
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> new RemoteControl(null)),
                () -> assertThrows(NullPointerException.class, () -> remote.switchDevice(null))
            );
        }
    }
}
