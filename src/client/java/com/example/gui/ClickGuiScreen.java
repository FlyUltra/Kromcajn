package com.example.gui;
import com.example.ModuleManager;
import com.example.settings.BooleanSetting;
import com.example.settings.ModeSetting;
import com.example.settings.NumberSetting;
import com.example.settings.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import com.example.Module;
import java.awt.Color;
import java.util.List;

public class ClickGuiScreen extends Screen {

    private static final int W = 520;
    private static final int H = 340;
    private static final int SIDEBAR_W = 130;

    // --- Moderní Paleta (Red & Dark) ---
    private static final int BG_MAIN = 0xEE0A0A0A;
    private static final int BG_CARD = 0xFF141414;
    private static final int ACCENT_RED = 0xFFEF4444;
    private static final int TEXT_PRIMARY = 0xFFF8FAFC;
    private static final int TEXT_DIM = 0xFF64748B;

    private Category selectedCategory = Category.COMBAT;
    private Module selectedModule = null;
    private boolean bindingMode = false;

    private final ParticleEngine particleEngine = new ParticleEngine(80);

    public ClickGuiScreen() {
        super(Component.literal("Kromcajn"));
    }

    private int getRGB() {
        float hue = (System.currentTimeMillis() % 5000) / 5000f;
        return Color.HSBtoRGB(hue, 0.8f, 1f);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0x88000000);
        particleEngine.render(g, this.width, this.height);

        int x = (this.width - W) / 2;
        int y = (this.height - H) / 2;
        int chroma = getRGB();

        // Hlavní okno
        g.fill(x - 1, y - 1, x + W + 1, y + H + 1, 0x55000000);
        drawRoundedRect(g, x, y, W, H, BG_MAIN);
        g.fill(x, y, x + W, y + 2, chroma);

        renderSidebar(g, x, y, mouseX, mouseY);
        renderModules(g, x, y, mouseX, mouseY, chroma);

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void renderSidebar(GuiGraphics g, int x, int y, int mouseX, int mouseY) {
        int catY = y + 40;
        for (Category cat : Category.values()) {
            boolean isSelected = (selectedCategory == cat);
            boolean hovered = mouseX >= x && mouseX <= x + SIDEBAR_W && mouseY >= catY && mouseY <= catY + 22;

            if (isSelected) {
                g.fill(x + 5, catY, x + 7, catY + 18, ACCENT_RED);
                g.drawString(this.font, cat.name(), x + 15, catY + 5, ACCENT_RED, false);
            } else {
                g.drawString(this.font, cat.name(), x + 15, catY + 5, hovered ? TEXT_PRIMARY : TEXT_DIM, false);
            }
            catY += 28;
        }
    }

    private void renderModules(GuiGraphics g, int x, int y, int mouseX, int mouseY, int chroma) {
        int moduleX = x + SIDEBAR_W + 15;
        int moduleY = y + 45;
        List<Module> modules = ModuleManager.INSTANCE.getModulesInCategory(selectedCategory);

        g.drawString(this.font, selectedCategory.name().toUpperCase(), moduleX, y + 25, TEXT_DIM, false);

        for (Module m : modules) {
            int cardW = W - SIDEBAR_W - 30;
            int cardH = 32;

            drawRoundedRect(g, moduleX, moduleY, cardW, cardH, BG_CARD);

            if (m.isEnabled()) {
                renderBorder(g, moduleX, moduleY, cardW, cardH, ACCENT_RED);
            }

            g.drawString(this.font, m.getName(), moduleX + 10, moduleY + 12, m.isEnabled() ? TEXT_PRIMARY : TEXT_DIM, false);

            String gear = (selectedModule == m) ? "[-]" : "[+]";
            g.drawString(this.font, gear, moduleX + cardW - 25, moduleY + 12, ACCENT_RED, false);

            if (selectedModule == m) {
                drawSettingsArea(g, m, moduleX, moduleY + 35, cardW, mouseX, mouseY);
                moduleY += (m.getSettings().size() * 18) + 40;
            }
            moduleY += 38;
        }
    }

