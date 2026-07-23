package com.can.structural.bridge;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancedRemoteControl extends RemoteControl {

    private final Map<Device, Map<String, Integer>> channelPresetsByDevice =
        new IdentityHashMap<>();

    public AdvancedRemoteControl(Device device) {
        super(device);
    }

    public void mute() {
        device.setVolume(0);
    }

    public void saveChannelPreset(String name) {
        presetsForCurrentDevice(true).put(
            requirePresetName(name),
            device.getChannel()
        );
    }

    public void selectChannelPreset(String name) {
        String presetName = requirePresetName(name);
        Integer channel = presetsForCurrentDevice(false).get(presetName);
        if (channel == null) {
            throw new IllegalArgumentException("Unknown channel preset: " + presetName);
        }
        device.setChannel(channel);
    }

    public int getPresetCount() {
        return presetsForCurrentDevice(false).size();
    }

    private Map<String, Integer> presetsForCurrentDevice(boolean createWhenMissing) {
        Map<String, Integer> presets = channelPresetsByDevice.get(device);
        if (presets != null || !createWhenMissing) {
            return presets == null ? Map.of() : presets;
        }

        Map<String, Integer> newPresets = new LinkedHashMap<>();
        channelPresetsByDevice.put(device, newPresets);
        return newPresets;
    }

    private static String requirePresetName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("preset name cannot be blank");
        }
        return name.trim();
    }
}
