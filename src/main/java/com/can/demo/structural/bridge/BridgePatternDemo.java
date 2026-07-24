package com.can.demo.structural.bridge;

import com.can.structural.bridge.AdvancedRemoteControl;
import com.can.structural.bridge.Device;
import com.can.structural.bridge.Radio;
import com.can.structural.bridge.RemoteControl;
import com.can.structural.bridge.Tv;

/**
 * Executable composition root for the Bridge example.
 *
 * <p>Reusable abstractions and implementations remain in
 * {@code com.can.structural.bridge}.</p>
 */
public final class BridgePatternDemo {

    private BridgePatternDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("2) Bridge");

        Device tv = new Tv();
        RemoteControl remote = new RemoteControl(tv);

        remote.togglePower();
        remote.volumeUp();
        remote.channelUp();

        System.out.println("Basic remote + TV -> " + tv.status());

        Device radio = new Radio();
        AdvancedRemoteControl advancedRemote = new AdvancedRemoteControl(radio);

        advancedRemote.togglePower();
        advancedRemote.volumeUp();
        advancedRemote.saveChannelPreset("favori");
        advancedRemote.channelUp();
        advancedRemote.selectChannelPreset("favori");
        advancedRemote.mute();

        System.out.println(
            "Advanced remote + Radio (preset workflow) -> " + radio.status()
        );

        advancedRemote.switchDevice(tv);
        advancedRemote.channelUp();

        System.out.println("Advanced remote switched to TV -> " + tv.status());
        System.out.println();
    }
}
