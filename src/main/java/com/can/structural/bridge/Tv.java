package com.can.structural.bridge;

public class Tv implements Device {

    private boolean enabled;
    private int volume = 30;
    private int channel = 1;

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
        this.channel = Math.max(1, channel);
    }

    @Override
    public String status() {
        return "TV -> power=" + (enabled ? "ON" : "OFF")
                + ", volume=" + volume
                + ", channel=" + channel;
    }
}
