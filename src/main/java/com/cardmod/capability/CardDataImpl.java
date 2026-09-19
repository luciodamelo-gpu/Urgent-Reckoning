package com.cardmod.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CardDataImpl implements ICardData, INBTSerializable<CompoundTag> {
    private final Map<ResourceLocation, Integer> counts = new LinkedHashMap<>();
    private long goodTimingReadyAt = 0L;

    @Override
    public long getGoodTimingReadyAt() { return goodTimingReadyAt; }

    @Override
    public void setGoodTimingReadyAt(long v) { this.goodTimingReadyAt = v; }

    @Override
    public ResourceLocation getSelectedCard() {
        for (Map.Entry<ResourceLocation, Integer> e : counts.entrySet()) {
            if (e.getValue() > 0) return e.getKey();
        }
        return null;
    }

    @Override
    public void setSelectedCard(ResourceLocation id) {
        counts.clear();
        if (id != null) counts.put(id, 1);
    }

    @Override
    public Map<ResourceLocation, Integer> getAllCounts() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(counts));
    }

    @Override
    public int getCount(ResourceLocation id) {
        return counts.getOrDefault(id, 0);
    }

    @Override
    public void setCount(ResourceLocation id, int count) {
        if (count <= 0) counts.remove(id);
        else counts.put(id, count);
    }

    @Override
    public void clearAll() {
        counts.clear();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (!counts.isEmpty()) {
            CompoundTag map = new CompoundTag();
            for (Map.Entry<ResourceLocation, Integer> e : counts.entrySet()) {
                map.putInt(e.getKey().toString(), e.getValue());
            }
            tag.put("CardCounts", map);
            ResourceLocation first = getSelectedCard();
            if (first != null) tag.putString("SelectedCard", first.toString());
        }
        if (goodTimingReadyAt > 0) {
            tag.putLong("GoodTimingReadyAt", goodTimingReadyAt);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        counts.clear();
        goodTimingReadyAt = tag.contains("GoodTimingReadyAt", Tag.TAG_LONG) ? tag.getLong("GoodTimingReadyAt") : 0L;
        if (tag.contains("CardCounts", Tag.TAG_COMPOUND)) {
            CompoundTag map = tag.getCompound("CardCounts");
            for (String key : map.getAllKeys()) {
                try {
                    ResourceLocation id = new ResourceLocation(key);
                    int c = map.getInt(key);
                    if (c > 0) counts.put(id, c);
                } catch (Exception ignored) {}
            }
        } else if (tag.contains("SelectedCard")) {
            try {
                String raw = tag.getString("SelectedCard");
                if (!raw.isEmpty()) {
                    ResourceLocation id = new ResourceLocation(raw);
                    counts.put(id, 1);
                }
            } catch (Exception ignored) {}
        }
    }
}
