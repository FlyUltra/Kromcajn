package com.example;

import com.example.settings.NumberSetting;
import com.example.settings.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ModuleSettingsScreen extends Screen {
    private final Module module;
    private final Screen parent;
    private final int windowW = 400;
    private final int windowH = 260;

    public ModuleSettingsScreen(Module module, Screen parent) {
        super(Component.literal(module.getName() + " Settings"));
        this.module = module;
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0xAA000000);

        int x = (this.width - windowW) / 2;
        int y = (this.height - windowH) / 2;

        g.fill(x, y, x + windowW, y + windowH, 0xFF111315);
        renderBorder(g, x, y, windowW, windowH);

        g.fill(x, y, x + windowW, y + 2, 0xFF3B82F6);
        g.drawString(this.font, module.getName() + " - Konfigurace", x + 15, y + 15, 0xFFFFFFFF, false);

        g.drawString(this.font, "Popis:", x + 15, y + 40, 0xFF9CA3AF, false);
        g.drawWordWrap(this.font, Component.literal(module.description), x + 15, y + 55, windowW - 30, 0xFFE5E7EB);

        int startY = y + 100;
        for (Setting<?> s : module.settings) {
            drawSettingElement(g, s, x + 15, startY, mouseX, mouseY);
            startY += 30;
        }

        g.drawString(this.font, "[ ESC pro zavření ]", x + windowW - 100, y + windowH - 20, 0xFF555555, false);

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void drawSettingElement(GuiGraphics g, Setting<?> s, int x, int y, int mX, int mY) {
        g.drawString(this.font, s.name, x, y, 0xFFE5E7EB, false);

        if (s instanceof NumberSetting ns) {
            int sliderW = 150;
            int sliderX = x + 100;

            g.fill(sliderX, y, sliderX + sliderW, y + 10, 0xFF2A2D31);

            double diff = Math.min(sliderW, Math.max(0, (((Number) ns.value).doubleValue() - ns.min) / (ns.max - ns.min) * sliderW));
            g.fill(sliderX, y, sliderX + (int) diff, y + 10, 0xFF3B82F6);

            g.drawString(this.font, String.format("%.1f", ns.value), sliderX + sliderW + 5, y + 1, 0xFFFFFFFF, false);
        }
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        updateSettings(event.x(), event.y());
        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        if (mouseButtonEvent.button() == 0) {
            updateSettings(mouseButtonEvent.x(), mouseButtonEvent.y());
        }
        return super.mouseDragged(mouseButtonEvent, d, e);
    }

    private void updateSettings(double mX, double mY) {
        int x = (this.width - windowW) / 2;
        int y = (this.height - windowH) / 2;
        int startY = y + 100;

        for (Setting<?> s : module.settings) {
            if (s instanceof NumberSetting ns) {
                int sliderX = x + 15 + 100;
                int sliderW = 150;

                // Pokud myš kliká v oblasti slideru (výška 10px)
                if (mX >= sliderX && mX <= sliderX + sliderW && mY >= startY && mY <= startY + 10) {
                    double percent = (mX - sliderX) / (double) sliderW;
                    double newValue = ns.min + (percent * (ns.max - ns.min));

                    ns.value = Math.round(newValue * 10.0) / 10.0;
                }
            }
            startY += 30;
        }
    }

    private void renderBorder(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + 1, 0xFF2A2D31);
        g.fill(x, y, x + 1, y + h, 0xFF2A2D31);
        g.fill(x + w - 1, y, x + w, y + h, 0xFF2A2D31);
        g.fill(x, y + h - 1, x + w, y + h, 0xFF2A2D31);
    }
}