package com.example.settings;

public abstract class Setting<T> {
    public String name;
    public T value;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }
}