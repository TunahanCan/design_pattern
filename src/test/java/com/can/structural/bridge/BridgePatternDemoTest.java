package com.can.structural.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BridgePatternDemoTest {

    @Test
    void shouldControlTvWithBasicRemote() {
        Device tv = new Tv();
        RemoteControl remote = new RemoteControl(tv);

        remote.togglePower();
        remote.volumeUp();
        remote.channelUp();

        assertTrue(tv.isEnabled());
        assertEquals(40, tv.getVolume());
        assertEquals(2, tv.getChannel());
    }

    @Test
    void shouldMuteUsingAdvancedRemote() {
        Device radio = new Radio();
        AdvancedRemoteControl remote = new AdvancedRemoteControl(radio);

        remote.togglePower();
        remote.volumeUp();
        remote.mute();

        assertTrue(radio.isEnabled());
        assertEquals(0, radio.getVolume());
    }

    @Test
    void shouldSwitchImplementationAtRuntime() {
        Device tv = new Tv();
        Device radio = new Radio();

        AdvancedRemoteControl remote = new AdvancedRemoteControl(radio);

        remote.togglePower();
        assertTrue(radio.isEnabled());
        assertFalse(tv.isEnabled());

        remote.switchDevice(tv);
        remote.togglePower();

        assertTrue(tv.isEnabled());
    }
}
