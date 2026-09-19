package com.cardmod.card;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

public final class DamageHelper {
    private DamageHelper() {}

    public static Player playerAttacker(DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker instanceof Player p) return p;
        Entity direct = source.getDirectEntity();
        if (direct instanceof Projectile proj && proj.getOwner() instanceof Player p) return p;
        return null;
    }
}
