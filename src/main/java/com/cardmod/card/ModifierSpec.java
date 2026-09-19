package com.cardmod.card;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;
import java.util.function.Supplier;

public record ModifierSpec(Supplier<Attribute> attribute, AttributeModifier.Operation operation, double amount, UUID uuid) {
    public AttributeModifier toModifier(String cardId) {
        return new AttributeModifier(uuid, "cardmod:" + cardId, amount, operation);
    }
}
