package com.cardmod.card;

import com.cardmod.CardMod;
import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class TaczReloadHandler {
    private static final ResourceLocation QUICK_HANDS = new ResourceLocation("cardmod", "quick_hands");
    private static final ResourceLocation BRUISED = new ResourceLocation("cardmod", "bruised_hands");
    private static final ResourceLocation QUICK_DRAW = new ResourceLocation("cardmod", "quick_draw");
    private static final ResourceLocation SHARPSHOOTER = new ResourceLocation("cardmod", "sharpshooters_grip");
    private static Boolean present = null;
    private static boolean wired = false;
    private static boolean wireLogged = false;
    private static boolean shiftLogged = false;
    private static Method fromLivingEntity = null;
    private static Method getDataHolder = null;
    private static Field reloadStateTypeField = null;
    private static Field reloadTimestampField = null;
    private static Method isReloading = null;
    private TaczReloadHandler() {}
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;
        int qh = CardCapability.getCount(player, QUICK_HANDS);
        int bh = CardCapability.getCount(player, BRUISED);
        int qd = CardCapability.getCount(player, QUICK_DRAW);
        int sg = CardCapability.getCount(player, SHARPSHOOTER);
        int netPercent = qh*4 - bh*4 + qd*10 + sg*5;
        int net = netPercent;
        if (net == 0) return;
        accelerate(player, net, true);
    }
    public static void accelerate(Player player, int net, boolean log) {
        if (net == 0) return;
        if (!wire()) {
            if (!wireLogged) { wireLogged = true; CardMod.LOGGER.warn("CARDMOD tacz reload: TACZ API not available"); }
            return;
        }
        try {
            Object op = fromLivingEntity.invoke(null, player);
            Object holder = getDataHolder.invoke(op);
            Object stateType = reloadStateTypeField.get(holder);
            if (stateType == null || !(Boolean) isReloading.invoke(stateType)) return;
            long ts = reloadTimestampField.getLong(holder);
            long shift = Math.round(net * 0.5);
            reloadTimestampField.setLong(holder, ts - shift);
            if (log && !shiftLogged) {
                shiftLogged = true;
                CardMod.LOGGER.info("CARDMOD tacz reload accelerating x{} for {}", net, player.getName().getString());
            }
        } catch (Exception ignored) {}
    }
    private static boolean wire() {
        if (present == null) present = ModList.get().isLoaded("tacz");
        if (!present) return false;
        if (wired) return fromLivingEntity != null;
        wired = true;
        try {
            Class<?> ig = Class.forName("com.tacz.guns.api.entity.IGunOperator");
            fromLivingEntity = ig.getMethod("fromLivingEntity", LivingEntity.class);
            getDataHolder = ig.getMethod("getDataHolder");
            Class<?> holderCls = Class.forName("com.tacz.guns.entity.shooter.ShooterDataHolder");
            reloadStateTypeField = holderCls.getField("reloadStateType");
            reloadTimestampField = holderCls.getField("reloadTimestamp");
            Class<?> stateCls = Class.forName("com.tacz.guns.api.entity.ReloadState$StateType");
            isReloading = stateCls.getMethod("isReloading");
        } catch (Exception e) { fromLivingEntity = null; }
        return fromLivingEntity != null;
    }
}
