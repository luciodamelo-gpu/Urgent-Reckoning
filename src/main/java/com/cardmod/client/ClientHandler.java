package com.cardmod.client;

import com.cardmod.CardMod;
import com.cardmod.registry.CardRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = CardMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientHandler {
    private ClientHandler() {
    }

    public static void openScreen(List<ResourceLocation> offered) {
        var cards = offered.stream().map(CardRegistry::get).filter(c -> c != null).toList();
        if (cards.isEmpty()) cards = List.copyOf(CardRegistry.getAll());
        Minecraft.getInstance().setScreen(new CardSelectionScreen(cards));
    }

}
