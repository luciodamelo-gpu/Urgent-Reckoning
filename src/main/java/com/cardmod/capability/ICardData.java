package com.cardmod.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.Map;

@AutoRegisterCapability
public interface ICardData {
    ResourceLocation getSelectedCard();
    void setSelectedCard(ResourceLocation id);

    Map<ResourceLocation, Integer> getAllCounts();
    int getCount(ResourceLocation id);
    void setCount(ResourceLocation id, int count);
    default void addCount(ResourceLocation id, int delta) {
        int cur = getCount(id);
        setCount(id, cur + delta);
    }
    void clearAll();

    long getGoodTimingReadyAt();
    void setGoodTimingReadyAt(long v);
}
