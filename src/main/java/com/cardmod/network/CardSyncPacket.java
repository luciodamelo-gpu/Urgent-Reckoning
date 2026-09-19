package com.cardmod.network;

import com.cardmod.client.ClientCardCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CardSyncPacket {
    private final Map<ResourceLocation, Integer> counts;
    private final long goodTimingReadyAt;

    public CardSyncPacket(Map<ResourceLocation, Integer> counts, long goodTimingReadyAt) {
        this.counts = Map.copyOf(counts);
        this.goodTimingReadyAt = goodTimingReadyAt;
    }

    public CardSyncPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Map<ResourceLocation, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) map.put(buf.readResourceLocation(), buf.readVarInt());
        this.counts = Map.copyOf(map);
        this.goodTimingReadyAt = buf.readLong();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(counts.size());
        for (var e : counts.entrySet()) {
            buf.writeResourceLocation(e.getKey());
            buf.writeVarInt(e.getValue());
        }
        buf.writeLong(goodTimingReadyAt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            Map<ResourceLocation, Integer> copy = Map.copyOf(counts);
            long ready = goodTimingReadyAt;
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientCardCache.update(copy, ready)));
        }
        context.setPacketHandled(true);
    }
}
