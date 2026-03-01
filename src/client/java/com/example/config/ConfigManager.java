package com.example.config;

import com.example.ModuleManager;
import com.example.settings.ModeSetting;
import com.example.settings.NumberSetting;
import com.example.settings.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import com.example.Module;

import java.io.*;

public class ConfigManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final File folder = new File(Minecraft.getInstance().gameDirectory, "kromcajn");
    private final File configFile = new File(folder, "config.json");

    public void save() {
        if (!folder.exists()) folder.mkdirs();

        JsonObject json = new JsonObject();

        for (Module m : ModuleManager.INSTANCE.getModules()) {
            JsonObject moduleJson = new JsonObject();

            moduleJson.addProperty("enabled", m.isEnabled());
            moduleJson.addProperty("key", m.getKey());

            if (!m.getSettings().isEmpty()) {
                JsonObject settingsJson = new JsonObject();
                for (Setting<?> s : m.getSettings()) {
                    if (s instanceof NumberSetting) {
                        settingsJson.addProperty(s.name, (Double) s.value);
                    } else if (s instanceof ModeSetting) {
                        settingsJson.addProperty(s.name, (String) s.value);
                    }
                }
                moduleJson.add("settings", settingsJson);
            }

            json.add(m.getName(), moduleJson);
        }

        try (Writer writer = new FileWriter(configFile)) {
            gson.toJson(json, writer);
        } catch (IOException e) {
            System.err.println("Nepodařilo se uložit kromcajn config!");
        }
    }

    public void load() {
        if (!configFile.exists()) return;

        try (Reader reader = new FileReader(configFile)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            for (Module m : ModuleManager.INSTANCE.getModules()) {
                if (json.has(m.getName())) {
                    JsonObject moduleJson = json.getAsJsonObject(m.getName());

                    if (moduleJson.has("enabled")) m.setEnabled(moduleJson.get("enabled").getAsBoolean());

                    if (moduleJson.has("key")) m.setKey(moduleJson.get("key").getAsInt());

                    if (moduleJson.has("settings")) {
                        JsonObject settingsJson = moduleJson.getAsJsonObject("settings");
                        for (Setting<?> s : m.getSettings()) {
                            if (settingsJson.has(s.name)) {
                                if (s instanceof NumberSetting) {
                                    ((NumberSetting) s).value = settingsJson.get(s.name).getAsDouble();
                                } else if (s instanceof ModeSetting) {
                                    String val = settingsJson.get(s.name).getAsString();
                                    ((ModeSetting) s).value = val;
                                    updateModeIndex((ModeSetting) s, val);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Nepodařilo se načíst kromcajn config!");
        }
    }

    private void updateModeIndex(ModeSetting s, String val) {
        for (int i = 0; i < s.modes.length; i++) {
            if (s.modes[i].equalsIgnoreCase(val)) {
                s.index = i;
                break;
            }
        }
    }
}