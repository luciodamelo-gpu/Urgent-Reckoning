package com.cardmod.client;

import com.cardmod.CardMod;
import com.cardmod.capability.ShieldHelper;
import com.cardmod.card.TaczReloadHandler;
import com.cardmod.network.NetworkHandler;
import com.cardmod.network.ShieldKeyPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = CardMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientForgeEvents {
    private static boolean taczActive = false;
    private static boolean blockActive = false;
    private static int ticks = 0;
    private static final ResourceLocation QUICK_HANDS_ID = new ResourceLocation("cardmod", "quick_hands");
    private static final ResourceLocation BRUISED_ID = new ResourceLocation("cardmod", "bruised_hands");
    private static final ResourceLocation QUICK_DRAW_ID = new ResourceLocation("cardmod", "quick_draw");
    private static final ResourceLocation SHARPSHOOTER_ID = new ResourceLocation("cardmod", "sharpshooters_grip");
    private static final ResourceLocation FAST_FINGERS_ID = new ResourceLocation("cardmod", "fast_fingers");

    private ClientForgeEvents() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            taczActive = false;
            blockActive = false;
            return;
        }
        boolean holdingGun = ShieldHelper.isHoldingTaczGun(mc.player);
        boolean wantTacz = holdingGun && ShieldKeybinds.isTaczShieldDown();
        boolean wantBlock = !holdingGun && ShieldKeybinds.isBlockDown();
        ticks++;
        int qh = ClientCardCache.getCounts().getOrDefault(QUICK_HANDS_ID, 0);
        int bh = ClientCardCache.getCounts().getOrDefault(BRUISED_ID, 0);
        int qd = ClientCardCache.getCounts().getOrDefault(QUICK_DRAW_ID, 0);
        int sg = ClientCardCache.getCounts().getOrDefault(SHARPSHOOTER_ID, 0);
        int ff = ClientCardCache.getCounts().getOrDefault(FAST_FINGERS_ID, 0);
        int netPercent = qh * 4 - bh * 4 + qd * 10 + sg * 5 + ff * 5;
        if (netPercent != 0) {
            TaczReloadHandler.accelerate(mc.player, netPercent);
        }
        if (wantTacz) {
            if (!taczActive || ticks % 10 == 0) {
                NetworkHandler.CHANNEL.sendToServer(new ShieldKeyPacket(ShieldKeyPacket.KEY_TACZ, true));
                ShieldHelper.GunShieldState.hold(mc.player.getUUID());
                taczActive = true;
            }
        } else if (taczActive) {
            NetworkHandler.CHANNEL.sendToServer(new ShieldKeyPacket(ShieldKeyPacket.KEY_TACZ, false));
            ShieldHelper.GunShieldState.release(mc.player.getUUID());
            taczActive = false;
        }
        if (wantBlock) {
            if (!blockActive || ticks % 10 == 0) {
                NetworkHandler.CHANNEL.sendToServer(new ShieldKeyPacket(ShieldKeyPacket.KEY_BLOCK, true));
                ShieldHelper.GunShieldState.holdBlock(mc.player.getUUID());
                blockActive = true;
            }
        } else if (blockActive) {
            NetworkHandler.CHANNEL.sendToServer(new ShieldKeyPacket(ShieldKeyPacket.KEY_BLOCK, false));
            ShieldHelper.GunShieldState.releaseBlock(mc.player.getUUID());
            blockActive = false;
        }
    }
}
