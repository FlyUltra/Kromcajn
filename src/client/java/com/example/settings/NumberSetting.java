package com.example.settings;

public class NumberSetting extends Setting<Double> {
    public double min, max;
    public NumberSetting(String name, double defaultValue, double min, double max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
    }
}