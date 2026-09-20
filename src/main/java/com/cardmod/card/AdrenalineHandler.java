package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import java.util.UUID;

public final class AdrenalineHandler {
    private static final ResourceLocation ADRENALINE = new ResourceLocation("cardmod", "adrenaline");
    private static final UUID ID = UUID.fromString("7c1e9a2b-4d5f-4a6e-8b0c-9d3e5f7a1b2c");

    private AdrenalineHandler() {
    }

    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        int count = CardCapability.getCount(player, ADRENALINE);
        var instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance == null) return;
        if (count > 0 && player.getHealth() < player.getMaxHealth() * 0.5f) {
            instance.removeModifier(ID);
            instance.addTransientModifier(new AttributeModifier(ID, "cardmod:adrenaline",
                    0.05 * count, AttributeModifier.Operation.MULTIPLY_TOTAL));
        } else {
            instance.removeModifier(ID);
        }
    }
}
