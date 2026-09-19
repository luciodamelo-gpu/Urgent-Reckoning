package com.cardmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public final class ShieldKeybinds {
    public static final KeyMapping SHIELD_TACZ = new KeyMapping(
            "key.cardmod.shield_block_tacz",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.cardmod");
    public static final KeyMapping SHIELD_BLOCK = new KeyMapping(
            "key.cardmod.block",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.categories.cardmod");

    private ShieldKeybinds() {}

    public static boolean isTaczShieldDown() {
        return SHIELD_TACZ.isDown();
    }

    public static boolean isBlockDown() {
        return SHIELD_BLOCK.isDown();
    }

    public static boolean isShieldDown() {
        return SHIELD_TACZ.isDown();
    }
}
