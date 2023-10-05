package com.aetherteam.cumulus.data.providers;

import io.github.fabricators_of_create.porting_lib.data.LanguageProvider;
import net.minecraft.data.PackOutput;

public abstract class CumulusLanguageProvider extends LanguageProvider {
    protected final String id;

    public CumulusLanguageProvider(PackOutput output, String id) {
        super(output, id, "en_us");
        this.id = id;
    }

    public void addGuiText(String key, String name) {
        this.add("gui." + this.id + "." + key, name);
    }

    public void addMenuTitle(String key, String name) {
        this.add(this.id + ".menu_title." + key, name);
    }

    public void addClientConfig(String prefix, String key, String name) {
        this.add("config." + this.id + ".client." + prefix + "." + key, name);
    }

    public void addPackDescription(String packName, String description) {
        this.add("pack." + this.id + "." + packName + ".description", description);
    }
}
