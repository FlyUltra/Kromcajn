package com.example.settings;

public class ModeSetting extends Setting<String> {
    public String[] modes;
    public int index;
    public ModeSetting(String name, String defaultValue, String... modes) {
        super(name, defaultValue);
        this.modes = modes;
    }
}