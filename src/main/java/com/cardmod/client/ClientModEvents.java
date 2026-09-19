package com.cardmod.client;

import com.cardmod.CardMod;
import com.cardmod.capability.ShieldHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = CardMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(ShieldKeybinds.SHIELD_TACZ);
        event.register(ShieldKeybinds.SHIELD_BLOCK);
    }

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "shield_bar", (gui, gfx, partialTick, w, h) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.options.hideGui) return;
            if (!ClientCardCache.hasGoodTiming()) return;
            if (!ShieldHelper.isShieldingClient(mc.player)) return;
            float frac = ClientCardCache.shieldFraction();
            int bw = 41, bh = 5;
            int x = w / 2 - bw / 2;
            int y = h / 2 + 14;
            gfx.fill(x - 1, y - 1, x + bw + 1, y + bh + 1, 0xFF000000);
            gfx.fill(x, y, x + bw, y + bh, 0xFF222222);
            int filled = Math.round(bw * frac);
            int col = frac >= 1f ? 0xFF55FF55 : 0xFFAAAAAA;
            if (filled > 0) gfx.fill(x, y, x + filled, y + bh, col);
        });
    }
}
