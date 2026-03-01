package com.example;

import com.example.settings.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

import java.util.List;


public class ClickGuiScreen extends Screen {

    // --- Původní design tokeny zůstávají ---
    private static final int PANEL_BG = 0xCC111315;
    private static final int PANEL_BORDER = 0xFF2A2D31;
    private static final int ACCENT = 0xFF3B82F6;
    private static final int TEXT_PRIMARY = 0xFFE5E7EB;
    private static final int TEXT_MUTED = 0xFF9CA3AF;
    private static final int ENABLED_BG = 0xFF1F2937;
    private static final int ENABLED_ACCENT = 0xFF22C55E;
    private static final int HOVER_BG = 0x332A2D31;

    private static final int PANEL_WIDTH = 160;
    private static final int HEADER_H = 18;
    private static final int MODULE_H = 16;
    private static final int SETTING_H = 14;

    private static final int START_Y = 30;
    private static final int START_X = 30;
    private static final int SPACING_X = 190;

    private Module expandedModule = null;

    private static final int PARTICLE_COUNT = 70;
    private final BgParticle[] particles = new BgParticle[PARTICLE_COUNT];
    private boolean particlesInit = false;

    private static class BgParticle {
        float x, y;
        float vx, vy;
        float size;
    }

    @Override
    protected void init() {
        super.init();

        // reset particles při otevření GUI
        particlesInit = false;
    }

    @Override
    public void removed() {
        super.removed();

        // optional clean reset
        particlesInit = false;
    }

    private void initParticles() {
        if (particlesInit) return;
        particlesInit = true;

        for (int i = 0; i < particles.length; i++) {
            BgParticle p = new BgParticle();
            p.x = (float) (Math.random() * this.width);
            p.y = (float) (Math.random() * this.height);
            p.vx = (float) ((Math.random() - 0.5) * 0.4);
            p.vy = (float) ((Math.random() - 0.5) * 0.4);
            p.size = (float) (Math.random() * 1.8 + 0.5);
            particles[i] = p;
        }
    }

    private void renderParticles(GuiGraphics g) {
        initParticles();

        for (BgParticle p : particles) {
            // pohyb
            p.x += p.vx;
            p.y += p.vy;

            // wrap screen
            if (p.x < 0) p.x = this.width;
            if (p.x > this.width) p.x = 0;
            if (p.y < 0) p.y = this.height;
            if (p.y > this.height) p.y = 0;

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

    public ClickGuiScreen() {
        super(Component.literal("Kromcajn"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0x88000000);
        renderParticles(g);
        drawCategory(g, Category.RENDER, START_X, START_Y, mouseX, mouseY);
        drawCategory(g, Category.COMBAT, START_X + SPACING_X, START_Y, mouseX, mouseY);
        drawCategory(g, Category.MOVEMENT, START_X + SPACING_X * 2, START_Y, mouseX, mouseY);

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void drawCategory(GuiGraphics g, Category category, int x, int y, int mouseX, int mouseY) {
        List<Module> modules = ModuleManager.INSTANCE.getModulesInCategory(category);

        int totalHeight = HEADER_H + 8;
        for (Module m : modules) {
            totalHeight += MODULE_H;
            if (expandedModule == m) {
                totalHeight += m.settings.size() * SETTING_H;
            }
        }

        g.fill(x, y, x + PANEL_WIDTH, y + totalHeight, PANEL_BG);
        renderBorder(g, x, y, PANEL_WIDTH, totalHeight);
        g.fill(x, y, x + PANEL_WIDTH, y + 2, ACCENT);
        g.drawString(
                this.font,
                kromcajnText(category.name(), TEXT_PRIMARY),
                x + 8,
                y + 5,
                TEXT_PRIMARY,
                false
        );

        int offsetY = y + HEADER_H;
        for (Module m : modules) {
            drawModule(g, m, x, offsetY, mouseX, mouseY);
            offsetY += MODULE_H;

            if (expandedModule == m) {
                for (Setting<?> s : m.settings) {
                    drawSetting(g, s, x, offsetY);
                    offsetY += SETTING_H;
                }
            }
        }
    }

    private void drawModule(GuiGraphics g, Module m, int x, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + PANEL_WIDTH && mouseY >= y && mouseY <= y + MODULE_H;

        // hover background
        if (hovered) {
            g.fill(x + 2, y, x + PANEL_WIDTH - 2, y + MODULE_H, HOVER_BG);
        }

        // enabled indicator (lepší než full fill)
        if (m.isEnabled()) {
            g.fill(x + 4, y + 3, x + 6, y + MODULE_H - 3, ENABLED_ACCENT);
        }

        // module name — CUSTOM FONT
        g.drawString(
                this.font,
                kromcajnText(m.getName(), m.isEnabled() ? TEXT_PRIMARY : TEXT_MUTED),
                x + 10,
                y + 4,
                0xFFFFFFFF,
                false
        );

        // expand icon
        if (!m.settings.isEmpty()) {
            g.drawString(
                    this.font,
                    kromcajnText(">", ACCENT),
                    x + PANEL_WIDTH - 12,
                    y + 4,
                    ACCENT,
                    false
            );
        }
    }

    private void drawSetting(GuiGraphics g, Setting<?> s, int x, int y) {
        g.fill(x + 8, y, x + PANEL_WIDTH - 8, y + SETTING_H, 0x22FFFFFF);

        String text = s.name + ": " + s.value;

        g.drawString(
                this.font,
                kromcajnText(text, 0xFFBBBBBB),
                x + 12,
                y + 3,
                0xFFFFFFFF,
                false
        );
    }

    private void renderBorder(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + 1, PANEL_BORDER);
        g.fill(x, y, x + 1, y + h, PANEL_BORDER);
        g.fill(x + w - 1, y, x + w, y + h, PANEL_BORDER);
        g.fill(x, y + h - 1, x + w, y + h, PANEL_BORDER);
    }

    // ================= CLICK LOGIKA =================

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (checkClick(Category.RENDER, START_X, START_Y, mouseX, mouseY, button)) return true;
        if (checkClick(Category.COMBAT, START_X + SPACING_X, START_Y, mouseX, mouseY, button)) return true;
        if (checkClick(Category.MOVEMENT, START_X + SPACING_X * 2, START_Y, mouseX, mouseY, button)) return true;

        return super.mouseClicked(event, bl);
    }

    private boolean checkClick(Category category, int x, int y, double mX, double mY, int button) {
        List<Module> modules = ModuleManager.INSTANCE.getModulesInCategory(category);
        int offsetY = y + HEADER_H;

        for (Module m : modules) {
            if (mX >= x && mX <= x + PANEL_WIDTH && mY >= offsetY && mY <= offsetY + MODULE_H) {

                boolean clickedOnPlus = mX >= (x + PANEL_WIDTH - 20);

                if (!m.settings.isEmpty() && (button == 1 || (button == 0 && clickedOnPlus))) {
                    this.minecraft.setScreen(new ModuleSettingsScreen(m, this));
                    return true;
                }

                else if (button == 0) {
                    m.toggle();
                    return true;
                }
            }
            offsetY += MODULE_H;
        }
        return false;
    }

    private Component kromcajnText(String text, int color) {
        return Component.literal(text)
                .withStyle(style -> style
                        .withFont(new FontDescription.Resource(
                                Identifier.parse("modid:mojepismo")
                        ))
                        .withColor(color)
                );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}