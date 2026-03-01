package com.example.modules;

import com.example.Module;
import com.example.gui.Category;
import com.example.settings.BooleanSetting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.Color;

public class ESP extends Module {

    public BooleanSetting players = new BooleanSetting("Players", true);
    public BooleanSetting monsters = new BooleanSetting("Monsters", false);
    public BooleanSetting animals = new BooleanSetting("Animals", false);
    public BooleanSetting showHealth = new BooleanSetting("Show Health", true);

    public ESP() {
        super("ESP", Category.RENDER);
        addSettings(players, monsters, animals, showHealth);

        WorldRenderEvents.END_MAIN.register(context -> {
            if (this.isEnabled()) {
                PoseStack poseStack = context.matrices();
                float partialTick = 1.0f;

                mc.renderBuffers().bufferSource().endBatch();

                com.mojang.blaze3d.pipeline.RenderTarget target = mc.getMainRenderTarget();
                com.mojang.blaze3d.systems.RenderSystem.getDevice()
                        .createCommandEncoder()
                        .clearDepthTexture(target.getDepthTexture(), 1.0);

                this.onRenderWorld(poseStack, partialTick);

                mc.renderBuffers().bufferSource().endBatch();
            }
        });
    }

    private void onRenderWorld(PoseStack poseStack, float partialTick) {
        if (mc.level == null || mc.gameRenderer.getMainCamera() == null) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.position();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player || !shouldRender(entity)) continue;

            double x = Mth.lerp(partialTick, entity.xOld, entity.getX()) - cameraPos.x;
            double y = Mth.lerp(partialTick, entity.yOld, entity.getY()) - cameraPos.y;
            double z = Mth.lerp(partialTick, entity.zOld, entity.getZ()) - cameraPos.z;

            poseStack.pushPose();
            poseStack.translate(x, y, z);

            AABB bb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
            Color color = getEntityColor(entity);

            renderBox(poseStack, bb, color);

            if (showHealth.value && entity instanceof LivingEntity living) {
                renderHealthLabel(poseStack, living, (float) bb.maxY + 0.5f, camera);
            }

            poseStack.popPose();
        }
    }

    private void renderBox(PoseStack poseStack, AABB bb, Color color) {
        var bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.lines());

        net.minecraft.world.phys.shapes.VoxelShape shape = net.minecraft.world.phys.shapes.Shapes.create(bb);

        int colorArgb = net.minecraft.util.ARGB.colorFromFloat(
                1.0f,
                color.getRed() / 255f,
                color.getGreen() / 255f,
                color.getBlue() / 255f
        );

        net.minecraft.client.renderer.ShapeRenderer.renderShape(
                poseStack,
                consumer,
                shape,
                0.0, 0.0, 0.0,
                colorArgb,
                1.0f
        );
    }

    private void renderHealthLabel(PoseStack poseStack, LivingEntity living, float yOffset, Camera camera) {
        String healthText = String.format("%.1f HP", living.getHealth());

        poseStack.pushPose();
        poseStack.translate(0, yOffset, 0);

        poseStack.mulPose(camera.rotation());
        poseStack.scale(-0.025f, -0.025f, 0.025f);

        Matrix4f matrix = poseStack.last().pose();
        float xOffset = (float)(-mc.font.width(healthText) / 2);

        mc.font.drawInBatch(healthText, xOffset, 0, 0xFF55FF55, false, matrix,
                mc.renderBuffers().bufferSource(), net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 15728880);

        poseStack.popPose();
    }

    private boolean shouldRender(Entity entity) {
        if (entity instanceof Player) return players.value;
        if (entity instanceof Monster) return monsters.value;
        if (entity instanceof Animal) return animals.value;
        return false;
    }

    private Color getEntityColor(Entity entity) {
        if (entity instanceof Player) return Color.RED;
        if (entity instanceof Monster) return Color.ORANGE;
        if (entity instanceof Animal) return Color.GREEN;
        return Color.WHITE;
    }
}