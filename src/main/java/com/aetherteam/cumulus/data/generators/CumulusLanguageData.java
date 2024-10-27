package com.aetherteam.cumulus.data.generators;

import com.aetherteam.cumulus.data.providers.CumulusLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class CumulusLanguageData extends CumulusLanguageProvider {
    public CumulusLanguageData(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected void addTranslations() {
        this.addGuiText("button.menu_list", "Menu List");
        this.addGuiText("button.menu_launch", "Launch Menu");
        this.addGuiText("title.menu_selection", "Choose a Main Menu");

        this.addMenuTitle("minecraft", "Minecraft");

        this.addClientConfig("menu", "enable_menu_api", "Determines whether the Menu API is enabled or not");
        this.addClientConfig("menu", "active_menu", "Sets the current active menu title screen");
        this.addClientConfig("menu", "enable_menu_list_button", "Adds a button to the top right of the main menu screen to open a menu selection screen");

        this.addPackDescription("mod", "Cumulus Resources");
    }
}
