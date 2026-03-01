package com.example.gui;

import net.minecraft.client.gui.GuiGraphics;
import java.util.ArrayList;
import java.util.List;

public class ParticleEngine {

    private final int count;
    private final List<BgParticle> particles = new ArrayList<>();
    private int lastWidth, lastHeight;

    public ParticleEngine(int count) {
        this.count = count;
    }

    public void render(GuiGraphics g, int width, int height) {
        // Pokud se změnilo rozlišení nebo nejsou částice, inicializuj
        if (particles.isEmpty() || lastWidth != width || lastHeight != height) {
            init(width, height);
        }

        for (BgParticle p : particles) {
            p.update(width, height);

            int alpha = 120;
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

    private void init(int width, int height) {
        particles.clear();
        this.lastWidth = width;
        this.lastHeight = height;
        for (int i = 0; i < count; i++) {
            particles.add(new BgParticle(width, height));
        }
    }

    private static class BgParticle {
        float x, y, vx, vy, size;

        BgParticle(int width, int height) {
            this.x = (float) (Math.random() * width);
            this.y = (float) (Math.random() * height);
            this.vx = (float) ((Math.random() - 0.5) * 0.4);
            this.vy = (float) ((Math.random() - 0.5) * 0.4);
            this.size = (float) (Math.random() * 1.8 + 0.5);
        }

        void update(int width, int height) {
            x += vx;
            y += vy;

            if (x < 0) x = width;
            if (x > width) x = 0;
            if (y < 0) y = height;
            if (y > height) y = 0;
        }
    }
}