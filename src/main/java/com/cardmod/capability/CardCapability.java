package com.cardmod.capability;

import com.cardmod.CardMod;
import com.cardmod.card.Card;
import com.cardmod.network.NetworkHandler;
import com.cardmod.registry.CardRegistry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Map;

public final class CardCapability {
    public static final Capability<ICardData> CARD_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation KEY = new ResourceLocation(CardMod.MODID, "card_data");

    private CardCapability() {}

    public static void onAttach(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;
        LazyOptional<ICardData> holder = LazyOptional.of(CardDataImpl::new);
        event.addCapability(KEY, new ICapabilitySerializable<CompoundTag>() {
            @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) { return CARD_DATA.orEmpty(cap, holder); }
            @Override
            public CompoundTag serializeNBT() {
                return holder.map(data -> ((CardDataImpl) data).serializeNBT()).orElseGet(CompoundTag::new);
            }
            @Override public void deserializeNBT(CompoundTag tag) { holder.ifPresent(data -> ((CardDataImpl) data).deserializeNBT(tag)); }
        });
        event.addListener(holder::invalidate);
    }

    public static void onClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        ICardData orig = event.getOriginal().getCapability(CARD_DATA).orElse(null);
        Map<ResourceLocation, Integer> counts = orig == null ? Map.of() : orig.getAllCounts();
        event.getEntity().getCapability(CARD_DATA).ifPresent(data -> {
            data.clearAll();
            for (var e : counts.entrySet()) data.setCount(e.getKey(), e.getValue());
        });
        event.getOriginal().invalidateCaps();
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { reapply((Player) event.getEntity()); }
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { reapply((Player) event.getEntity()); }

    public static ResourceLocation getSelected(Player player) {
        ICardData data = player.getCapability(CARD_DATA).orElse(null);
        return data == null ? null : data.getSelectedCard();
    }

    public static void setSelected(Player player, ResourceLocation id) {
        player.getCapability(CARD_DATA).ifPresent(data -> data.setSelectedCard(id));
    }

    public static int getCount(Player player, ResourceLocation id) {
        ICardData data = player.getCapability(CARD_DATA).orElse(null);
        return data == null ? 0 : data.getCount(id);
    }

    public static void setCount(Player player, ResourceLocation id, int count) {
        player.getCapability(CARD_DATA).ifPresent(data -> data.setCount(id, count));
    }

    public static void addCount(Player player, ResourceLocation id, int delta) {
        player.getCapability(CARD_DATA).ifPresent(data -> data.addCount(id, delta));
    }

    public static void clearAllCounts(Player player) {
        player.getCapability(CARD_DATA).ifPresent(ICardData::clearAll);
    }

    public static Map<ResourceLocation, Integer> getAllCounts(Player player) {
        ICardData data = player.getCapability(CARD_DATA).orElse(null);
        return data == null ? Map.of() : data.getAllCounts();
    }

    public static long getGoodTimingReadyAt(Player player) {
        ICardData data = player.getCapability(CARD_DATA).orElse(null);
        return data == null ? 0L : data.getGoodTimingReadyAt();
    }

    public static void setGoodTimingReadyAt(Player player, long v) {
        player.getCapability(CARD_DATA).ifPresent(data -> data.setGoodTimingReadyAt(v));
    }

    public static void clearAllModifiers(Player player) {
        for (Card card : CardRegistry.getAll()) card.remove(player);
        float max = player.getMaxHealth();
        if (player.getHealth() > max) player.setHealth(max);
    }

    public static void reapply(Player player) {
        clearAllModifiers(player);
        ICardData data = player.getCapability(CARD_DATA).orElse(null);
        if (data == null) return;
        Map<ResourceLocation, Integer> counts = data.getAllCounts();
        if (counts.isEmpty()) {
            syncIfServer(player);
            return;
        }
        boolean anyValid = false;
        for (var e : counts.entrySet()) {
            Card card = CardRegistry.get(e.getKey());
            if (card == null) continue;
            anyValid = true;
            int c = e.getValue();
            if (c > 0) card.apply(player, c);
        }
        if (!anyValid && !counts.isEmpty()) {
            data.clearAll();
        }
        syncIfServer(player);
    }

    private static void syncIfServer(Player player) {
        if (player instanceof ServerPlayer sp) {
            NetworkHandler.syncTo(sp);
        }
    }
}
