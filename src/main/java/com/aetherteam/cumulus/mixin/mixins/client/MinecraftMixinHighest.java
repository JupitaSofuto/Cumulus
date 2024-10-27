package com.aetherteam.cumulus.mixin.mixins.client;

import com.aetherteam.cumulus.client.event.listeners.MenuListener;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = 333)
public abstract class MinecraftMixinHighest {

    @Shadow @Nullable public Screen screen;

    @Definition(id = "screen", field = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;")
    @Definition(id = "guiScreen", local = @Local(argsOnly = true, type = Screen.class))
    @Expression("this.screen = guiScreen")
    @Inject(method = "setScreen", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.BEFORE))
    private void beforeScreenSetCall(@Nullable Screen guiScreen, CallbackInfo ci) {
        if (guiScreen != null) {
            var oldScreen = this.screen;

            MenuListener.onGuiOpenHighest(oldScreen, guiScreen);
        }
    }
}
