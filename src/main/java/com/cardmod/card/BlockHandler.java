package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import com.cardmod.capability.ShieldHelper;
import com.cardmod.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class BlockHandler {
    private static final ResourceLocation GOOD_TIMING = new ResourceLocation("cardmod", "good_timing");
    private static final long COOLDOWN_MS = 10_000L;

    private BlockHandler() {}

    public static void onHurt(LivingHurtEvent event) {
        if (event.isCanceled()) return;
        if (event.getAmount() <= 0) return;
        if (!(event.getEntity() instanceof Player player)) return;
        int count = CardCapability.getCount(player, GOOD_TIMING);
        if (count <= 0) return;
        long now = System.currentTimeMillis();
        if (now < CardCapability.getGoodTimingReadyAt(player)) return;
        if (!ShieldHelper.isShielding(player)) return;
        event.setCanceled(true);
        CardCapability.setGoodTimingReadyAt(player, now + COOLDOWN_MS);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1f, 1f);
        player.displayClientMessage(Component.literal("Blocked!"), true);
        if (player instanceof ServerPlayer sp) NetworkHandler.syncTo(sp);
    }
}
