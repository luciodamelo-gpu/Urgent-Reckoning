package com.cardmod.network;

import com.cardmod.capability.ShieldHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShieldKeyPacket {
    public static final byte KEY_TACZ = 0;
    public static final byte KEY_BLOCK = 1;

    private final byte key;
    private final boolean down;

    public ShieldKeyPacket(byte key, boolean down) {
        this.key = key;
        this.down = down;
    }

    public ShieldKeyPacket(boolean down) {
        this(KEY_TACZ, down);
    }

    public ShieldKeyPacket(FriendlyByteBuf buf) {
        this.key = buf.readByte();
        this.down = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(key);
        buf.writeBoolean(down);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            if (key == KEY_BLOCK) {
                if (down) ShieldHelper.GunShieldState.holdBlock(player.getUUID());
                else ShieldHelper.GunShieldState.releaseBlock(player.getUUID());
            } else {
                if (down) ShieldHelper.GunShieldState.hold(player.getUUID());
                else ShieldHelper.GunShieldState.release(player.getUUID());
            }
        });
        context.setPacketHandled(true);
    }
}
