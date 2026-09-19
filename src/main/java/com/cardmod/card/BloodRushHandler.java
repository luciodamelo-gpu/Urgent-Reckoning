package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BloodRushHandler {
    private static final ResourceLocation BLOOD_RUSH = new ResourceLocation("cardmod", "blood_rush");
    private static final UUID ID = UUID.fromString("3b2a2e7c-5d3a-4f1e-9f2c-1a2b3c4d5e6f");
    private static final Map<UUID, Long> UNTIL = new ConcurrentHashMap<>();
    private BloodRushHandler() {}
    public static void onDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        int c = CardCapability.getCount(p, BLOOD_RUSH);
        if (c <= 0) return;
        if (event.getAmount() <= 0) return;
        long until = System.currentTimeMillis() + 3000L;
        UNTIL.put(p.getUUID(), until);
        apply(p, c);
    }
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player p = event.player;
        Long until = UNTIL.get(p.getUUID());
        if (until == null) return;
        if (System.currentTimeMillis() >= until) {
            UNTIL.remove(p.getUUID());
            var inst = p.getAttribute(Attributes.MOVEMENT_SPEED);
            if (inst != null) inst.removeModifier(ID);
        } else {
            int c = CardCapability.getCount(p, BLOOD_RUSH);
            if (c <= 0) {
                UNTIL.remove(p.getUUID());
                var inst = p.getAttribute(Attributes.MOVEMENT_SPEED);
                if (inst != null) inst.removeModifier(ID);
            }
        }
    }
    private static void apply(Player p, int c) {
        var inst = p.getAttribute(Attributes.MOVEMENT_SPEED);
        if (inst == null) return;
        inst.removeModifier(ID);
        double amt = 0.10 * c;
        inst.addTransientModifier(new AttributeModifier(ID, "cardmod:blood_rush", amt, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
