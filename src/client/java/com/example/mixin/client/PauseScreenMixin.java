package com.example.mixin.client;

import com.example.gui.ClickGuiScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Říkáme hře: "Tento kód se napojí do třídy PauseScreen"
@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {

    protected PauseScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {

        this.addRenderableWidget(Button.builder(Component.literal("Kromcajn Menu"), button -> {

            if (this.minecraft != null) {
                this.minecraft.setScreen(new ClickGuiScreen());
            }

        }).bounds(10, 10, 120, 20).build());
    }
}