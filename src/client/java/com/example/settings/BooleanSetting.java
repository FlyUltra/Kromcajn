package com.example.settings;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public boolean isEnabled() {
        return this.value;
    }

    public void toggle() {
        this.value = !this.value;
    }



}