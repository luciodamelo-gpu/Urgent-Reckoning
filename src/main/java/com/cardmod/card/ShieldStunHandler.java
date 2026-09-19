package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public final class ShieldStunHandler {
    private static final ResourceLocation IRON_WILL = new ResourceLocation("cardmod", "iron_will");

    private ShieldStunHandler() {}

    public static void onDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        int count = CardCapability.getCount(player, IRON_WILL);
        if (count <= 0) return;
        var cooldowns = player.getCooldowns();
        if (!cooldowns.isOnCooldown(Items.SHIELD)) return;
        int remaining = Math.round(cooldowns.getCooldownPercent(Items.SHIELD, 0f) * 100);
        int next = Math.max(0, remaining - 20 * count);
        cooldowns.removeCooldown(Items.SHIELD);
        if (next > 0) cooldowns.addCooldown(Items.SHIELD, next);
    }
}
