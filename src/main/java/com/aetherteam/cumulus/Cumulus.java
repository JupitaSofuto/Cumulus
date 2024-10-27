package com.aetherteam.cumulus;

import com.aetherteam.cumulus.api.Menu;
import com.aetherteam.cumulus.api.Menus;
import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

public class Cumulus implements ClientModInitializer {
    public static final String MODID = "cumulus_menus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceKey<Registry<Menu>> MENU_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Cumulus.MODID, "menu"));
    public static final Registry<Menu> MENU_REGISTRY = FabricRegistryBuilder.createSimple(MENU_REGISTRY_KEY).buildAndRegister();

    @Override
    public void onInitializeClient() {
        Menus.init();

        NeoForgeConfigRegistry.INSTANCE.register(Cumulus.MODID, ModConfig.Type.CLIENT, CumulusConfig.CLIENT_SPEC);
    }
}
