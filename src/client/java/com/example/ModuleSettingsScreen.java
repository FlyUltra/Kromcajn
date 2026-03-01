package com.example;

import com.example.settings.NumberSetting;
import com.example.settings.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

public class ModuleSettingsScreen extends Screen {

    // ===== DESIGN TOKENS (modern) =====
    private static final int BG_OVERLAY = 0xAA000000;
    private static final int WINDOW_BG = 0xFF0F1113;
    private static final int WINDOW_BORDER = 0xFF2A2D31;
    private static final int ACCENT = 0xFF3B82F6;
    private static final int TEXT_PRIMARY = 0xFFE5E7EB;
    private static final int TEXT_MUTED = 0xFF9CA3AF;
    private static final int SLIDER_BG = 0xFF1A1D21;
    private static final int SLIDER_FILL = 0xFF3B82F6;
    private static final int SLIDER_KNOB = 0xFFFFFFFF;
    private static final int HOVER_BG = 0x22FFFFFF;

    private final Module module;
    private final Screen parent;

    private final int windowW = 420;
    private final int windowH = 280;

    public ModuleSettingsScreen(Module module, Screen parent) {
        super(Component.literal(module.getName() + " Settings"));
        this.module = module;
        this.parent = parent;
    }

    // =========================================================
    // RENDER
    // =========================================================

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, BG_OVERLAY);

        int x = (this.width - windowW) / 2;
        int y = (this.height - windowH) / 2;

        // main panel
        g.fill(x, y, x + windowW, y + windowH, WINDOW_BG);
        renderBorder(g, x, y, windowW, windowH);

        g.fill(x, y, x + windowW, y + 2, ACCENT);

        // ===== TITLE =====
        g.drawString(
                this.font,
                kromcajnText(module.getName(), TEXT_PRIMARY),
                x + 18,
                y + 14,
                TEXT_PRIMARY,
                false
        );

        // ===== DESCRIPTION =====
        g.drawString(this.font, "Description", x + 18, y + 40, TEXT_MUTED, false);
        g.drawWordWrap(
                this.font,
                Component.literal(module.description),
                x + 18,
                y + 54,
                windowW - 36,
                TEXT_PRIMARY
        );

        // ===== SETTINGS =====
        int startY = y + 105;
        for (Setting<?> s : module.settings) {
            drawSettingElement(g, s, x + 18, startY, mouseX, mouseY);
            startY += 34;
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    // =========================================================
    // MODERN SLIDER
    // =========================================================

    private void drawSettingElement(GuiGraphics g, Setting<?> s, int x, int y, int mX, int mY) {

        // name
        g.drawString(
                this.font,
                kromcajnText(s.name, TEXT_PRIMARY),
                x,
                y,
                TEXT_PRIMARY,
                false
        );

        if (s instanceof NumberSetting ns) {
            int sliderW = 190;
            int sliderX = x;
            int sliderY = y + 12;

            boolean hovered =
                    mX >= sliderX &&
                            mX <= sliderX + sliderW &&
                            mY >= sliderY - 2 &&
                            mY <= sliderY + 10;

            // track
            g.fill(sliderX, sliderY, sliderX + sliderW, sliderY + 4, SLIDER_BG);

            double percent =
                    (((Number) ns.value).doubleValue() - ns.min) /
                            (ns.max - ns.min);

            int fill = (int) (sliderW * percent);

            // fill
            g.fill(sliderX, sliderY, sliderX + fill, sliderY + 4, SLIDER_FILL);

            int knobX = sliderX + fill - 2;
            g.fill(knobX, sliderY - 2, knobX + 6, sliderY + 6,
                    hovered ? ACCENT : SLIDER_KNOB);

            g.drawString(
                    this.font,
                    kromcajnText(String.format("%.1f", ns.value), TEXT_MUTED),
                    sliderX + sliderW + 8,
                    sliderY - 3,
                    TEXT_MUTED,
                    false
            );
        }
    }

    // =========================================================
    // INPUT
    // =========================================================

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        updateSettings(event.x(), event.y());
        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double d, double e) {
        if (event.button() == 0) {
            updateSettings(event.x(), event.y());
        }
        return super.mouseDragged(event, d, e);
    }

    private void updateSettings(double mX, double mY) {
        int x = (this.width - windowW) / 2;
        int y = (this.height - windowH) / 2;
        int startY = y + 105;

        for (Setting<?> s : module.settings) {
            if (s instanceof NumberSetting ns) {

                int sliderX = x + 18;
                int sliderW = 190;
                int sliderY = startY + 12;

                if (mX >= sliderX && mX <= sliderX + sliderW &&
                        mY >= sliderY - 2 && mY <= sliderY + 10) {

                    double percent = (mX - sliderX) / sliderW;
                    percent = Math.max(0, Math.min(1, percent));

                    double newValue = ns.min + percent * (ns.max - ns.min);
                    ns.value = Math.round(newValue * 10.0) / 10.0;
                }
            }
            startY += 34;
        }
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

    private void renderBorder(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + 1, WINDOW_BORDER);
        g.fill(x, y, x + 1, y + h, WINDOW_BORDER);
        g.fill(x + w - 1, y, x + w, y + h, WINDOW_BORDER);
        g.fill(x, y + h - 1, x + w, y + h, WINDOW_BORDER);
    }
}