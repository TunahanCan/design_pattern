package com.can.structural.bridge;

import java.util.Objects;

public class RemoteControl {

    protected Device device;

    public RemoteControl(Device device) {
        this.device = requireDevice(device);
    }

    public void switchDevice(Device device) {
        this.device = requireDevice(device);
    }

    public void togglePower() {
        if (device.isEnabled()) {
            device.disable();
            return;
        }
        device.enable();
    }

    public void volumeDown() {
        device.setVolume(device.getVolume() - 10);
    }

    public void volumeUp() {
        device.setVolume(device.getVolume() + 10);
    }

    public void channelDown() {
        device.setChannel(device.getChannel() - 1);
    }

    public void channelUp() {
        device.setChannel(device.getChannel() + 1);
    }

    private static Device requireDevice(Device device) {
        return Objects.requireNonNull(device, "device cannot be null");
    }
}
