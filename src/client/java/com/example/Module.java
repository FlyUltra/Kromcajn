package com.example;

import com.example.settings.Setting;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private String name;
    private Category category;
    private boolean enabled;
    public List<Setting<?>> settings = new ArrayList<>();
    protected Minecraft mc = Minecraft.getInstance();
    public String description = "Žádný popis nebyl nastaven.";

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}

    public void addSettings(Setting<?>... s) {
        this.settings.addAll(java.util.Arrays.asList(s));
    }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting<?>> settings) {
        this.settings = settings;
    }
}