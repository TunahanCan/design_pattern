package com.can.structural.bridge;

public class BridgePatternDemo {

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
        advancedRemote.mute();

        System.out.println("Advanced remote + Radio -> " + radio.status());

        advancedRemote.switchDevice(tv);
        advancedRemote.channelUp();

        System.out.println("Advanced remote switched to TV -> " + tv.status());
        System.out.println();
    }
}
