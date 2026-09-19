package com.cardmod.client;

import com.cardmod.CardMod;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientCardCache {
    private static final Map<ResourceLocation, Integer> COUNTS = new ConcurrentHashMap<>();
    private static volatile long goodTimingReadyAt = 0L;
    private static final ResourceLocation GOOD_TIMING = new ResourceLocation("cardmod", "good_timing");

    private ClientCardCache() {}

    public static void update(Map<ResourceLocation, Integer> counts, long readyAt) {
        COUNTS.clear();
        COUNTS.putAll(counts);
        goodTimingReadyAt = readyAt;
        CardMod.LOGGER.info("CARDMOD sync received {} cards", counts.size());
    }

    public static Map<ResourceLocation, Integer> getCounts() {
        return Map.copyOf(COUNTS);
    }

    public static boolean hasGoodTiming() {
        return COUNTS.getOrDefault(GOOD_TIMING, 0) > 0;
    }

    public static float shieldFraction() {
        long remaining = goodTimingReadyAt - System.currentTimeMillis();
        if (remaining <= 0) return 1f;
        return 1f - Math.min(1f, remaining / 10_000f);
    }

    public static boolean shieldReady() {
        return System.currentTimeMillis() >= goodTimingReadyAt;
    }
}
