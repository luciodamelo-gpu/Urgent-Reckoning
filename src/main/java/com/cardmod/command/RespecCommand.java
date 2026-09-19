package com.cardmod.command;

import com.cardmod.CardMod;
import com.cardmod.capability.CardCapability;
import com.cardmod.card.Card;
import com.cardmod.card.Rarity;
import com.cardmod.network.NetworkHandler;
import com.cardmod.network.OpenCardScreenPacket;
import com.cardmod.registry.CardRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class RespecCommand {
    private static final Map<UUID, List<ResourceLocation>> OFFERED = new ConcurrentHashMap<>();
    private RespecCommand() {}
    public static List<ResourceLocation> getOffered(UUID id) { return OFFERED.get(id); }
    public static void clearOffered(UUID id) { OFFERED.remove(id); }

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("respec")
                .requires(s -> s.hasPermission(0))
                .executes(ctx -> respecSelf(ctx))
                .then(Commands.argument("rarity", StringArgumentType.word())
                        .suggests(RespecCommand::suggestRarity)
                        .requires(s -> s.hasPermission(2))
                        .executes(ctx -> respecRaritySelf(ctx))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> respecRarityTargets(ctx)))))
                ;
    }

    private static CompletableFuture<Suggestions> suggestRarity(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder b) {
        for (String s : List.of("common","uncommon","rare","legendary","leg","cursed","mythic","godly")) b.suggest(s);
        return b.buildFuture();
    }

    private static Rarity parseRarity(String raw) {
        String s = raw.toLowerCase(Locale.ROOT);
        return switch (s) {
            case "common" -> Rarity.COMMON;
            case "uncommon" -> Rarity.UNCOMMON;
            case "rare" -> Rarity.RARE;
            case "legendary", "leg" -> Rarity.LEGENDARY;
            case "cursed" -> Rarity.CURSED;
            case "mythic" -> Rarity.MYTHIC;
            case "godly" -> Rarity.GODLY;
            default -> null;
        };
    }

    private static int respecSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        return doRespec(List.of(p));
    }

    private static int respecRaritySelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        String raw = StringArgumentType.getString(ctx, "rarity");
        Rarity r = parseRarity(raw);
        if (r == null) { ctx.getSource().sendFailure(Component.literal("Unknown rarity: " + raw)); return 0; }
        return doRespecRarity(List.of(p), r);
    }

    private static int respecRarityTargets(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String raw = StringArgumentType.getString(ctx, "rarity");
        Rarity r = parseRarity(raw);
        if (r == null) { ctx.getSource().sendFailure(Component.literal("Unknown rarity: " + raw)); return 0; }
        Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "targets");
        return doRespecRarity(players, r);
    }

    private static int doRespec(Collection<ServerPlayer> players) {
        for (ServerPlayer p : players) {
            CardCapability.clearAllModifiers(p);
            CardCapability.clearAllCounts(p);
            List<Card> offer = CardRegistry.getRandomOffer(p.getRandom(), 3);
            List<ResourceLocation> ids = offer.stream().map(Card::getId).toList();
            CardMod.LOGGER.info("CARDMOD respec offer for {}: {}", p.getName().getString(), ids);
            OFFERED.put(p.getUUID(), ids);
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), new OpenCardScreenPacket(ids));
        }
        return players.size();
    }

    private static int doRespecRarity(Collection<ServerPlayer> players, Rarity rarity) {
        for (ServerPlayer p : players) {
            CardCapability.clearAllModifiers(p);
            CardCapability.clearAllCounts(p);
            List<Card> offer = CardRegistry.getRandomOfferByRarity(p.getRandom(), rarity, 3);
            if (offer.isEmpty()) offer = CardRegistry.getRandomOffer(p.getRandom(), 3);
            List<ResourceLocation> ids = offer.stream().map(Card::getId).toList();
            OFFERED.put(p.getUUID(), ids);
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), new OpenCardScreenPacket(ids));
        }
        return players.size();
    }
}
