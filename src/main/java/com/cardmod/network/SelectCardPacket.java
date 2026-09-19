package com.cardmod.network;

import com.cardmod.CardMod;
import com.cardmod.capability.CardCapability;
import com.cardmod.card.Card;
import com.cardmod.command.RespecCommand;
import com.cardmod.registry.CardRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;

import java.util.function.Supplier;

public class SelectCardPacket {
    private final ResourceLocation cardId;

    public SelectCardPacket(ResourceLocation cardId) {
        this.cardId = cardId;
    }

    public SelectCardPacket(FriendlyByteBuf buf) {
        this.cardId = buf.readResourceLocation();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(cardId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            Card card = CardRegistry.get(cardId);
            if (card == null) {
                CardMod.LOGGER.warn("Ignoring unknown card selection: {}", cardId);
                return;
            }
            List<ResourceLocation> offered = RespecCommand.getOffered(player.getUUID());
            if (offered != null && !offered.contains(cardId)) {
                CardMod.LOGGER.warn("Ignoring card not offered: {} not in {}", cardId, offered);
                return;
            }
            RespecCommand.clearOffered(player.getUUID());
            CardCapability.clearAllModifiers(player);
            CardCapability.setSelected(player, cardId);
            card.apply(player);
            NetworkHandler.syncTo(player);
            player.displayClientMessage(Component.literal("Card applied: " + card.getDisplayName()), false);
        });
        context.setPacketHandled(true);
    }
}
