package com.aetherteam.cumulus.mixin.mixins.client.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Mutable
    @Accessor("MENU_BACKGROUND")
    static void cumulus$setMenuBackground(ResourceLocation location) {
        throw new AssertionError();
    }

    @Mutable
    @Accessor("HEADER_SEPARATOR")
    static void cumulus$setHeaderSeparator(ResourceLocation location) {
        throw new AssertionError();
    }

    @Mutable
    @Accessor("FOOTER_SEPARATOR")
    static void cumulus$setFooterSeparator(ResourceLocation location) {
        throw new AssertionError();
    }

    @Accessor("minecraft")
    Minecraft cumulus$minecraft();
}
