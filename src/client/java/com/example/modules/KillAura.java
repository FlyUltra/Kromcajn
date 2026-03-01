package com.example.modules;

import com.example.Module;
import com.example.gui.Category;
import com.example.settings.NumberSetting;
import com.example.settings.BooleanSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;

import java.util.Comparator;
import java.util.stream.StreamSupport;

public class KillAura extends Module {

    public NumberSetting range = new NumberSetting("Range", 3.0, 6.0, 3.8);
    public BooleanSetting attackPlayers = new BooleanSetting("Players", true);
    public BooleanSetting attackMonsters = new BooleanSetting("Monsters", false);
    public BooleanSetting attackAnimals = new BooleanSetting("Animals", false);

    public KillAura() {
        super("KillAura", Category.COMBAT);
        addSettings(range, attackPlayers, attackMonsters, attackAnimals);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;

        Entity target = getNearestTarget();
        if (target == null) return;

        rotateToEntity(target);

        if (mc.player.getAttackStrengthScale(0.5f) >= 1.0f) {
            attack(target);
        }
    }

    private Entity getNearestTarget() {
        double currentRange = range.getDefaultValue();

        return StreamSupport.stream(mc.level.entitiesForRendering().spliterator(), false)
                .filter(e -> e instanceof LivingEntity && e != mc.player && !e.isRemoved())
                .filter(e -> ((LivingEntity) e).getHealth() > 0)
                .filter(e -> {
                    if (e instanceof Player) return attackPlayers.isEnabled();

                    if (e instanceof net.minecraft.world.entity.monster.Enemy) return attackMonsters.isEnabled();

                    if (e instanceof Animal) return attackAnimals.isEnabled();

                    return false;
                })
                .filter(e -> mc.player.distanceTo(e) <= currentRange)
                .sorted(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                .findFirst()
                .orElse(null);
    }

    private void rotateToEntity(Entity target) {
        double dx = target.getX() - mc.player.getX();
        double dy = (target.getY() + target.getEyeHeight() * 0.8) - (mc.player.getY() + mc.player.getEyeHeight());
        double dz = target.getZ() - mc.player.getZ();

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0f);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, distanceXZ)));

        mc.player.setYRot(yaw);
        mc.player.setXRot(Mth.clamp(pitch, -90.0f, 90.0f));
        mc.player.yHeadRot = yaw;
        mc.player.yBodyRot = yaw;
    }

    private void attack(Entity target) {
        if (mc.gameMode != null) {
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }
}