package com.cardmod.card;

public enum Rarity {
    COMMON(60.0),
    UNCOMMON(35.0),
    RARE(25.0),
    LEGENDARY(5.0),
    CURSED(5.0),
    MYTHIC(1.0),
    GODLY(0.05);

    private final double weight;

    Rarity(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
