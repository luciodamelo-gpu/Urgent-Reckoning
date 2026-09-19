package com.cardmod.network;

import com.cardmod.CardMod;
import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class NetworkHandler {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CardMod.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private NetworkHandler() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, OpenCardScreenPacket.class,
                OpenCardScreenPacket::encode, OpenCardScreenPacket::new, OpenCardScreenPacket::handle);
        CHANNEL.registerMessage(id++, SelectCardPacket.class,
                SelectCardPacket::encode, SelectCardPacket::new, SelectCardPacket::handle);
        CHANNEL.registerMessage(id++, CardSyncPacket.class,
                CardSyncPacket::encode, CardSyncPacket::new, CardSyncPacket::handle);
        CHANNEL.registerMessage(id++, ShieldKeyPacket.class,
                ShieldKeyPacket::encode, ShieldKeyPacket::new, ShieldKeyPacket::handle);
    }

    public static void syncTo(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new CardSyncPacket(CardCapability.getAllCounts(player), CardCapability.getGoodTimingReadyAt(player)));
    }
}