    private void drawSettingsArea(GuiGraphics g, Module m, int x, int y, int w, int mouseX, int mouseY) {
        int sCount = m.getSettings().size();
        drawRoundedRect(g, x + 5, y, w - 10, (sCount * 18) + 35, 0xFF080808);

        // Render BIND
        boolean hoverBind = mouseX >= x + 15 && mouseX <= x + 150 && mouseY >= y + 8 && mouseY <= y + 22;
        String bindName = (m.getKey() == 0) ? "NONE" : GLFW.glfwGetKeyName(m.getKey(), 0);
        if (bindName == null) bindName = "KEY " + m.getKey();

        String bindLabel = bindingMode ? "> PRESS KEY <" : "Bind: " + bindName;
        g.drawString(this.font, bindLabel, x + 15, y + 10, bindingMode ? 0xFFFFFF00 : (hoverBind ? TEXT_PRIMARY : ACCENT_RED), false);

        int sY = y + 28;
        for (Setting<?> s : m.getSettings()) {
            boolean hovered = mouseX >= x + 18 && mouseX <= x + w - 18 && mouseY >= sY && mouseY <= sY + 14;

            String valStr = s.value.toString();
            if (s instanceof NumberSetting) valStr = String.format("%.1f", (Double)s.value);

            g.drawString(this.font, s.name + ":", x + 18, sY, TEXT_DIM, false);
            int offset = this.font.width(s.name + ": ");
            g.drawString(this.font, valStr, x + 18 + offset, sY, hovered ? ACCENT_RED : TEXT_PRIMARY, false);

            sY += 18;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double mX = event.x();
        double mY = event.y();
        int button = event.button();

        int x = (this.width - W) / 2;
        int y = (this.height - H) / 2;

        // Sidebar click
        int catY = y + 40;
        for (Category cat : Category.values()) {
            if (mX >= x && mX <= x + SIDEBAR_W && mY >= catY && mY <= catY + 25) {
                selectedCategory = cat;
                selectedModule = null;
                return true;
            }
            catY += 28;
        }

        return checkModuleClicks(x, y, mX, mY, button) || super.mouseClicked(event, bl);
    }

    private boolean checkModuleClicks(int x, int y, double mX, double mY, int button) {
        int moduleX = x + SIDEBAR_W + 15;
        int moduleY = y + 45;
        int cardW = W - SIDEBAR_W - 30;

        for (Module m : ModuleManager.INSTANCE.getModulesInCategory(selectedCategory)) {
            // Klik na kartu modulu
            if (mX >= moduleX && mX <= moduleX + cardW && mY >= moduleY && mY <= moduleY + 32) {
                if (mX >= moduleX + cardW - 30) {
                    selectedModule = (selectedModule == m) ? null : m;
                } else if (button == 0) {
                    m.toggle();
                    // ZDE: Kromcajn.INSTANCE.configManager.save();
                }
                return true;
            }

            if (selectedModule == m) {
                // Klik na BIND
                if (mX >= moduleX + 15 && mX <= moduleX + 150 && mY >= moduleY + 35 && mY <= moduleY + 55) {
                    bindingMode = true;
                    return true;
                }

                // Klik na SETTINGS
                int sY = moduleY + 35 + 28;
                for (Setting<?> s : m.getSettings()) {
                    if (mX >= moduleX + 15 && mX <= moduleX + cardW - 15 && mY >= sY && mY <= sY + 14) {
                        handleSettingClick(s, button);
                        // ZDE: Kromcajn.INSTANCE.configManager.save();
                        return true;
                    }
                    sY += 18;
                }
                moduleY += (m.getSettings().size() * 18) + 40;
            }
            moduleY += 38;
        }
        return false;
    }

    private void handleSettingClick(Setting<?> s, int button) {
        if (s instanceof ModeSetting ms) {
            if (button == 0) ms.index = (ms.index + 1) % ms.modes.length;
            else if (button == 1) ms.index = (ms.index <= 0) ? ms.modes.length - 1 : ms.index - 1;
            ms.value = ms.modes[ms.index];
        } else if (s instanceof NumberSetting ns) {
            double step = 0.1;
            if (button == 0) ns.value = Math.min(ns.max, ns.value + step);
            else if (button == 1) ns.value = Math.max(ns.min, ns.value - step);
            ns.value = Math.round(ns.value * 10.0) / 10.0;
        } else if (s instanceof BooleanSetting bs) {
            if (button == 0) { // Levé tlačítko myši
                bs.value = !bs.value; // Přepne true na false a naopak
            }
        }
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        // 1. Zachycení bindu modulu (zůstává stejné)
        if (bindingMode && selectedModule != null) {
            if (keyEvent.isEscape()) {
                selectedModule.setKey(0);
            } else {
                selectedModule.setKey(keyEvent.key());
            }
            bindingMode = false;
            // ZDE: Kromcajn.INSTANCE.configManager.save();
            return true;
        }

        // 2. Zavření menu na ESC
        if (keyEvent.isEscape() && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }

        // 3. Původní navigace (Tab, šipky atd.) bez volání privátních metod
        if (super.keyPressed(keyEvent)) {
            return true;
        } else {
            // Místo this.createTabEvent použijeme přímo konstruktor
            net.minecraft.client.gui.navigation.FocusNavigationEvent navEvent = switch (keyEvent.key()) {
                case 258 -> new net.minecraft.client.gui.navigation.FocusNavigationEvent.TabNavigation(!keyEvent.hasShiftDown());
                case 262 -> new net.minecraft.client.gui.navigation.FocusNavigationEvent.ArrowNavigation(net.minecraft.client.gui.navigation.ScreenDirection.RIGHT);
                case 263 -> new net.minecraft.client.gui.navigation.FocusNavigationEvent.ArrowNavigation(net.minecraft.client.gui.navigation.ScreenDirection.LEFT);
                case 264 -> new net.minecraft.client.gui.navigation.FocusNavigationEvent.ArrowNavigation(net.minecraft.client.gui.navigation.ScreenDirection.DOWN);
                case 265 -> new net.minecraft.client.gui.navigation.FocusNavigationEvent.ArrowNavigation(net.minecraft.client.gui.navigation.ScreenDirection.UP);
                default -> null;
            };

            if (navEvent != null) {
                net.minecraft.client.gui.ComponentPath path = super.nextFocusPath(navEvent);
                if (path == null && navEvent instanceof net.minecraft.client.gui.navigation.FocusNavigationEvent.TabNavigation) {
                    this.clearFocus();
                    path = super.nextFocusPath(navEvent);
                }
                if (path != null) this.changeFocus(path);
            }
            return false;
        }
    }

    // --- Pomocné render metody ---
    private void drawRoundedRect(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x + 1, y, x + w - 1, y + h, color);
        g.fill(x, y + 1, x + 1, y + h - 1, color);
        g.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    private void renderBorder(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + 1, color);
        g.fill(x, y + h - 1, x + w, y + h, color);
        g.fill(x, y, x + 1, y + h, color);
        g.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}