package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class CleanHitHandler {
    private static final ResourceLocation CLEAN_HIT = new ResourceLocation("cardmod", "clean_hit");

    private CleanHitHandler() {
    }

    public static void onHurt(LivingHurtEvent event) {
        if (event.isCanceled()) return;
        Player player = DamageHelper.playerAttacker(event.getSource());
        if (player == null) return;
        LivingEntity victim = event.getEntity();
        if (victim.getHealth() < victim.getMaxHealth() * 0.75f) return;
        int count = CardCapability.getCount(player, CLEAN_HIT);
        if (count <= 0) return;
        event.setAmount(event.getAmount() * (1f + 0.05f * count));
    }
}
