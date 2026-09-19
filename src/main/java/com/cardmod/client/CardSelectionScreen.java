package com.cardmod.client;

import com.cardmod.card.Card;
import com.cardmod.card.Rarity;
import com.cardmod.network.NetworkHandler;
import com.cardmod.network.SelectCardPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CardSelectionScreen extends Screen {
    private static final int CARD_SIZE = 70;
    private static final int GAP = 12;
    private static final int COLS = 4;

    private final List<Card> cards;
    private final List<PlacedCard> placed = new ArrayList<>();
    private final List<Sparkle> sparkles = new ArrayList<>();

    public CardSelectionScreen(List<Card> cards) {
        super(Component.literal("Choose a card"));
        this.cards = List.copyOf(cards);
    }

    public CardSelectionScreen() {
        this(List.of());
    }

    @Override
    protected void init() {
        placed.clear();
        sparkles.clear();
        if (cards.isEmpty()) return;
        int cols = Math.min(COLS, cards.size());
        int rows = (int) Math.ceil(cards.size() / (double) cols);
        int totalHeight = rows * CARD_SIZE + (rows - 1) * GAP;
        int startY = (this.height - totalHeight) / 2;
        for (int i = 0; i < cards.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int lastRowCols = (row == rows - 1) ? (cards.size() - (rows - 1) * cols) : cols;
            int rowWidth = lastRowCols * CARD_SIZE + (lastRowCols - 1) * GAP;
            int rowStartX = (this.width - rowWidth) / 2;
            int x = rowStartX + col * (CARD_SIZE + GAP);
            int y = startY + row * (CARD_SIZE + GAP);
            placed.add(new PlacedCard(cards.get(i), x, y));
        }
    }

    @Override
    public void tick() {
        super.tick();
        Iterator<Sparkle> it = sparkles.iterator();
        while (it.hasNext()) {
            Sparkle s = it.next();
            s.x += s.vx;
            s.y += s.vy;
            s.age++;
            if (s.age >= s.maxAge || s.y > s.baseY + CARD_SIZE + 6) it.remove();
        }
        for (PlacedCard p : placed) {
            Rarity rarity = p.card.getRarity();
            if (rarity != Rarity.MYTHIC && rarity != Rarity.GODLY) continue;
            double chance = rarity == Rarity.GODLY ? 0.3 : 0.18;
            if (Math.random() >= chance) continue;
            {
                    float sx = p.x + 2 + (float) Math.random() * (CARD_SIZE - 4);
                    float sy = p.y + 2;
                    float vx = (float) (Math.random() - 0.5) * 0.4f;
                    float vy = 0.5f + (float) Math.random() * 0.8f;
                    sparkles.add(new Sparkle(sx, sy, vx, vy, sy));
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        if (placed.isEmpty()) { super.render(graphics, mouseX, mouseY, partialTick); return; }
        int titleY = placed.get(0).y - 24;
        graphics.drawCenteredString(this.font, this.title, this.width / 2, titleY, 0xFFFFFF);
        for (PlacedCard slot : placed) {
            boolean hovered = slot.isHovered(mouseX, mouseY);
            int outline = getOutlineColor(slot.card, hovered);
            int fill = getFillColor(slot.card, hovered);
            graphics.fill(slot.x - 2, slot.y - 2, slot.x + CARD_SIZE + 2, slot.y + CARD_SIZE + 2, outline);
            graphics.fill(slot.x, slot.y, slot.x + CARD_SIZE, slot.y + CARD_SIZE, fill);
            if (slot.card.getRarity() != Rarity.GODLY) {
                int bar = getRarityBarColor(slot.card);
                graphics.fill(slot.x, slot.y + CARD_SIZE - 4, slot.x + CARD_SIZE, slot.y + CARD_SIZE, bar);
            } else {
                graphics.fill(slot.x, slot.y + CARD_SIZE - 4, slot.x + CARD_SIZE, slot.y + CARD_SIZE, 0xFFFFF59D);
            }
            int textColor = slot.card.getRarity() == Rarity.GODLY ? 0xFF3E2800 : 0xFFFFFFFF;
            drawCenteredWrappedString(graphics, slot.card.getDisplayName(), slot.x, slot.y, textColor);
            if (hovered) graphics.renderTooltip(this.font, getTooltip(slot.card), mouseX, mouseY);
        }
        for (Sparkle s : sparkles) {
            float alpha = 1f - (float) s.age / s.maxAge;
            if (alpha < 0) alpha = 0;
            int a = (int) (alpha * 255);
            int ix = (int) s.x;
            int iy = (int) s.y;
            int arm = (s.age % 8 < 4) ? 2 : 1;
            int gold = (a << 24) | 0xFFFFD7;
            graphics.fill(ix - arm, iy, ix + arm + 1, iy + 1, gold);
            graphics.fill(ix, iy - arm, ix + 1, iy + arm + 1, gold);
            graphics.fill(ix, iy, ix + 1, iy + 1, (a << 24) | 0xFFFFFF);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private int getOutlineColor(Card card, boolean hovered) {
        int base = switch (card.getRarity()) {
            case COMMON -> 0xFFFFFFFF;
            case UNCOMMON -> 0xFF2ECC71;
            case RARE -> 0xFF3498DB;
            case LEGENDARY -> 0xFFF1C40F;
            case CURSED -> 0xFF9B59B6;
            case MYTHIC -> 0xFFFFD700;
            case GODLY -> 0xFFFFE44D;
        };
        if (hovered) return 0xFFFFFFFF;
        return base;
    }

    private int getFillColor(Card card, boolean hovered) {
        if (card.getRarity() == Rarity.GODLY) {
            return hovered ? 0xFFFFF59D : 0xFFFFD700;
        }
        if (card.getRarity() == Rarity.MYTHIC) {
            return hovered ? 0xFF4A3A10 : 0xFF2B2100;
        }
        return hovered ? 0xFF3A3A5A : 0xFF1E1E1E;
    }

    private int getRarityBarColor(Card card) {
        return switch (card.getRarity()) {
            case COMMON -> 0xFFFFFFFF;
            case UNCOMMON -> 0xFF2ECC71;
            case RARE -> 0xFF3498DB;
            case LEGENDARY -> 0xFFF1C40F;
            case CURSED -> 0xFF9B59B6;
            case MYTHIC -> 0xFFFFD700;
            case GODLY -> 0xFFFFD700;
        };
    }

    private void drawCenteredWrappedString(GuiGraphics graphics, String text, int x, int y, int color) {
        int maxWidth = CARD_SIZE - 8;
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (this.font.width(test) > maxWidth && line.length() > 0) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else line = new StringBuilder(test);
        }
        if (line.length() > 0) lines.add(line.toString());
        int totalHeight = lines.size() * this.font.lineHeight;
        int startY = y + (CARD_SIZE - totalHeight) / 2 - 2;
        for (int i = 0; i < lines.size(); i++) {
            graphics.drawCenteredString(this.font, lines.get(i), x + CARD_SIZE / 2, startY + i * this.font.lineHeight, color);
        }
    }

    private static Component struck(String text) {
        return Component.literal(text).withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.GRAY);
    }

    private Component getTooltip(Card card) {
        String id = card.getId().getPath();
        Component rarity = Component.literal("[" + card.getRarity().name() + "] ").withStyle(getRarityChatColor(card.getRarity()));
        Component desc;
        switch (id) {
            case "kick":
                desc = struck(card.getDescription());
                break;
            case "sad_step":
                desc = Component.literal("increase ")
                        .append(struck("dash distance by 1 block"))
                        .append(Component.literal(" and run speed by 5%"));
                break;
            case "flashstep":
                desc = Component.literal("Increase running speed by 20%, ")
                        .append(struck("dash distance by 3 blocks"))
                        .append(Component.literal(", and "))
                        .append(struck("decrease dash cooldown by 4s"));
                break;
            case "martial_artist": {
                Component knock = Component.literal("knockback by 3 blocks").withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.GRAY);
                desc = Component.literal("Increase melee dmg by 15%, attack speed by 10%, and ").append(knock); break;
            }
            default: desc = Component.literal(card.getDescription());
        }
        return rarity.copy().append(desc);
    }

    private ChatFormatting getRarityChatColor(Rarity r) {
        return switch (r) {
            case COMMON -> ChatFormatting.WHITE;
            case UNCOMMON -> ChatFormatting.GREEN;
            case RARE -> ChatFormatting.BLUE;
            case LEGENDARY -> ChatFormatting.YELLOW;
            case CURSED -> ChatFormatting.LIGHT_PURPLE;
            case MYTHIC -> ChatFormatting.GOLD;
            case GODLY -> ChatFormatting.GOLD;
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) for (PlacedCard slot : placed) if (slot.isHovered((int) mouseX, (int) mouseY)) {
            NetworkHandler.CHANNEL.sendToServer(new SelectCardPacket(slot.card.getId()));
            Minecraft.getInstance().setScreen(null); return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static final class PlacedCard {
        final Card card;
        final int x;
        final int y;

        PlacedCard(Card card, int x, int y) {
            this.card = card;
            this.x = x;
            this.y = y;
        }

        boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= x && mouseX < x + CARD_SIZE && mouseY >= y && mouseY < y + CARD_SIZE;
        }
    }

    private static final class Sparkle {
        float x;
        float y;
        float vx;
        float vy;
        float baseY;
        int age;
        int maxAge;

        Sparkle(float x, float y, float vx, float vy, float baseY) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.baseY = baseY;
            this.maxAge = 18 + (int) (Math.random() * 18);
            this.age = 0;
        }
    }
}
