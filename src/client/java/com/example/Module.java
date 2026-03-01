package com.example;

import com.example.gui.Category;
import com.example.settings.Setting;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW; // Potřebujeme pro definici kláves

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private String name;
    private Category category;
    private boolean enabled;
    private int key; // --- NOVÉ: Proměnná pro keybind ---

    public List<Setting<?>> settings = new ArrayList<>();
    protected Minecraft mc = Minecraft.getInstance();
    public String description = "Žádný popis nebyl nastaven.";

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
        this.key = GLFW.GLFW_FALSE;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
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