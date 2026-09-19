package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class DamageReductionHandler {
    private static final ResourceLocation CERAMIC_SKIN = new ResourceLocation("cardmod", "ceramic_skin");

    private DamageReductionHandler() {}

    public static void onHurt(LivingHurtEvent event) {
        if (event.isCanceled()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        int count = CardCapability.getCount(player, CERAMIC_SKIN);
        if (count <= 0) return;
        double reduction = Math.min(0.95, 0.04 * count);
        event.setAmount(event.getAmount() * (float) (1.0 - reduction));
    }
}
