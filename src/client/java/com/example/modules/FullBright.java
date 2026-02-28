package com.example.modules;

import com.example.Category;
import com.example.Module;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FullBright extends Module {

    public FullBright() {
        super("FullBright", Category.RENDER);
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    @Override
    public void onTick() {
        if (isEnabled() && mc.player != null) {

            mc.player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION,
                    1000,
                    0,
                    false,
                    false,
                    false
            ));
        }
    }
}