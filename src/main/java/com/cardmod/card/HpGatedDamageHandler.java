package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public final class HpGatedDamageHandler {
    private record Gate(ResourceLocation id, float threshold, float rate) {
    }

    private static final List<Gate> GATES = List.of(
            new Gate(new ResourceLocation("cardmod", "desperation"), 0.25f, 0.05f),
            new Gate(new ResourceLocation("cardmod", "bloodied"), 0.25f, 0.15f),
            new Gate(new ResourceLocation("cardmod", "berserkers_pulse"), 0.50f, 0.15f));

    private HpGatedDamageHandler() {
    }

    public static void onHurt(LivingHurtEvent event) {
        if (event.isCanceled()) return;
        Player player = DamageHelper.playerAttacker(event.getSource());
        if (player == null) return;
        float bonus = 0f;
        for (Gate gate : GATES) {
            int count = CardCapability.getCount(player, gate.id());
            if (count <= 0) continue;
            if (player.getHealth() < player.getMaxHealth() * gate.threshold()) {
                bonus += gate.rate() * count;
            }
        }
        if (bonus != 0f) event.setAmount(event.getAmount() * (1f + bonus));
    }
}
