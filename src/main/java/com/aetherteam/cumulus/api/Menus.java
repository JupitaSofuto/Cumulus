package com.aetherteam.cumulus.api;

import com.aetherteam.cumulus.Cumulus;
import com.google.common.base.Suppliers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Menus {
    public static final ResourceLocation MINECRAFT_ICON = ResourceLocation.withDefaultNamespace("textures/block/grass_block_side.png");
    public static final Component MINECRAFT_NAME = Component.translatable("cumulus_menus.menu_title.minecraft");
    public static final BooleanSupplier MINECRAFT_CONDITION = () -> true;

    public static final Supplier<Menu> MINECRAFT = register("minecraft", new Menu(MINECRAFT_ICON, MINECRAFT_NAME, new TitleScreen(true), MINECRAFT_CONDITION));

    public static Supplier<Menu> register(String path, Menu menu) {
        var entry = Registry.register(Cumulus.MENU_REGISTRY, ResourceLocation.fromNamespaceAndPath(Cumulus.MODID, path), menu);

        return Suppliers.memoize(() -> entry);
    }

    @Nullable
    public static Menu get(String id) {
        return Cumulus.MENU_REGISTRY.get(ResourceLocation.withDefaultNamespace(id));
    }

    /**
     * @return A {@link List} of all registered {@link Menu}s.
     */
    public static List<Menu> getMenus() {
        return Cumulus.MENU_REGISTRY.stream().toList();
    }

    /**
     * @return A {@link List} of all {@link Menu}s' {@link Screen}s.
     */
    public static List<Screen> getMenuScreens() {
        return getMenus().stream().map(Menu::getScreen).collect(Collectors.toList());
    }

    public static void init() {}
}
