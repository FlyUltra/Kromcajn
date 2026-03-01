package com.example.modules;

import com.example.gui.Category;
import com.example.Module;
import com.example.settings.ModeSetting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import java.lang.reflect.Field;

public class FullBright extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Potion", "Potion", "Gamma");

    public FullBright() {
        super("FullBright", Category.RENDER);
        addSettings(mode);
    }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.player == null) return;

        if (mode.value.equals("Potion")) {
            mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1000, 0, false, false, false));
        }
        else if (mode.value.equals("Gamma")) {
            setGammaUnsafe(10.0);

            if (mc.player.hasEffect(MobEffects.NIGHT_VISION)) {
                mc.player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) mc.player.removeEffect(MobEffects.NIGHT_VISION);
        setGammaUnsafe(1.0);
    }

    private void setGammaUnsafe(double value) {
        try {
            Field valueField = mc.options.gamma().getClass().getDeclaredField("value");
            valueField.setAccessible(true);
            valueField.set(mc.options.gamma(), value);
        } catch (Exception e) {
            mc.options.gamma().set(value);
        }
    }
}