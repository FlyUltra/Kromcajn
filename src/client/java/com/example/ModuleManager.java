package com.example;

import com.example.gui.Category;
import com.example.modules.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new FullBright());
        modules.add(new SideStep());
        modules.add(new Scaffold());
        modules.add(new ESP());
        modules.add(new KillAura());
    }

    public List<Module> getModules() { return modules; }

    public List<Module> getModulesInCategory(Category c) {
        return modules.stream().filter(m -> m.getCategory() == c).collect(Collectors.toList());
    }



    public Module getModuleByName(String name) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}