package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class GlassCannonHandler {
    private static final ResourceLocation GLASS_CANNON = new ResourceLocation("cardmod", "glass_cannon");

    private GlassCannonHandler() {
    }

    public static void onHurt(LivingHurtEvent event) {
        if (event.isCanceled()) return;
        Player attacker = DamageHelper.playerAttacker(event.getSource());
        if (attacker != null) {
            int count = CardCapability.getCount(attacker, GLASS_CANNON);
            if (count > 0) event.setAmount(event.getAmount() * (1f + 0.15f * count));
        }
        if (event.getEntity() instanceof Player victim) {
            int count = CardCapability.getCount(victim, GLASS_CANNON);
            if (count > 0) {
                double taken = Math.min(2.0, 1.0 + 0.05 * count);
                event.setAmount(event.getAmount() * (float) taken);
            }
        }
    }
}
