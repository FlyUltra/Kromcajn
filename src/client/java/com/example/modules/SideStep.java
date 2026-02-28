package com.example.modules;

import com.example.Category;
import com.example.Module;
import com.example.settings.NumberSetting;

public class SideStep extends Module {
    private int timer = 0;
    private boolean direction = false;

    public NumberSetting speed = new NumberSetting("Timer", 1.0, 0.1, 5.0);

    public SideStep() {
        super("SideStep", Category.MOVEMENT);
        addSettings(speed);
    }

    @Override
    public void onTick() {
        if (isEnabled() && mc.player != null) {
            timer++;

            //  (cca 250ms)
            if (timer >= 5) {
                direction = !direction;
                timer = 0;
            }

            if (direction) {
                mc.player.xxa = 1.0f;
            } else {
                mc.player.xxa = -1.0f;
            }
        }
    }
}