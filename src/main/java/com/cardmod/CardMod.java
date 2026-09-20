package com.cardmod;

import com.cardmod.capability.CardCapability;
import com.cardmod.card.BlockHandler;
import com.cardmod.card.DamageReductionHandler;
import com.cardmod.card.RangedDamageHandler;
import com.cardmod.card.AdrenalineHandler;
import com.cardmod.card.BloodRushHandler;
import com.cardmod.card.CleanHitHandler;
import com.cardmod.card.SoftLandingHandler;
import com.cardmod.card.ShieldStunHandler;
import com.cardmod.card.SpringyTendonsHandler;
import com.cardmod.card.SixteenHandler;
import com.cardmod.card.TaczReloadHandler;
import com.cardmod.command.CardAdminCommand;
import com.cardmod.command.RespecCommand;
import com.cardmod.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CardMod.MODID)
public class CardMod {
    public static final String MODID = "cardmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CardMod() {
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CardCapability::onAttach);
        MinecraftForge.EVENT_BUS.addListener(CardCapability::onClone);
        MinecraftForge.EVENT_BUS.addListener(CardCapability::onLogin);
        MinecraftForge.EVENT_BUS.addListener(CardCapability::onRespawn);
        MinecraftForge.EVENT_BUS.addListener(RangedDamageHandler::onHurt);
        MinecraftForge.EVENT_BUS.addListener(DamageReductionHandler::onHurt);
        MinecraftForge.EVENT_BUS.addListener(SixteenHandler::onHurt);
        MinecraftForge.EVENT_BUS.addListener(SixteenHandler::onLogout);
        MinecraftForge.EVENT_BUS.addListener(BlockHandler::onHurt);
        MinecraftForge.EVENT_BUS.addListener(SpringyTendonsHandler::onJump);
        MinecraftForge.EVENT_BUS.addListener(BloodRushHandler::onDamage);
        MinecraftForge.EVENT_BUS.addListener(BloodRushHandler::onTick);
        MinecraftForge.EVENT_BUS.addListener(SoftLandingHandler::onHurt);
        MinecraftForge.EVENT_BUS.addListener(AdrenalineHandler::onTick);
        MinecraftForge.EVENT_BUS.addListener(CleanHitHandler::onHurt);
        MinecraftForge.EVENT_BUS.addListener(TaczReloadHandler::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(net.minecraftforge.eventbus.api.EventPriority.LOWEST, false, ShieldStunHandler::onDamage);
        MinecraftForge.EVENT_BUS.addListener(RespecCommand::register);
        MinecraftForge.EVENT_BUS.addListener(CardAdminCommand::register);
    }
}
