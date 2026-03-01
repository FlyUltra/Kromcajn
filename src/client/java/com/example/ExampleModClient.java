package com.example;

import com.example.config.ConfigManager;
import com.example.gui.ClickGuiScreen;
import com.example.gui.MenuParticleManager;
import com.example.modules.ESP;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ExampleModClient implements ClientModInitializer {


	@Override
	public void onInitializeClient() {

		ConfigManager configManager = new ConfigManager();
		configManager.load();

		HudRenderCallback.EVENT.register((g, tickDelta) -> {
			Minecraft mc = Minecraft.getInstance();

			if (mc.screen != null) {
				MenuParticleManager.render(g, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
			} else {
				MenuParticleManager.reset();
			}
		});

		HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
			Minecraft client = Minecraft.getInstance();
			if (client == null || client.font == null) return;

			float hue = (System.currentTimeMillis() % 3000) / 3000.0f;
			int rgbColor = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);

			int rainbowWithAlpha = 0xFF000000 | (rgbColor & 0x00FFFFFF);

			Component txt = Component.literal("Kromcajn")
					.withStyle(Style.EMPTY
							.withFont(new FontDescription.Resource(
									Identifier.parse("modid:mojepismo")
							))
							.withColor(rainbowWithAlpha)
					);

			Component version = Component.literal(" v0.1.5")
					.withStyle(Style.EMPTY
							.withFont(new FontDescription.Resource(
									Identifier.parse("modid:mojepismo")
							))
							.withColor(0xAAAAAA)
					);

			Component full = txt.copy().append(version);

			int x = 10;
			int y = 10;
			int padding = 4;
			int textWidth = client.font.width(full);
			int textHeight = 8;

			drawContext.fill(x - padding, y - padding, x + textWidth + padding, y + textHeight + padding, 0x90000000);

			drawContext.fill(x - padding, y - padding, x - padding + 2, y + textHeight + padding, rainbowWithAlpha);

			drawContext.drawString(client.font, full, x + 2, y, 0xFFFFFFFF, false);
		});

		HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
			Minecraft client = Minecraft.getInstance();
			if (client == null || client.font == null || client.player == null) return;

			int yOffset = 25;

			var activeModules = ModuleManager.INSTANCE.getModules().stream()
					.filter(Module::isEnabled)
					.sorted((m1, m2) -> Integer.compare(client.font.width(m2.getName()), client.font.width(m1.getName())))
					.toList();

			for (Module m : activeModules) {
				String name = m.getName();
				int textWidth = client.font.width(name);
				int x = 5;

				float hue = ((System.currentTimeMillis() + (yOffset * 10)) % 3000) / 3000.0f;
				int color = 0xFF000000 | java.awt.Color.HSBtoRGB(hue, 0.7f, 1.0f);

				drawContext.fill(x - 2, yOffset - 1, x + textWidth + 4, yOffset + 9, 0x70000000);

				drawContext.fill(x - 2, yOffset - 1, x, yOffset + 9, color);

				drawContext.drawString(
						client.font,
						Component.literal(name).withStyle(Style.EMPTY
										.withFont(new FontDescription.Resource(
												Identifier.parse("modid:mojepismo")
										))),
						x + 3,
						yOffset,
						0xFFFFFFFF,
						false
				);

				yOffset += 11;
			}
		});


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			if (InputConstants.isKeyDown(client.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT)) {
				if (client.screen == null) {
					client.setScreen(new ClickGuiScreen());
				}
			}
		});


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
				for (Module m : ModuleManager.INSTANCE.getModules()) {
					if (m.isEnabled()) {
						m.onTick();
					}
				}
			}
		});

	}
}