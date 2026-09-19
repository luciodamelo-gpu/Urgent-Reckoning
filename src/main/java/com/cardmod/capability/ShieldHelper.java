package com.cardmod.capability;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ShieldHelper {
    private ShieldHelper() {}

    public static boolean isShielding(Player player) {
        if (player.isBlocking()) return true;
        if (isHoldingTaczGun(player)) return GunShieldState.isHeld(player.getUUID());
        return GunShieldState.isBlockHeld(player.getUUID());
    }

    public static boolean isShieldingClient(Player player) {
        if (player.isBlocking()) return true;
        if (isHoldingTaczGun(player)) return GunShieldState.isHeld(player.getUUID());
        return GunShieldState.isBlockHeld(player.getUUID());
    }

    public static boolean isHoldingTaczGun(Player player) {
        String mainId = ForgeRegistries.ITEMS.getKey(player.getMainHandItem().getItem()).toString();
        String offId = ForgeRegistries.ITEMS.getKey(player.getOffhandItem().getItem()).toString();
        return mainId.contains("tacz") || offId.contains("tacz");
    }

    public static final class GunShieldState {
        private static final Map<UUID, Long> HELD_UNTIL = new ConcurrentHashMap<>();
        private static final Map<UUID, Long> BLOCK_UNTIL = new ConcurrentHashMap<>();

        private GunShieldState() {}

        public static void hold(UUID id) {
            HELD_UNTIL.put(id, System.currentTimeMillis() + 1500L);
        }

        public static void release(UUID id) {
            HELD_UNTIL.remove(id);
        }

        public static boolean isHeld(UUID id) {
            return check(HELD_UNTIL, id);
        }

        public static void holdBlock(UUID id) {
            BLOCK_UNTIL.put(id, System.currentTimeMillis() + 1500L);
        }

        public static void releaseBlock(UUID id) {
            BLOCK_UNTIL.remove(id);
        }

        public static boolean isBlockHeld(UUID id) {
            return check(BLOCK_UNTIL, id);
        }

        private static boolean check(Map<UUID, Long> map, UUID id) {
            Long until = map.get(id);
            if (until == null) return false;
            if (until < System.currentTimeMillis()) {
                map.remove(id);
                return false;
            }
            return true;
        }
    }
}
