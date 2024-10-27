package com.aetherteam.cumulus.api;

import com.aetherteam.cumulus.Cumulus;
import com.aetherteam.cumulus.mixin.mixins.client.accessor.ScreenAccessor;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;

import java.util.function.BooleanSupplier;

public class Menu {
    private final ResourceLocation icon;
    private final Component name;
    private final TitleScreen screen;
    private final BooleanSupplier condition;
    private final Runnable apply;
    private final Music music;
    private final CubeMap panorama;

    public Menu(ResourceLocation icon, Component name, TitleScreen screen, BooleanSupplier condition) {
        this(icon, name, screen, condition, new Properties());
    }

    public Menu(ResourceLocation icon, Component name, TitleScreen screen, BooleanSupplier condition, Properties properties) {
        this(icon, name, screen, condition, properties.apply, properties.music, properties.panorama);
    }

    public Menu(ResourceLocation icon, Component name, TitleScreen screen, BooleanSupplier condition, Runnable apply, Music music, CubeMap panorama) {
        this.icon = icon;
        this.name = name;
        this.screen = screen;
        this.condition = condition;
        this.apply = apply;
        this.music = music;
        this.panorama = panorama;
    }

    /**
     * @return The {@link ResourceLocation} for the icon that this menu has in the {@link com.aetherteam.cumulus.client.gui.screen.MenuSelectionScreen selection screen}.
     */
    public ResourceLocation getIcon() {
        return this.icon;
    }

    /**
     * @return The {@link Component} for the name that this menu has in the {@link com.aetherteam.cumulus.client.gui.screen.MenuSelectionScreen selection screen}.
     */
    public Component getName() {
        return this.name;
    }

    /**
     * @return The {@link TitleScreen} to display for this menu.
     */
    public TitleScreen getScreen() {
        return this.screen;
    }

    /**
     * @return The {@link BooleanSupplier} condition for when this menu should be able to display.
     */
    public BooleanSupplier getCondition() {
        return this.condition;
    }

    /**
     * @return {@link Runnable} for a function to run when this menu is applied.
     */
    public Runnable getApply() {
        return this.apply;
    }

    /**
     * @return {@link Music} to run in the menu.
     */
    public Music getMusic() {
        return this.music;
    }

    /**
     * @return {@link CubeMap} for the menu panorama.
     */
    public CubeMap getPanorama() {
        return this.panorama;
    }

    /**
     * @return The {@link ResourceLocation} of the {@link Menu}'s full registry ID.
     */
    public ResourceLocation getId() {
        return Cumulus.MENU_REGISTRY.getKey(this);
    }

    /**
     * @return The {@link String} of the {@link Menu}'s full registry ID, converted from a {@link ResourceLocation} from {@link Menu#getId()}.
     */
    public String toString() {
        return this.getId().toString();
    }

    public static class Properties {
        private Runnable apply = () -> {};
        private Music music = Musics.MENU;
        private CubeMap panorama = ScreenAccessor.cumulus$getCubeMap();

        /**
         * @see Menu#getApply()
         */
        public Properties apply(Runnable apply) {
            this.apply = apply;
            return this;
        }

        /**
         * @see Menu#getMusic()
         */
        public Properties music(Music music) {
            this.music = music;
            return this;
        }

        /**
         * @see Menu#getPanorama()
         */
        public Properties panorama(CubeMap panorama) {
            this.panorama = panorama;
            return this;
        }

        public static Properties propertiesFromType(Menu menu) {
            Properties props = new Properties();
            props.apply = menu.apply;
            props.music = menu.music;
            props.panorama = menu.panorama;
            return props;
        }
    }
}
