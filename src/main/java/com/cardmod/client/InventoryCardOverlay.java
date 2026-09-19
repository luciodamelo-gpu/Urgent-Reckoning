package com.cardmod.client;

import com.cardmod.CardMod;
import com.cardmod.card.Card;
import com.cardmod.card.Rarity;
import com.cardmod.registry.CardRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = CardMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class InventoryCardOverlay {
    private InventoryCardOverlay() {}

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen)) return;
        Map<ResourceLocation, Integer> counts = ClientCardCache.getCounts();
        if (counts.isEmpty()) return;
        Minecraft mc = Minecraft.getInstance();
        var gfx = event.getGuiGraphics();
        int h = mc.getWindow().getGuiScaledHeight();
        int i = 0;
        for (var e : counts.entrySet()) {
            if (e.getValue() <= 0) continue;
            Card card = CardRegistry.get(e.getKey());
            if (card == null) continue;
            String text = card.getDisplayName() + (e.getValue() > 1 ? " x" + e.getValue() : "");
            gfx.drawString(mc.font, text, 8, h - 12 - i * 10, rarityColor(card.getRarity()), false);
            i++;
        }
    }

    private static int rarityColor(Rarity r) {
        return switch (r) {
            case COMMON -> 0xFFFFFF;
            case UNCOMMON -> 0x55FF55;
            case RARE -> 0x5555FF;
            case LEGENDARY -> 0xFFE14D;
            case CURSED -> 0xAA55FF;
            case MYTHIC -> 0xD9A400;
            case GODLY -> 0x8A6300;
        };
    }
}
