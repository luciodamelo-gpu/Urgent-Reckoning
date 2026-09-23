package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class DamageReductionHandler {
    private static final ResourceLocation CERAMIC_SKIN = new ResourceLocation("cardmod", "ceramic_skin");
    private static final ResourceLocation UNMOVING = new ResourceLocation("cardmod", "unmoving");

    private DamageReductionHandler() {}

    public static void onHurt(LivingHurtEvent event) {
        if (event.isCanceled()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        double reduction = 0.0;
        int ceramic = CardCapability.getCount(player, CERAMIC_SKIN);
        if (ceramic > 0) reduction += 0.04 * ceramic;
        int unmoving = CardCapability.getCount(player, UNMOVING);
        if (unmoving > 0 && player.isCrouching()) reduction += 0.15 * unmoving;
        if (reduction <= 0) return;
        reduction = Math.min(0.95, reduction);
        event.setAmount(event.getAmount() * (float) (1.0 - reduction));
    }
}
