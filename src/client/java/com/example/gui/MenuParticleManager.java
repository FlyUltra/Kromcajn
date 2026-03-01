package com.example.gui;

import net.minecraft.client.gui.GuiGraphics;

public class MenuParticleManager {

    private static final int COUNT = 70;
    private static final BgParticle[] particles = new BgParticle[COUNT];
    private static boolean init = false;
    private static float fade = 0f;

    private static class BgParticle {
        float x, y, vx, vy, size;
    }

    public static void render(GuiGraphics g, int width, int height) {
        init(width, height);

        fade = Math.min(1f, fade + 0.05f);

        for (BgParticle p : particles) {
            p.x += p.vx;
            p.y += p.vy;

            if (p.x < 0) p.x = width;
            if (p.x > width) p.x = 0;
            if (p.y < 0) p.y = height;
            if (p.y > height) p.y = 0;

            int alpha = (int) (120 * fade);
            int color = (alpha << 24) | 0xFFFFFF;

            g.fill(
                    (int) p.x,
                    (int) p.y,
                    (int) (p.x + p.size),
                    (int) (p.y + p.size),
                    color
            );
        }
    }

    private static void init(int width, int height) {
        if (init) return;
        init = true;

        for (int i = 0; i < particles.length; i++) {
            BgParticle p = new BgParticle();
            p.x = (float) (Math.random() * width);
            p.y = (float) (Math.random() * height);
            p.vx = (float) ((Math.random() - 0.5) * 0.4);
            p.vy = (float) ((Math.random() - 0.5) * 0.4);
            p.size = (float) (Math.random() * 1.8 + 0.5);
            particles[i] = p;
        }
    }

    public static void reset() {
        init = false;
        fade = 0f;
    }
}