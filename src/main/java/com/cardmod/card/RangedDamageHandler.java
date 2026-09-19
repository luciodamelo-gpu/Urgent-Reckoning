package com.cardmod.card;

import com.cardmod.capability.CardCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class RangedDamageHandler {
    private RangedDamageHandler() {}

    public static void onHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        Player player = DamageHelper.playerAttacker(source);
        if (player == null) return;
        Entity direct = source.getDirectEntity();
        if (direct == null || direct == player) return;
        String directId = direct.getType().builtInRegistryHolder().key().location().toString().toLowerCase();
        String directName = direct.getType().toString().toLowerCase();
        boolean isArrow = directId.contains("arrow") || directName.contains("arrow");
        boolean isTacz = directId.contains("tacz") || directId.contains("bullet") || directId.contains("gun")
                || directName.contains("tacz") || directName.contains("bullet");
        boolean isProjectile = directId.contains("projectile") || directName.contains("projectile");
        float bonus = 0f;
        ResourceLocation lf = new ResourceLocation("cardmod","light_focus");
        ResourceLocation ke = new ResourceLocation("cardmod","keen_eyes");
        ResourceLocation cs = new ResourceLocation("cardmod","chainsmoker");
        ResourceLocation bh = new ResourceLocation("cardmod","bruised_hands");
        ResourceLocation he = new ResourceLocation("cardmod","hunters_eye");
        int lfCount = CardCapability.getCount(player, lf);
        int keCount = CardCapability.getCount(player, ke);
        int csCount = CardCapability.getCount(player, cs);
        int bhCount = CardCapability.getCount(player, bh);
        int heCount = CardCapability.getCount(player, he);
        if (lfCount > 0) bonus += 0.04f * lfCount;
        if (keCount > 0) bonus += 0.10f * keCount;
        if (csCount > 0) bonus += 0.50f * csCount;
        if (bhCount > 0) bonus += 0.06f * bhCount;
        if (heCount > 0) bonus += 0.08f * heCount;
        if (bonus == 0) {
            ResourceLocation sel = CardCapability.getSelected(player);
            if (sel != null) {
                String p = sel.getPath();
                if ("light_focus".equals(p)) bonus = 0.04f;
                else if ("keen_eyes".equals(p)) bonus = 0.10f;
            }
        }
        if (bonus == 0) return;

        if (isTacz) { event.setAmount(event.getAmount() * (1f + bonus)); return; }
        if (isArrow || isProjectile) {
            if (hasApothicArrowDamage()) return;
            event.setAmount(event.getAmount() * (1f + bonus)); return;
        }
        if (direct instanceof LivingEntity) return;
        event.setAmount(event.getAmount() * (1f + bonus));
    }

    private static boolean hasApothicArrowDamage() {
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("attributeslib", "arrow_damage"));
        return attr != null;
    }
}
