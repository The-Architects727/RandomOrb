//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.architects.RandomItemCommandMod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;

import net.architects.RandomItemCommandMod.config.ModConfigs;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GiveRandomCommand {
    private static boolean forceExit = false;
    public GiveRandomCommand() {
    }
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("giverandom").requires((source) -> {
            return source.hasPermissionLevel(2);
        })).then(CommandManager.argument("targets", EntityArgumentType.players()).then(((RequiredArgumentBuilder)CommandManager.argument("count", IntegerArgumentType.integer()).executes((context) -> {
            return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "count"));
        })))));

    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
        int repeats = 1;
        String message;
            do {
                ItemStack item = getItem();
                int i = item.getItem().getMaxCount();
                int j = i * 100;

                Iterator var6 = targets.iterator();

                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) var6.next();
                item.setCount(item.getMaxCount() / (roll.nextInt(item.getMaxCount()) + 1));

                boolean bl = serverPlayerEntity.getInventory().insertStack(item);
                ItemEntity itemEntity;
                if (bl && item.isEmpty()) {
                    item.setCount(1);
                    itemEntity = serverPlayerEntity.dropItem(item, false);
                    if (itemEntity != null) {
                        itemEntity.setDespawnImmediately();
                    }

                    serverPlayerEntity.world.playSound((PlayerEntity) null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    serverPlayerEntity.currentScreenHandler.sendContentUpdates();
                } else {
                    itemEntity = serverPlayerEntity.dropItem(item, false);
                    if (itemEntity != null) {
                        itemEntity.resetPickupDelay();
                        itemEntity.setOwner(serverPlayerEntity.getUuid());
                    }
                }
                repeats++;

            } while (repeats <= count);
            if (forceExit) {
                source.sendFeedback(Text.of("Was not able to get Item. Make sure catagory blacklist is not all inclusive."), false);
            } else {
                source.sendFeedback(Text.of("Gave " + source.getName() + " " + count + " random items"), false);
            }

        return targets.size();
    }
    static java.util.Random roll = new java.util.Random();

    private static boolean containsBlacklist(Item chosenItem) {
        for(String item : ModConfigs.blacklist) {
            if (chosenItem.equals((Registry.ITEM.get(Identifier.tryParse(item))))) {
                return true;
            }
        }
        return false;
    }

    private static ItemStack getItem() {
        int reapeatedTries = 0;
        Item chosenItem;
        chosenItem = Registry.ITEM.getRandom(net.minecraft.util.math.random.Random.create()).get().value();

        ItemStack itemStack;

        if (ModConfigs.whitelistBoolean) {
            try {
                chosenItem = Registry.ITEM.get(Identifier.tryParse(ModConfigs.whitelist[roll.nextInt(ModConfigs.whitelist.length)]));
                itemStack = new ItemStack(chosenItem, ((roll.nextInt(chosenItem.getMaxCount()) / 4) + 1));
            } catch (Exception e) {
                itemStack = new ItemStack(Items.AIR, 0);
            }

        } else if ((ModConfigs.blacklistBoolean && onBlacklist(chosenItem))) {
            try {
                while (containsBlacklist(chosenItem) || isIlleagal(chosenItem)) {
                    chosenItem = Registry.ITEM.getRandom(net.minecraft.util.math.random.Random.create()).get().value();
                }
                itemStack = new ItemStack(chosenItem, ((roll.nextInt(chosenItem.getMaxCount()) / 4) + 1));
            } catch (Exception e) {
                itemStack = new ItemStack(Items.AIR, 0);
            }

        } else if(ModConfigs.modBlacklistBoolean && onModBlacklist(chosenItem)) {
            try {
                while ((onModBlacklist(chosenItem)) && !forceExit) {
                    chosenItem = Registry.ITEM.getRandom(net.minecraft.util.math.random.Random.create()).get().value();
                    reapeatedTries++;
                    if (reapeatedTries > 1000) {
                        forceExit = true;
                    }
                }
                if(forceExit) {
                    itemStack = new ItemStack(Items.AIR, 0);
                } else {
                    itemStack = new ItemStack(chosenItem, ((roll.nextInt(chosenItem.getMaxCount()) / 4) + 1));
                }
            } catch (Exception e) {
                itemStack = new ItemStack(Items.AIR, 0);
            }

        } else if(ModConfigs.whitelistBoolean) {
            try {
                chosenItem = whitelistChoose().getItem();
                    itemStack = new ItemStack(chosenItem, ((roll.nextInt(chosenItem.getMaxCount()) / 4) + 1));
            } catch (Exception e) {
                itemStack = new ItemStack(Items.AIR, 0);
            }

        }
        else {
            try {
                do {
                    chosenItem = Registry.ITEM.getRandom(net.minecraft.util.math.random.Random.create()).get().value();
                } while (isIlleagal(chosenItem));
                itemStack = new ItemStack(chosenItem, ((roll.nextInt(chosenItem.getMaxCount()) / 4) + 1));
            } catch (Exception e) {
                itemStack = new ItemStack(Items.AIR, 0);
            }



        }

        int enchantCount = roll.nextInt(ModConfigs.enchantMax + 4);
        enchantCount -= 4;
        if (enchantCount < 0) {
            enchantCount = 0;
        }



        for(int i = 1; i <= enchantCount; i++) {
            if (isTools(chosenItem)) {
                Enchantment enchant = toolEnchantment()[roll.nextInt(toolEnchantment().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
            } else if (isArmorHelment(chosenItem)) {
                Enchantment enchant = ArmorEnchantmentHelmet()[roll.nextInt(ArmorEnchantmentHelmet().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
            } else if (isArmorChestplate(chosenItem)) {
                Enchantment enchant = ArmorEnchantmentChestplate()[roll.nextInt(ArmorEnchantmentChestplate().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
            } else if (isArmorLeggings(chosenItem)) {
                Enchantment enchant = ArmorEnchantmentLeggings()[roll.nextInt(ArmorEnchantmentLeggings().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
            } else if (isArmorBoots(chosenItem)) {
                Enchantment enchant = ArmorEnchantmentBoots()[roll.nextInt(ArmorEnchantmentBoots().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
            } else if (isSwords(chosenItem)) {
                Enchantment enchant = SwordEnchantments()[roll.nextInt(SwordEnchantments().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
            } else if (isBow(chosenItem)) {
                Enchantment enchant = BowEnchantments()[roll.nextInt(BowEnchantments().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
            } else if (isCrossBow(chosenItem)) {
                Enchantment enchant = CrossBowEnchantments()[roll.nextInt(CrossBowEnchantments().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
            } else if (isTrident(chosenItem)) {
                Enchantment enchant = TridentEnchantments()[roll.nextInt(TridentEnchantments().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
            } else if (isFishingRod(chosenItem)) {
                Enchantment enchant = fishingRodEnchantment()[roll.nextInt(fishingRodEnchantment().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
            } else if (chosenItem.equals(Items.SHIELD)) {
                Enchantment enchant = shieldEnchantment()[roll.nextInt(shieldEnchantment().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
                if(i == 2) {
                    return itemStack;
                }
            } else if (chosenItem.equals(Items.ELYTRA)) {
                Enchantment enchant = shieldEnchantment()[roll.nextInt(shieldEnchantment().length)];
                itemStack.addEnchantment(enchant, roll.nextInt(enchant.getMaxLevel()) + 1);
                if(i == 2) {
                    return itemStack;
                }
            }
        }

        return itemStack;
    }


    private static boolean isIlleagal(Item item) {
        return item.equals(Items.BEDROCK) || item.equals(Items.BARRIER) || item.equals(Items.COMMAND_BLOCK) || item.equals(Items.END_PORTAL_FRAME) || item.equals(Items.LIGHT)
                || item.equals(Items.COMMAND_BLOCK_MINECART) || item.equals(Items.CHAIN_COMMAND_BLOCK) || item.equals(Items.REPEATING_COMMAND_BLOCK) || item.equals(Items.DEBUG_STICK)
                || item.equals(Items.STRUCTURE_BLOCK) || item.equals(Items.STRUCTURE_VOID) || item.equals(Items.POTION)|| item.equals(Items.LINGERING_POTION)
                || item.equals(Items.SPLASH_POTION)|| item.equals(Items.ENCHANTED_BOOK) || item.equals(Items.JIGSAW);
    }

    private static boolean onBlacklist(Item item) {
        for(String blacklistItem : ModConfigs.blacklist) {
            if((blacklistItem).equalsIgnoreCase(item.getTranslationKey())) {
                return true;
            }
        }
        return false;
    }

    private static boolean onModBlacklist(Item item) {
        try {
            if (!isIlleagal(item) && item.getGroup() != null) {
                for (String blacklistGroup : ModConfigs.modBlacklist) {
                    if (item.getGroup().getName().equalsIgnoreCase(blacklistGroup)) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }

    }


    private static ItemStack whitelistChoose() {
        Item item = Registry.ITEM.get(Identifier.tryParse((ModConfigs.whitelist[roll.nextInt(ModConfigs.whitelist.length)])));
        return new ItemStack(item, roll.nextInt(item.getMaxCount() / 4));
    }

    private static boolean isTools(Item item) {
        return item.equals(Items.WOODEN_AXE) || item.equals(Items.WOODEN_PICKAXE) || item.equals(Items.WOODEN_SHOVEL) || item.equals(Items.WOODEN_HOE)
                || item.equals(Items.STONE_AXE) || item.equals(Items.STONE_PICKAXE) || item.equals(Items.STONE_SHOVEL) || item.equals(Items.STONE_HOE)
                || item.equals(Items.IRON_AXE) || item.equals(Items.IRON_PICKAXE) || item.equals(Items.IRON_SHOVEL) || item.equals(Items.IRON_HOE)
                || item.equals(Items.GOLDEN_AXE) || item.equals(Items.GOLDEN_PICKAXE) || item.equals(Items.GOLDEN_SHOVEL) || item.equals(Items.GOLDEN_HOE)
                || item.equals(Items.DIAMOND_AXE) || item.equals(Items.DIAMOND_PICKAXE) || item.equals(Items.DIAMOND_SHOVEL) || item.equals(Items.DIAMOND_HOE)
                || item.equals(Items.NETHERITE_AXE) || item.equals(Items.NETHERITE_PICKAXE) || item.equals(Items.NETHERITE_SHOVEL) || item.equals(Items.NETHERITE_HOE);
    }

    private static boolean isFishingRod(Item item) {
        return item.equals(Items.FISHING_ROD);
    }

    private static Enchantment[] toolEnchantment() {
        Enchantment[] enchantments = new Enchantment[5];
        enchantments[0] = Enchantments.EFFICIENCY;
        enchantments[1] = Enchantments.SILK_TOUCH;
        enchantments[2] = Enchantments.UNBREAKING;
        enchantments[3] = Enchantments.FORTUNE;
        enchantments[4] = Enchantments.MENDING;

        return enchantments;
    }

    private static Enchantment[] fishingRodEnchantment() {
        Enchantment[] enchantments = new Enchantment[4];
        enchantments[0] = Enchantments.UNBREAKING;
        enchantments[1] = Enchantments.LUCK_OF_THE_SEA;
        enchantments[2] = Enchantments.LURE;
        enchantments[3] = Enchantments.MENDING;

        return enchantments;
    }

    private static Enchantment[] shieldEnchantment() {
        Enchantment[] enchantments = new Enchantment[2];
        enchantments[0] = Enchantments.UNBREAKING;
        enchantments[1] = Enchantments.MENDING;

        return enchantments;
    }

    private static boolean isArmorHelment(Item item) {
        return item.equals(Items.LEATHER_HELMET) || item.equals(Items.IRON_HELMET) || item.equals(Items.GOLDEN_HELMET) || item.equals(Items.DIAMOND_HELMET) || item.equals(Items.NETHERITE_CHESTPLATE);
    }
    private static boolean isArmorChestplate(Item item) {
        return item.equals(Items.LEATHER_CHESTPLATE) || item.equals(Items.IRON_CHESTPLATE) || item.equals(Items.GOLDEN_CHESTPLATE) || item.equals(Items.DIAMOND_CHESTPLATE) || item.equals(Items.NETHERITE_CHESTPLATE);
    }
    private static boolean isArmorLeggings(Item item) {
        return item.equals(Items.LEATHER_LEGGINGS) || item.equals(Items.IRON_LEGGINGS) || item.equals(Items.GOLDEN_LEGGINGS) || item.equals(Items.DIAMOND_LEGGINGS) || item.equals(Items.NETHERITE_LEGGINGS);
    }
    private static boolean isArmorBoots(Item item) {
        return item.equals(Items.LEATHER_BOOTS) || item.equals(Items.IRON_BOOTS) || item.equals(Items.GOLDEN_BOOTS) || item.equals(Items.DIAMOND_BOOTS) || item.equals(Items.NETHERITE_BOOTS);
    }

    private static Enchantment[] ArmorEnchantmentHelmet() {
        Enchantment[] enchantments = new Enchantment[9];
        enchantments[0] = Enchantments.UNBREAKING;
        enchantments[1] = Enchantments.MENDING;
        enchantments[2] = Enchantments.PROTECTION;
        enchantments[3] = Enchantments.BLAST_PROTECTION;
        enchantments[4] = Enchantments.FIRE_PROTECTION;
        enchantments[5] = Enchantments.PROJECTILE_PROTECTION;
        enchantments[6] = Enchantments.RESPIRATION;
        enchantments[7] = Enchantments.AQUA_AFFINITY;
        enchantments[8] = Enchantments.THORNS;

        return enchantments;
    }
    private static Enchantment[] ArmorEnchantmentChestplate() {
        Enchantment[] enchantments = new Enchantment[7];
        enchantments[0] = Enchantments.UNBREAKING;
        enchantments[1] = Enchantments.MENDING;
        enchantments[2] = Enchantments.PROTECTION;
        enchantments[3] = Enchantments.BLAST_PROTECTION;
        enchantments[4] = Enchantments.FIRE_PROTECTION;
        enchantments[5] = Enchantments.PROJECTILE_PROTECTION;
        enchantments[6] = Enchantments.THORNS;

        return enchantments;
    }
    private static Enchantment[] ArmorEnchantmentLeggings() {
        Enchantment[] enchantments = new Enchantment[8];
        enchantments[0] = Enchantments.UNBREAKING;
        enchantments[1] = Enchantments.MENDING;
        enchantments[2] = Enchantments.PROTECTION;
        enchantments[3] = Enchantments.BLAST_PROTECTION;
        enchantments[4] = Enchantments.FIRE_PROTECTION;
        enchantments[5] = Enchantments.PROJECTILE_PROTECTION;
        enchantments[6] = Enchantments.SWIFT_SNEAK;
        enchantments[7] = Enchantments.THORNS;

        return enchantments;
    }
    private static Enchantment[] ArmorEnchantmentBoots() {
        Enchantment[] enchantments = new Enchantment[10];
        enchantments[0] = Enchantments.UNBREAKING;
        enchantments[1] = Enchantments.MENDING;
        enchantments[2] = Enchantments.PROTECTION;
        enchantments[3] = Enchantments.BLAST_PROTECTION;
        enchantments[4] = Enchantments.FIRE_PROTECTION;
        enchantments[5] = Enchantments.PROJECTILE_PROTECTION;
        enchantments[6] = Enchantments.SOUL_SPEED;
        enchantments[7] = Enchantments.FEATHER_FALLING;
        enchantments[8] = Enchantments.DEPTH_STRIDER;
        enchantments[9] = Enchantments.THORNS;

        return enchantments;
    }

    private static boolean isSwords(Item item) {
        return item.equals(Items.WOODEN_SWORD)
                || item.equals(Items.STONE_SWORD)
                || item.equals(Items.IRON_SWORD)
                || item.equals(Items.GOLDEN_SWORD)
                || item.equals(Items.DIAMOND_SWORD)
                || item.equals(Items.NETHERITE_SWORD);
    }

    private static Enchantment[] SwordEnchantments() {
        Enchantment[] enchantments = new Enchantment[9];
        enchantments[0] = Enchantments.UNBREAKING;
        enchantments[1] = Enchantments.MENDING;
        enchantments[2] = Enchantments.SHARPNESS;
        enchantments[3] = Enchantments.BANE_OF_ARTHROPODS;
        enchantments[4] = Enchantments.SMITE;
        enchantments[5] = Enchantments.FIRE_ASPECT;
        enchantments[6] = Enchantments.LOOTING;
        enchantments[7] = Enchantments.SWEEPING;
        enchantments[8] = Enchantments.KNOCKBACK;

        return enchantments;
    }

    private static boolean isCrossBow(Item item) {
        return item.equals(Items.CROSSBOW);
    }

    private static Enchantment[] CrossBowEnchantments() {
        Enchantment[] enchantments = new Enchantment[5];
        enchantments[0] = Enchantments.UNBREAKING;
        enchantments[1] = Enchantments.MENDING;
        enchantments[2] = Enchantments.PIERCING;
        enchantments[3] = Enchantments.MULTISHOT;
        enchantments[4] = Enchantments.QUICK_CHARGE;

        return enchantments;
    }

    private static boolean isTrident(Item item) {
        return item.equals(Items.TRIDENT);
    }

    private static Enchantment[] TridentEnchantments() {
        Enchantment[] enchantments = new Enchantment[6];
        enchantments[0] = Enchantments.UNBREAKING;
        enchantments[1] = Enchantments.MENDING;
        enchantments[2] = Enchantments.LOYALTY;
        enchantments[3] = Enchantments.CHANNELING;
        enchantments[4] = Enchantments.RIPTIDE;
        enchantments[5] = Enchantments.IMPALING;

        return enchantments;
    }

    private static boolean isBow(Item item) {
        return item.equals(Items.BOW);
    }

    private static Enchantment[] BowEnchantments() {
        Enchantment[] enchantments = new Enchantment[6];
        enchantments[0] = Enchantments.UNBREAKING;
        enchantments[1] = Enchantments.MENDING;
        enchantments[2] = Enchantments.INFINITY;
        enchantments[3] = Enchantments.FLAME;
        enchantments[4] = Enchantments.PUNCH;
        enchantments[5] = Enchantments.POWER;

        return enchantments;
    }


}
