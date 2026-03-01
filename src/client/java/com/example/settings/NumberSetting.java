package com.example.settings;

public class NumberSetting extends Setting<Double> {
    public double min, max;
    public double defaultValue;
    public NumberSetting(String name, double defaultValue, double min, double max) {
        super(name, defaultValue);
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}