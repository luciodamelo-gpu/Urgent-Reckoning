package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class SoftLandingHandler {
    private static final ResourceLocation SOFT_LANDING = new ResourceLocation("cardmod", "soft_landing");

    private SoftLandingHandler() {
    }

    public static void onHurt(LivingHurtEvent event) {
        if (event.isCanceled()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!event.getSource().is(DamageTypes.FALL)) return;
        int count = CardCapability.getCount(player, SOFT_LANDING);
        if (count <= 0) return;
        double reduction = Math.min(0.95, 0.25 * count);
        event.setAmount(event.getAmount() * (float) (1.0 - reduction));
    }
}
