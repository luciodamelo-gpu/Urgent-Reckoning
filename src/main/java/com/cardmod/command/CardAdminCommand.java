package com.cardmod.command;

import com.cardmod.capability.CardCapability;
import com.cardmod.card.Card;
import com.cardmod.registry.CardRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class CardAdminCommand {
    private CardAdminCommand() {}

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("card").requires(s -> s.hasPermission(2))
                .then(Commands.literal("clear")
                        .executes(ctx -> clearSelfAll(ctx))
                        .then(Commands.argument("card", StringArgumentType.word()).suggests(CardAdminCommand::suggest)
                                .executes(ctx -> clearSelfOne(ctx, 1))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                        .executes(ctx -> clearSelfOne(ctx, IntegerArgumentType.getInteger(ctx, "count"))))))
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("give")
                                .then(Commands.argument("card", StringArgumentType.word()).suggests(CardAdminCommand::suggest)
                                        .executes(ctx -> giveTargets(ctx, 1))
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes(ctx -> giveTargets(ctx, IntegerArgumentType.getInteger(ctx, "count"))))))
                        .then(Commands.literal("clear")
                                .executes(ctx -> clearTargetsAll(ctx))
                                .then(Commands.argument("card", StringArgumentType.word()).suggests(CardAdminCommand::suggest)
                                        .executes(ctx -> clearTargetsOne(ctx, 1))
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes(ctx -> clearTargetsOne(ctx, IntegerArgumentType.getInteger(ctx, "count")))))))
        );
    }

    private static CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder b) {
        for (Card c : CardRegistry.getAll()) b.suggest(c.getId().getPath());
        return b.buildFuture();
    }

    private static ResourceLocation parseCard(String raw) {
        if (raw.contains(":")) return new ResourceLocation(raw);
        return new ResourceLocation("cardmod", raw);
    }

    private static int giveTargets(CommandContext<CommandSourceStack> ctx, int count) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "targets");
        ResourceLocation id = parseCard(StringArgumentType.getString(ctx, "card"));
        return givePlayers(players, id, count, ctx.getSource());
    }
    private static int givePlayers(Collection<ServerPlayer> players, ResourceLocation id, int count, CommandSourceStack src) {
        Card card = CardRegistry.get(id);
        if (card == null) { src.sendFailure(Component.literal("Unknown card: " + id)); return 0; }
        for (ServerPlayer p : players) {
            CardCapability.addCount(p, id, count);
            CardCapability.clearAllModifiers(p);
            CardCapability.reapply(p);
        }
        String msg = "Gave " + count + "x " + card.getDisplayName() + " to " + players.size() + " player(s)";
        src.sendSuccess(() -> Component.literal(msg), true);
        return players.size();
    }

    private static int clearSelfAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        return clearPlayersAll(List.of(p), ctx.getSource());
    }
    private static int clearTargetsAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "targets");
        return clearPlayersAll(players, ctx.getSource());
    }
    private static int clearPlayersAll(Collection<ServerPlayer> players, CommandSourceStack src) {
        for (ServerPlayer p : players) {
            CardCapability.clearAllCounts(p);
            CardCapability.clearAllModifiers(p);
            CardCapability.reapply(p);
        }
        src.sendSuccess(() -> Component.literal("Cleared all cards from " + players.size() + " player(s)"), true);
        return players.size();
    }

    private static int clearSelfOne(CommandContext<CommandSourceStack> ctx, int count) throws CommandSyntaxException {
        ServerPlayer p = ctx.getSource().getPlayerOrException();
        ResourceLocation id = parseCard(StringArgumentType.getString(ctx, "card"));
        return clearPlayersOne(List.of(p), id, count, ctx.getSource());
    }
    private static int clearTargetsOne(CommandContext<CommandSourceStack> ctx, int count) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "targets");
        ResourceLocation id = parseCard(StringArgumentType.getString(ctx, "card"));
        return clearPlayersOne(players, id, count, ctx.getSource());
    }
    private static int clearPlayersOne(Collection<ServerPlayer> players, ResourceLocation id, int count, CommandSourceStack src) {
        Card card = CardRegistry.get(id);
        if (card == null) { src.sendFailure(Component.literal("Unknown card: " + id)); return 0; }
        for (ServerPlayer p : players) {
            int cur = CardCapability.getCount(p, id);
            if (cur <= 0) continue;
            int next = Math.max(0, cur - count);
            CardCapability.setCount(p, id, next);
            CardCapability.clearAllModifiers(p);
            CardCapability.reapply(p);
        }
        String msg = "Removed " + count + "x " + card.getDisplayName() + " from " + players.size() + " player(s)";
        src.sendSuccess(() -> Component.literal(msg), true);
        return players.size();
    }
}
