package com.cardmod.network;

import com.cardmod.client.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OpenCardScreenPacket {
    private final List<ResourceLocation> offered;

    public OpenCardScreenPacket(List<ResourceLocation> offered) {
        this.offered = List.copyOf(offered);
    }

    public OpenCardScreenPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<ResourceLocation> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) list.add(buf.readResourceLocation());
        this.offered = List.copyOf(list);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(offered.size());
        for (ResourceLocation id : offered) buf.writeResourceLocation(id);
    }

    public List<ResourceLocation> getOffered() { return offered; }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            List<ResourceLocation> copy = List.copyOf(offered);
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.openScreen(copy)));
        }
        context.setPacketHandled(true);
    }
}
