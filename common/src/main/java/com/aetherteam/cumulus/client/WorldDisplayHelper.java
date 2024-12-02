package com.aetherteam.cumulus.client;

import com.aetherteam.cumulus.Cumulus;
import com.aetherteam.cumulus.CumulusConfig;
import com.aetherteam.cumulus.mixin.mixins.client.accessor.MinecraftAccessor;
import com.aetherteam.cumulus.mixin.mixins.common.accessor.MinecraftServerAccessor;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class WorldDisplayHelper {
    public static boolean menuActive = false;
    @Nullable
    private static LevelSummary loadedSummary = null;

    /**
     * Checks if {@link CumulusConfig.Client#enable_world_preview} is enabled to see whether the world preview should be enabled or disabled.
     *
     * @see WorldDisplayHelper#enableWorldPreview()
     * @see WorldDisplayHelper#disableWorldPreview()
     */
    public static void toggleWorldPreview() {
        if (CumulusConfig.CLIENT.enable_world_preview.get()) {
            enableWorldPreview();
        } else {
            disableWorldPreview();
        }
    }

    /**
     * If there is no level loaded, then load a level.
     *
     * @see WorldDisplayHelper#loadLevel()
     */
    public static void enableWorldPreview() {
        Minecraft minecraft = Minecraft.getInstance();
        if (Cumulus.SERVER_INSTANCE != null && !menuActive) {
            Minecraft.getInstance().getToasts().addToast(
                    new SystemToast(
                            SystemToast.SystemToastId.WORLD_ACCESS_FAILURE,
                            Component.translatable("cumulus_menus.world_preview.toast.title"),
                            Component.translatable("cumulus_menus.world_preview.toast.description")
                    ));

            FAIL_RUN.run();
        } else if (minecraft.level == null) {
            loadLevel();
        }
    }

    /**
     * Checks if a level exists using the {@link LevelSummary} retrieved from {@link WorldDisplayHelper#getLevelSummary()},
     * sets the world preview menu as being active in {@link WorldDisplayHelper#menuActive}, sets up a loading screen, and then loads the level.
     * If the level does not exist, the world preview menu is set to be inactive and the {@link CumulusConfig.Client#enable_world_preview} config is reset.
     */
    public static void loadLevel() {
        Minecraft minecraft = Minecraft.getInstance();
        LevelSummary summary = getLevelSummary();
        if (summary != null && minecraft.getLevelSource().levelExists(summary.getLevelId())) {
            setActive();
            minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
            minecraft.createWorldOpenFlows().openWorld(summary.getLevelId(), FAIL_RUN);
        } else {
            resetActive();
            resetConfig();
        }
    }

    public static final Runnable FAIL_RUN = () -> {
        resetActive();
        resetConfig();
    };

    /**
     * Checks if a level exists using the {@link LevelSummary} retrieved from {@link WorldDisplayHelper#getLevelSummary()}, then closes any active screen and
     * enters a world that is already loaded by the world preview system, to save loading time. This is mainly used by the Quick Load feature.
     * This will reset the player's states from how they were for menu rendering to how they should be in game rendering, as done through {@link WorldDisplayHelper#resetStates()}.
     */
    public static void enterLoadedLevel() {
        Minecraft minecraft = Minecraft.getInstance();
        LevelSummary summary = getLevelSummary();
        if (summary != null && minecraft.getLevelSource().levelExists(summary.getLevelId()) && minecraft.getSingleplayerServer() != null) {
            resetStates();
            minecraft.forceSetScreen(null);
        }
    }

    /**
     * If there is a level loaded, then stop the level. Then load the title screen menu back up.
     *
     * @see WorldDisplayHelper#stopLevel(Screen)
     * @see WorldDisplayHelper#setMenu()
     */
    public static void disableWorldPreview() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            stopLevel(new GenericMessageScreen(Component.literal("")));
            setMenu();
        }
    }

    /**
     * Stops a level if one exists, after resetting the player and helper states to default with {@link WorldDisplayHelper#resetStates()}.
     *
     * @param screen The current {@link Screen}.
     */
    public static void stopLevel(@Nullable Screen screen) {
        resetStates();
        Minecraft minecraft = Minecraft.getInstance();
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (minecraft.level != null) {
            if (server != null) {
                server.halt(false);
            }
            minecraft.clearClientLevel(Objects.requireNonNullElseGet(screen, () -> new ProgressScreen(true)));
            ((MinecraftAccessor) minecraft).cumulus$setIsLocalServer(false);
        }
    }

    /**
     * Sets up a menu through Cumulus and forces it as the current screen.
     */
    public static void setMenu() {
        CumulusClient.MENU_HELPER.setShouldFade(false);
        Screen screen = CumulusClient.MENU_HELPER.applyMenu(CumulusClient.MENU_HELPER.getActiveMenu());
        if (screen != null) {
            Minecraft.getInstance().forceSetScreen(screen);
        }
    }

    /**
     * @return The {@link LevelSummary} for the world preview level.
     */
    @Nullable
    public static LevelSummary getLevelSummary() {
        if (loadedSummary == null) {
            findLevelSummary(); // This sets loadedSummary if it is null.
        }
        return loadedSummary;
    }

    /**
     * Finds the player's last opened world to use for the world preview, and applies the {@link LevelSummary} for it to {@link WorldDisplayHelper#loadedSummary}.
     */
    public static void findLevelSummary() {
        Minecraft minecraft = Minecraft.getInstance();
        LevelStorageSource source = minecraft.getLevelSource();
        try {
            List<LevelSummary> summaryList = new ArrayList<>(source.loadLevelSummaries(source.findLevelCandidates()).get());
            Collections.sort(summaryList); // Sorts the LevelSummaries by most recent to least recent.
            if (!summaryList.isEmpty()) {
                LevelSummary summary = null;

                for (int i = summaryList.size() - 1; i >= 0; i--) { // Looks for the most recent LevelSummary that isn't locked or disabled.
                    LevelSummary s = summaryList.get(i);
                    if (!s.isLocked() && !s.isDisabled()) {
                        LevelStorageSource.LevelStorageAccess access = source.createAccess(s.getLevelId());
                        summary = s;
                        access.close();
                    }
                }

                if (summary != null) {
                    loadedSummary = summary;
                }
            }
        } catch (ExecutionException | InterruptedException | UnsupportedOperationException |
                 IOException e) { // If a LevelSummary can't be found, then reset the helper back to the default states.
            resetActive();
            resetConfig();
            e.printStackTrace();
        }
    }

    /**
     * Checks whether a provided {@link LevelSummary} is the same as the one of the world preview's displayed level.
     *
     * @param summary The {@link LevelSummary} to compare to.
     * @return Whether they match, as a {@link Boolean}.
     */
    public static boolean sameSummaries(LevelSummary summary) {
        String id = getLevelSummary().getLevelId();
        if (Minecraft.getInstance().getSingleplayerServer() != null) {
            id = ((MinecraftServerAccessor) Minecraft.getInstance().getSingleplayerServer()).cumulus$getStorageSource().getLevelId();
        }
        return id.equals(summary.getLevelId());
    }

    /**
     * @see WorldDisplayHelper#resetPlayerState()
     * @see WorldDisplayHelper#resetConfig()
     */
    public static void resetStates() {
        resetPlayerState();
        resetActive();
    }

    /**
     * Re-enables the player's GUI and sets their camera perspective back to first person.
     */
    public static void resetPlayerState() {
        Minecraft.getInstance().options.hideGui = false;
        Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
    }

    /**
     * Sets the {@link CumulusConfig.Client#enable_world_preview} config to false.
     */
    public static void resetConfig() {
        CumulusConfig.CLIENT.enable_world_preview.set(false);
        CumulusConfig.CLIENT.enable_world_preview.save();
    }

    /**
     * Sets the {@link WorldDisplayHelper#menuActive} value to false, marking the world preview as not active.
     */
    public static void resetActive() {
        menuActive = false;
    }

    /**
     * Sets the {@link WorldDisplayHelper#menuActive} value to true, marking the world preview as active.
     */
    public static void setActive() {
        menuActive = true;
    }

    /**
     * @return Whether a world preview is active.
     */
    public static boolean isActive() {
        return menuActive;
    }

    /**
     * If the level is singleplayer, then hide the player's GUI and set them into third person before opening the title screen menu.
     * This is used for the panorama-style display of the world preview from the player's perspective.
     */
    public static void setupLevelForDisplay() {
        Minecraft minecraft = Minecraft.getInstance();
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server != null) {
            Minecraft.getInstance().options.hideGui = true;
            Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
            WorldDisplayHelper.setMenu();
        }
    }
}