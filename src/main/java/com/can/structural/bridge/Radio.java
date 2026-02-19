package com.can.structural.bridge;

public class Radio implements Device {

    private boolean enabled;
    private int volume = 20;
    private int channel = 88;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int percent) {
        volume = Math.max(0, Math.min(100, percent));
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public void setChannel(int channel) {
        this.channel = Math.max(80, channel);
    }

    @Override
    public String status() {
        return "RADIO -> power=" + (enabled ? "ON" : "OFF")
                + ", volume=" + volume
                + ", frequency=" + channel;
    }
}
