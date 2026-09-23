package com.cardmod.registry;

import com.cardmod.card.Card;
import com.cardmod.card.ModifierSpec;
import com.cardmod.card.Rarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_TOTAL;

public final class CardRegistry {
    private static final Map<ResourceLocation, Card> CARDS = new LinkedHashMap<>();

    private CardRegistry() {
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation("cardmod", path);
    }

    private static UUID uuid(String key) {
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }

    private static ModifierSpec mod(Supplier<Attribute> attribute, AttributeModifier.Operation operation, double amount, String key) {
        return new ModifierSpec(attribute, operation, amount, uuid(key));
    }

    private static Supplier<Attribute> apothic(String name) {
        return () -> ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("attributeslib", name));
    }

    static {
        register(new Card(id("quick_hands"), "Quick Hands", "Increases melee speed by 4% and reload speed 4%", Rarity.COMMON, List.of(
                mod(() -> Attributes.ATTACK_SPEED, MULTIPLY_TOTAL, 0.04, "cardmod:quick_hands"),
                mod(apothic("draw_speed"), MULTIPLY_TOTAL, 0.04, "cardmod:quick_hands_draw"))));
        register(new Card(id("windswept"), "Windswept", "Increases running speed by 5%", Rarity.COMMON, List.of(
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, 0.05, "cardmod:windswept"))));
        register(new Card(id("light_focus"), "Light Focus", "Increases ranged damage by 4%", Rarity.COMMON, List.of(
                mod(apothic("arrow_damage"), MULTIPLY_TOTAL, 0.04, "cardmod:light_focus"))));
        register(new Card(id("iron_will"), "Iron Will", "-1s stun duration", Rarity.UNCOMMON, List.of()));
        register(new Card(id("kick"), "Kick", "Knock back entities in front of you 2 blocks", Rarity.UNCOMMON, List.of()));
        register(new Card(id("sad_step"), "Sad Step", "increase dash distance by 1 block and run speed by 5%", Rarity.UNCOMMON, List.of(
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, 0.05, "cardmod:sad_step"))));
        register(new Card(id("low_temper"), "Low Temper", "Increase health by 3", Rarity.RARE, List.of(
                mod(() -> Attributes.MAX_HEALTH, ADDITION, 3.0, "cardmod:low_temper"))));
        register(new Card(id("good_timing"), "Good Timing", "Allows you to block an attack once every 10s", Rarity.RARE, List.of()));
        register(new Card(id("keen_eyes"), "Keen Eyes", "Increases ranged damage by 10%", Rarity.RARE, List.of(
                mod(apothic("arrow_damage"), MULTIPLY_TOTAL, 0.10, "cardmod:keen_eyes"))));
        register(new Card(id("martial_artist"), "Martial Artist",
                "Increase melee dmg by 15%, attack speed by 10%, and knockback by 3 blocks",
                Rarity.LEGENDARY, List.of(
                mod(() -> Attributes.ATTACK_DAMAGE, MULTIPLY_TOTAL, 0.15, "cardmod:martial_artist_damage"),
                mod(() -> Attributes.ATTACK_SPEED, MULTIPLY_TOTAL, 0.10, "cardmod:martial_artist_speed"),
                mod(() -> Attributes.ATTACK_KNOCKBACK, ADDITION, 1.0, "cardmod:martial_artist_knockback"))));
        register(new Card(id("flashstep"), "Flashstep",
                "Increase running speed by 20%, dash distance by 3 blocks, and decrease dash cooldown by 4s",
                Rarity.LEGENDARY, List.of(
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, 0.20, "cardmod:flashstep"))));
        register(new Card(id("chainsmoker"), "Chainsmoker",
                "Decrease movement by 50% increase ranged damage by 50%",
                Rarity.MYTHIC, List.of(
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, -0.50, "cardmod:chainsmoker_speed"),
                mod(apothic("arrow_damage"), MULTIPLY_TOTAL, 0.50, "cardmod:chainsmoker_damage"))));
        register(new Card(id("ceramic_skin"), "Ceramic Skin", "Increases damage reduction by 4%", Rarity.COMMON, List.of()));
        register(new Card(id("mischambered_heart"), "Mischambered Heart", "-1hp, increase melee damage by 5%", Rarity.COMMON, List.of(
                mod(() -> Attributes.MAX_HEALTH, ADDITION, -1.0, "cardmod:mischambered_heart_health"),
                mod(() -> Attributes.ATTACK_DAMAGE, MULTIPLY_TOTAL, 0.05, "cardmod:mischambered_heart_damage"))));
        register(new Card(id("sixteen_going_on_eighteen"), "Sixteen Going On Eighteen",
                "Every 16 attacks you deal 2x damage", Rarity.COMMON, List.of()));
        register(new Card(id("bruised_hands"), "Bruised Hands", "Reload speed -4%, increase ranged damage by 6%", Rarity.COMMON, List.of(
                mod(apothic("draw_speed"), MULTIPLY_TOTAL, -0.04, "cardmod:bruised_hands_draw"),
                mod(apothic("arrow_damage"), MULTIPLY_TOTAL, 0.06, "cardmod:bruised_hands_damage"))));
        register(new Card(id("calloused_hands"), "Calloused Hands", "Increase attack speed and DMG by 7%", Rarity.UNCOMMON, List.of(
                mod(() -> Attributes.ATTACK_SPEED, MULTIPLY_TOTAL, 0.07, "cardmod:calloused_hands_speed"),
                mod(() -> Attributes.ATTACK_DAMAGE, MULTIPLY_TOTAL, 0.07, "cardmod:calloused_hands_dmg"))));
        register(new Card(id("springy_tendons"), "Springy Tendons",
                "Increases jump height by half a block and movement speed by 5%",
                Rarity.UNCOMMON, List.of(
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, 0.05, "cardmod:springy_tendons_speed"))));
        register(new Card(id("hunters_eye"), "Hunter's Eye", "+8% ranged damage", Rarity.UNCOMMON, List.of(
                mod(apothic("arrow_damage"), MULTIPLY_TOTAL, 0.08, "cardmod:hunters_eye_dmg"))));
        register(new Card(id("reinforced_bones"), "Reinforced Bones", "+5 HP", Rarity.UNCOMMON, List.of(
                mod(() -> Attributes.MAX_HEALTH, ADDITION, 5.0, "cardmod:reinforced_bones"))));
        register(new Card(id("blood_rush"), "Blood Rush",
                "Taking damage grants +10% movement speed for 3 sec", Rarity.UNCOMMON, List.of()));
        register(new Card(id("counterweight"), "Counterweight", "+10% knockback resistance, -5% movement speed", Rarity.UNCOMMON, List.of(
                mod(() -> Attributes.KNOCKBACK_RESISTANCE, ADDITION, 0.10, "cardmod:counterweight_kb"),
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, -0.05, "cardmod:counterweight_speed"))));
        register(new Card(id("quick_draw"), "Quick Draw", "+10% reload speed", Rarity.UNCOMMON, List.of(
                mod(apothic("draw_speed"), MULTIPLY_TOTAL, 0.10, "cardmod:quick_draw"))));
        register(new Card(id("heavy_hands"), "Heavy Hands", "+10% melee damage, -5% melee attack speed", Rarity.UNCOMMON, List.of(
                mod(() -> Attributes.ATTACK_DAMAGE, MULTIPLY_TOTAL, 0.10, "cardmod:heavy_hands_dmg"),
                mod(() -> Attributes.ATTACK_SPEED, MULTIPLY_TOTAL, -0.05, "cardmod:heavy_hands_speed"))));
        register(new Card(id("fleet_hands"), "Fleet Hands", "+10% melee attack speed, -5% melee damage", Rarity.UNCOMMON, List.of(
                mod(() -> Attributes.ATTACK_SPEED, MULTIPLY_TOTAL, 0.10, "cardmod:fleet_hands_speed"),
                mod(() -> Attributes.ATTACK_DAMAGE, MULTIPLY_TOTAL, -0.05, "cardmod:fleet_hands_dmg"))));
        register(new Card(id("sharpshooters_grip"), "Sharpshooter's Grip", "+10% ranged damage, +5% reload speed", Rarity.UNCOMMON, List.of(
                mod(apothic("arrow_damage"), MULTIPLY_TOTAL, 0.10, "cardmod:sharpshooters_grip_dmg"),
                mod(apothic("draw_speed"), MULTIPLY_TOTAL, 0.05, "cardmod:sharpshooters_grip_draw"))));
        register(new Card(id("long_reach"), "Long Reach", "+10% unarmed attack range", Rarity.UNCOMMON, List.of(
                mod(() -> ForgeMod.ENTITY_REACH.get(), MULTIPLY_TOTAL, 0.10, "cardmod:long_reach"))));
        register(new Card(id("quick_trigger"), "Quick Trigger", "+5% gun fire rate", Rarity.COMMON, List.of()));
        register(new Card(id("firm_grip"), "Firm Grip", "+5% melee knockback", Rarity.COMMON, List.of(
                mod(() -> Attributes.ATTACK_KNOCKBACK, ADDITION, 0.05, "cardmod:firm_grip"))));
        register(new Card(id("steady_aim"), "Steady Aim", "+5% ranged Damage", Rarity.COMMON, List.of(
                mod(apothic("arrow_damage"), MULTIPLY_TOTAL, 0.05, "cardmod:steady_aim"))));
        register(new Card(id("light_step"), "Light Step", "+4% movement speed, +1 block Dash distance", Rarity.COMMON, List.of(
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, 0.04, "cardmod:light_step"))));
        register(new Card(id("heavy_swing"), "Heavy Swing", "+5% melee damage, -3% melee attack speed", Rarity.COMMON, List.of(
                mod(() -> Attributes.ATTACK_DAMAGE, MULTIPLY_TOTAL, 0.05, "cardmod:heavy_swing_dmg"),
                mod(() -> Attributes.ATTACK_SPEED, MULTIPLY_TOTAL, -0.03, "cardmod:heavy_swing_speed"))));
        register(new Card(id("fast_fingers"), "Fast Fingers", "+5% reload speed", Rarity.COMMON, List.of(
                mod(apothic("draw_speed"), MULTIPLY_TOTAL, 0.05, "cardmod:fast_fingers"))));
        register(new Card(id("thick_soles"), "Thick Soles", "+0.5 block jump height", Rarity.COMMON, List.of()));
        register(new Card(id("long_stride"), "Long Stride", "+2 blocks Dash distance", Rarity.COMMON, List.of()));
        register(new Card(id("short_fuse"), "Short Fuse", "-1 sec Dash cooldown", Rarity.COMMON, List.of()));
        register(new Card(id("hard_head"), "Hard Head", "+1 HP", Rarity.COMMON, List.of(
                mod(() -> Attributes.MAX_HEALTH, ADDITION, 1.0, "cardmod:hard_head"))));
        register(new Card(id("soft_landing"), "Soft Landing", "-25% fall damage", Rarity.COMMON, List.of()));
        register(new Card(id("braced_stance"), "Braced Stance", "+5% knockback resistance", Rarity.COMMON, List.of(
                mod(() -> Attributes.KNOCKBACK_RESISTANCE, ADDITION, 0.05, "cardmod:braced_stance"))));
        register(new Card(id("adrenaline"), "Adrenaline", "+5% movement speed below 50% HP", Rarity.COMMON, List.of()));
        register(new Card(id("clean_hit"), "Clean Hit", "+5% damage against enemies above 75% HP", Rarity.COMMON, List.of()));
        register(new Card(id("desperation"), "Desperation", "+5% damage below 25% HP", Rarity.COMMON, List.of()));
        register(new Card(id("unmoving"), "Unmoving", "+15% defense while crouching", Rarity.RARE, List.of()));
        register(new Card(id("bloodied"), "Bloodied", "+15% damage below 25% HP", Rarity.RARE, List.of()));
        register(new Card(id("predator"), "Predator", "+10% damage against enemies below 50% HP, +5% movement speed", Rarity.RARE, List.of(
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, 0.05, "cardmod:predator_speed"))));
        register(new Card(id("caffeinated_blood"), "Caffeinated Blood",
                "Increase movement speed by 10% and dash cool down by 5%", Rarity.RARE, List.of(
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, 0.10, "cardmod:caffeinated_blood"))));
        register(new Card(id("berserkers_pulse"), "Berserker's Pulse", "+15% damage below 50% HP", Rarity.RARE, List.of()));
        register(new Card(id("iron_frame"), "Iron Frame", "+10 HP, -5% movement speed", Rarity.RARE, List.of(
                mod(() -> Attributes.MAX_HEALTH, ADDITION, 10.0, "cardmod:iron_frame_health"),
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, -0.05, "cardmod:iron_frame_speed"))));
        register(new Card(id("glass_cannon"), "Glass Cannon", "+15% damage, -5% defense", Rarity.RARE, List.of()));
        register(new Card(id("deadeye"), "Deadeye", "+15% ranged damage, -5% movement speed", Rarity.RARE, List.of(
                mod(apothic("arrow_damage"), MULTIPLY_TOTAL, 0.15, "cardmod:deadeye_dmg"),
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, -0.05, "cardmod:deadeye_speed"))));
        register(new Card(id("relentless"), "Relentless", "+10% melee damage, +10% melee attack speed", Rarity.RARE, List.of(
                mod(() -> Attributes.ATTACK_DAMAGE, MULTIPLY_TOTAL, 0.10, "cardmod:relentless_dmg"),
                mod(() -> Attributes.ATTACK_SPEED, MULTIPLY_TOTAL, 0.10, "cardmod:relentless_speed"))));
        register(new Card(id("fleetfoot"), "Fleetfoot", "+15% movement speed, +5 blocks Dash distance", Rarity.RARE, List.of(
                mod(() -> Attributes.MOVEMENT_SPEED, MULTIPLY_TOTAL, 0.15, "cardmod:fleetfoot"))));
    }

    public static Card register(Card card) {
        CARDS.put(card.getId(), card);
        return card;
    }

    public static Card get(ResourceLocation id) {
        return CARDS.get(id);
    }

    public static Collection<Card> getAll() {
        return Collections.unmodifiableCollection(CARDS.values());
    }

    public static Card getOrThrow(ResourceLocation id) {
        Card card = CARDS.get(id);
        if (card == null) {
            throw new IllegalArgumentException("Unknown card: " + id);
        }
        return card;
    }

    public static List<Card> getRandomOffer(RandomSource random, int count) {
        Map<Rarity, List<Card>> byRarity = new EnumMap<>(Rarity.class);
        for (Rarity r : Rarity.values()) {
            byRarity.put(r, new ArrayList<>());
        }
        for (Card c : CARDS.values()) {
            byRarity.get(c.getRarity()).add(c);
        }
        List<Rarity> available = new ArrayList<>();
        for (Map.Entry<Rarity, List<Card>> e : byRarity.entrySet()) {
            if (!e.getValue().isEmpty()) {
                available.add(e.getKey());
            }
        }
        if (available.isEmpty()) {
            return List.of();
        }
        List<Card> pool = new ArrayList<>(CARDS.values());
        List<Card> result = new ArrayList<>();
        Map<Rarity, List<Card>> remainingByRarity = new EnumMap<>(byRarity);
        int attempts = 0;
        while (result.size() < count && result.size() < pool.size() && attempts < 100) {
            attempts++;
            double totalWeight = 0;
            for (Rarity r : available) {
                if (!remainingByRarity.get(r).isEmpty()) {
                    totalWeight += r.getWeight();
                }
            }
            if (totalWeight <= 0) break;
            double roll = random.nextDouble() * totalWeight;
            Rarity picked = null;
            double acc = 0;
            for (Rarity r : available) {
                List<Card> list = remainingByRarity.get(r);
                if (list.isEmpty()) continue;
                acc += r.getWeight();
                if (roll < acc) { picked = r; break; }
            }
            if (picked == null) {
                for (Rarity r : available) if (!remainingByRarity.get(r).isEmpty()) { picked = r; break; }
            }
            if (picked == null) break;
            List<Card> list = remainingByRarity.get(picked);
            Card chosen = list.remove(random.nextInt(list.size()));
            result.add(chosen);
        }
        while (result.size() < count && result.size() < pool.size()) {
            for (Card c : pool) if (!result.contains(c)) { result.add(c); break; }
        }
        return Collections.unmodifiableList(result);
    }

    public static List<Card> getRandomOfferByRarity(RandomSource random, Rarity rarity, int count) {
        List<Card> pool = CARDS.values().stream().filter(c -> c.getRarity() == rarity).toList();
        if (pool.isEmpty()) return List.of();
        List<Card> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled, new Random(random.nextLong()));
        return Collections.unmodifiableList(shuffled.subList(0, Math.min(count, shuffled.size())));
    }
}
