package com.aetherteam.cumulus.mixin.mixins.client.accessor;

import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TitleScreen.class)
public interface TitleScreenAccessor {
    @Accessor("splash")
    SplashRenderer cumulus$getSplash();

    @Accessor("splash")
    void setSplash(SplashRenderer splash);

    @Mutable
    @Accessor("fading")
    void cumulus$setFading(boolean fading);

    @Accessor("fadeInStart")
    void cumulus$setFadeInStart(long fadeInStart);

    @Accessor("panorama")
    void cumulus$setPanorama(PanoramaRenderer panorama);
}
