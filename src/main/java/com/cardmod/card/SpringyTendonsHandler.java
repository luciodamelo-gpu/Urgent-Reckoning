package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;

public final class SpringyTendonsHandler {
    private static final ResourceLocation SPRINGY = new ResourceLocation("cardmod", "springy_tendons");
    private static final ResourceLocation THICK_SOLES = new ResourceLocation("cardmod", "thick_soles");
    private SpringyTendonsHandler() {}
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        int total = CardCapability.getCount(p, SPRINGY) + CardCapability.getCount(p, THICK_SOLES);
        if (total <= 0) return;
        p.setDeltaMovement(p.getDeltaMovement().add(0, 0.22 * total, 0));
        p.hasImpulse = true;
    }
}
