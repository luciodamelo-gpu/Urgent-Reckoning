package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SixteenHandler {
    private static final ResourceLocation SIXTEEN = new ResourceLocation("cardmod", "sixteen_going_on_eighteen");
    private static final Map<UUID, Integer> HITS = new ConcurrentHashMap<>();

    private SixteenHandler() {}

    public static void onHurt(LivingHurtEvent event) {
        if (event.isCanceled()) return;
        Player player = DamageHelper.playerAttacker(event.getSource());
        if (player == null) return;
        int count = CardCapability.getCount(player, SIXTEEN);
        if (count <= 0) return;
        int n = HITS.getOrDefault(player.getUUID(), 0) + 1;
        if (n >= 16) {
            n = 0;
            event.setAmount(event.getAmount() * 2f);
            player.displayClientMessage(Component.literal("Critical hit!"), true);
        }
        HITS.put(player.getUUID(), n);
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        HITS.remove(event.getEntity().getUUID());
    }
}
