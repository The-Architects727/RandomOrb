package net.architects.RandomItemCommandMod.config;

import com.mojang.datafixers.util.Pair;
import net.architects.RandomItemCommandMod.RandomItemCommandMod;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static int enchantMax;
    public static boolean Debug;

    public static boolean blacklistBoolean;
    public static boolean modBlacklistBoolean;

    public static boolean whitelistBoolean;


    public static String[] whitelist;
    public static String whitelistString;

    public static String[] blacklist;
    public static String blacklistString;

    public static String[] modBlacklist;
    public static String modBlacklistString;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(RandomItemCommandMod.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("Debug Mode", false), "Default: false,     Turn on to find all group ids.");
        configs.addKeyValuePair(new Pair<>("Maximum Number of Enchanments", 4), "Default: 4");
        configs.addKeyValuePair(new Pair<>("Category Blacklist Options", false), "Default: false; To block catagories of items, insert a list of the group ids. the vanilla ids are: building_blocks decorations redstone misc food brewing combat tools");
        configs.addKeyValuePair(new Pair<>("Catagory Blacklist", ""), "List for group ids that should be given in the form:       building_blocks decorations redstone     and so on");
        configs.addKeyValuePair(new Pair<>("Blacklist and Whitelist Config", ":"), "Please choose either Blacklist, or Whitelist");
        configs.addKeyValuePair(new Pair<>("Item.Blacklist", false), "Default: false");
        configs.addKeyValuePair(new Pair<>("Item.Whitelist", false), "Default: false");
        configs.addKeyValuePair(new Pair<>("Item.Blacklist.List", ""), "List for Items that should not be given in the form       minecraft:iron_ingot minecraft:emerald minecraft:diamond     ");
        configs.addKeyValuePair(new Pair<>("Item.Whitelist.List", ""), "List for Items that should be given in the form       minecraft:iron_ingot minecraft:emerald minecraft:diamond     ");
    }

    private static void assignConfigs() {

        enchantMax = CONFIG.getOrDefault("Maximum Number of Enchanments", 4);

        modBlacklistBoolean = CONFIG.getOrDefault("Category Blacklist Options", false);

        modBlacklistString = CONFIG.getOrDefault("Catagory Blacklist", "");
        modBlacklist = modBlacklistString.split(" ");

        blacklistBoolean = CONFIG.getOrDefault("Item.Blacklist", false);

        whitelistBoolean = CONFIG.getOrDefault("Item.Whitelist", false);

        blacklistString = CONFIG.getOrDefault("Item.Blacklist.List", "");
        blacklist = blacklistString.split(" ");

        whitelistString = CONFIG.getOrDefault("Item.Whitelist.List", "");
        whitelist = whitelistString.split(" ");

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }
}
