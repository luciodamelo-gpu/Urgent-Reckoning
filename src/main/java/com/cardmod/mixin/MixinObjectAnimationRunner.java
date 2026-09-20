package com.cardmod.mixin;

import com.cardmod.client.ClientCardCache;
import net.minecraft.resources.ResourceLocation;
import java.util.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.tacz.guns.api.client.animation.ObjectAnimationRunner", remap = false)
public class MixinObjectAnimationRunner {

    @Shadow(remap = false)
    private com.tacz.guns.api.client.animation.ObjectAnimation animation;

    @Unique
    private static final ResourceLocation CARDMOD_QUICK_HANDS = new ResourceLocation("cardmod", "quick_hands");
    @Unique
    private static final ResourceLocation CARDMOD_BRUISED = new ResourceLocation("cardmod", "bruised_hands");
    @Unique
    private static final ResourceLocation CARDMOD_QUICK_DRAW = new ResourceLocation("cardmod", "quick_draw");
    @Unique
    private static final ResourceLocation CARDMOD_SHARPSHOOTER = new ResourceLocation("cardmod", "sharpshooters_grip");
    private static final ResourceLocation CARDMOD_FAST_FINGERS = new ResourceLocation("cardmod", "fast_fingers");
    @ModifyArg(
            method = "update",
            at = @At(value = "INVOKE",
                    target = "Lcom/tacz/guns/api/client/animation/ObjectAnimationRunner;updateProgress(J)V"),
            remap = false)
    private long cardmod$scaleReloadProgress(long delta) {
        if (animation == null || animation.name == null) return delta;
        String n = animation.name.toLowerCase(Locale.ROOT);
        if (!n.contains("reload")) return delta;
        int qh = ClientCardCache.getCounts().getOrDefault(CARDMOD_QUICK_HANDS, 0);
        int bh = ClientCardCache.getCounts().getOrDefault(CARDMOD_BRUISED, 0);
        int qd = ClientCardCache.getCounts().getOrDefault(CARDMOD_QUICK_DRAW, 0);
        int sg = ClientCardCache.getCounts().getOrDefault(CARDMOD_SHARPSHOOTER, 0);
        int ff = ClientCardCache.getCounts().getOrDefault(CARDMOD_FAST_FINGERS, 0);
        if (qh == 0 && bh == 0 && qd == 0 && sg == 0 && ff == 0) return delta;
        double factor = 1.0 + 0.04 * qh - 0.04 * bh + 0.10 * qd + 0.05 * sg + 0.05 * ff;
        if (factor <= 0.05) factor = 0.05;
        return (long)(delta * factor);
    }
}
