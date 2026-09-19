package com.cardmod.card;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class Card {
    private final ResourceLocation id;
    private final String displayName;
    private final String description;
    private final Rarity rarity;
    private final List<ModifierSpec> modifiers;

    public Card(ResourceLocation id, String displayName, String description, Rarity rarity, List<ModifierSpec> modifiers) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.rarity = rarity;
        this.modifiers = List.copyOf(modifiers);
    }

    public ResourceLocation getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public Rarity getRarity() { return rarity; }
    public List<ModifierSpec> getModifiers() { return modifiers; }

    public void apply(Player player) { apply(player, 1); }

    public void apply(Player player, int count) {
        if (count <= 0) { remove(player); return; }
        for (ModifierSpec spec : modifiers) {
            var attr = spec.attribute().get();
            if (attr == null) continue;
            AttributeInstance instance = player.getAttribute(attr);
            if (instance == null) continue;
            instance.removeModifier(spec.uuid());
            double scaled = spec.amount() * count;
            AttributeModifier mod = new AttributeModifier(spec.uuid(), "cardmod:" + id.getPath(), scaled, spec.operation());
            instance.addTransientModifier(mod);
        }
    }

    public void remove(Player player) {
        for (ModifierSpec spec : modifiers) {
            var attr = spec.attribute().get();
            if (attr == null) continue;
            AttributeInstance instance = player.getAttribute(attr);
            if (instance == null) continue;
            instance.removeModifier(spec.uuid());
        }
    }
}
