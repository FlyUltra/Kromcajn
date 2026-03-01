package com.example.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigModel {
    public Map<String, Integer> keyBinds = new HashMap<>();
    public boolean rainbowGUI = true;
    public float clickGuiScale = 1.0f;

    public ConfigModel() {
        keyBinds.put("Killaura", 19);
        keyBinds.put("Fly", 33);
    }
}