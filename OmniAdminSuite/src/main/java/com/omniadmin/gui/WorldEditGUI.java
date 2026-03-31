package com.omniadmin.gui;

import com.omniadmin.OmniAdminSuite;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.*;

public class WorldEditGUI extends GuiBase implements Listener {

    private final OmniAdminSuite plugin;
    private final GUIRegistry registry;

    // State: what block the admin is looking at for replacement
    private final Map<UUID, Block> targetBlock = new HashMap<>();
    // State: currently selected replacement material
    private final Map<UUID, Material> replaceMaterial = new HashMap<>();

    public WorldEditGUI(OmniAdminSuite plugin) {
        this.plugin   = plugin;
        this.registry = plugin.getMainMenu().registry;
    }

    // ──────────────────────────────────────────────────────────────
    //  MAIN WORLD PANEL
    // ──────────────────────────────────────────────────────────────
    public void open(Player admin) {
        World w = admin.getWorld();
        Inventory gui = Bukkit.createInventory(null, 54,
                Component.text("§a§lWorld Control §8// §7" + w.getName()));

        // Row 0: Block manipulation
        gui.setItem(0,  make(Material.GRASS_BLOCK,     "§a§lReplace Looked-At Block",  "§7Changes the block you are","§7currently looking at.","§eClick to open block picker"));
        gui.setItem(1,  make(Material.TNT,             "§c§lExplode at Your Location",  "§7Summons a massive explosion","§7at your feet (admin-safe)"));
        gui.setItem(2,  make(Material.COMMAND_BLOCK,   "§e§lFill Area (5x5 Sphere)",    "§7Fill a 5-radius sphere","§7around you with chosen block"));
        gui.setItem(3,  make(Material.DIAMOND_PICKAXE, "§b§lClear Area (5x5 Sphere)",   "§7Remove all blocks in 5-radius","§7sphere around you → AIR"));
        gui.setItem(4,  make(Material.WATER_BUCKET,    "§9§lFlood Area (5x5)",          "§7Fill 5-radius sphere with water"));
        gui.setItem(5,  make(Material.LAVA_BUCKET,     "§6§lLava Flood Area (5x5)",     "§7Fill 5-radius sphere with lava"));
        gui.setItem(6,  make(Material.OAK_SAPLING,     "§2§lGrow All Plants Nearby",    "§7Instantly grows all crops","§7and saplings in 10-block radius"));
        gui.setItem(7,  make(Material.FIRE_CHARGE,     "§c§lBurn Area (5x5)",           "§7Set everything in sphere on fire"));
        gui.setItem(8,  make(Material.STRUCTURE_VOID,  "§8§lUndo Last Fill",            "§7Attempts to undo last fill op","§7(stores one layer in memory)"));

        // Row 1: Weather & Time
        gui.setItem(9,  make(Material.SUN_FLOWER,      "§e§lSet Day",                  "§7Time → 1000 (midday)"));
        gui.setItem(10, make(Material.ENDER_EYE,       "§8§lSet Night",                "§7Time → 13000 (midnight)"));
        gui.setItem(11, make(Material.ICE,             "§b§lSet Dusk",                 "§7Time → 12000 (sunset)"));
        gui.setItem(12, make(Material.GLASS,           "§f§lClear Weather",            "§7Sun, no rain, no thunder","§7Duration: 1 hour"));
        gui.setItem(13, make(Material.WATER_BUCKET,    "§9§lStart Rain",               "§7Begin downpour","§7Duration: 1 hour"));
        gui.setItem(14, make(Material.LIGHTNING_ROD,   "§e§lThunderstorm",             "§7Activate thunder + rain","§7Duration: 1 hour"));
        gui.setItem(15, make(Material.CLOCK,           "§7§lFreeze Time",              "§7Toggle doDaylightCycle","§7Current: " + (w.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) ? "§aON" : "§cOFF")));
        gui.setItem(16, make(Material.DIAMOND,         "§b§lSet Custom Time",          "§7Open time-picker"));
        gui.setItem(17, make(Material.NETHER_STAR,     "§6§lTime Speed x10",           "§7Set tickSpeed to 100 for 5s"));

        // Row 2: Gamerules
        gui.setItem(18, make(Material.SKELETON_SKULL,  "§f§lToggle keepInventory",     "§7Current: "+(w.getGameRuleValue(GameRule.KEEP_INVENTORY)?"§aON":"§cOFF")));
        gui.setItem(19, make(Material.ZOMBIE_HEAD,     "§c§lToggle Mob Spawning",      "§7Current: "+(w.getGameRuleValue(GameRule.DO_MOB_SPAWNING)?"§aON":"§cOFF")));
        gui.setItem(20, make(Material.DIAMOND_SWORD,   "§e§lToggle PvP",               "§7Uses plugin-level PvP flag"));
        gui.setItem(21, make(Material.CRAFTING_TABLE,  "§a§lToggle Drops",             "§7doMobLoot / doTileDrops","§7Current drops: "+(w.getGameRuleValue(GameRule.DO_MOB_LOOT)?"§aON":"§cOFF")));
        gui.setItem(22, make(Material.FIRE,            "§c§lToggle Fire Spread",       "§7Current: "+(w.getGameRuleValue(GameRule.DO_FIRE_TICK)?"§aON":"§cOFF")));
        gui.setItem(23, make(Material.TNT,             "§c§lToggle TNT Damage",        "§7Current: "+(w.getGameRuleValue(GameRule.TNT_EXPLODES)?"§aON":"§cOFF")));
        gui.setItem(24, make(Material.OAK_SAPLING,     "§2§lToggle Leaf Decay",        "§7Current: "+(w.getGameRuleValue(GameRule.LEAVES_DECAY)?"§aON":"§cOFF")));
        gui.setItem(25, make(Material.SAND,            "§6§lToggle Gravity",           "§7Toggles randomTickSpeed 0/3"));
        gui.setItem(26, make(Material.BOOK,            "§d§lMore Gamerules →",         "§7Open extended gamerule panel"));

        // Row 3: Biome / World operations
        gui.setItem(27, make(Material.JUNGLE_SAPLING,  "§2§lBiome: Jungle",            "§7Set your chunk biome to Jungle"));
        gui.setItem(28, make(Material.CACTUS,          "§6§lBiome: Desert",            "§7Set your chunk biome to Desert"));
        gui.setItem(29, make(Material.SNOWBALL,        "§f§lBiome: Snowy Plains",      "§7Set your chunk biome to Snowy Plains"));
        gui.setItem(30, make(Material.LILY_PAD,        "§a§lBiome: Swamp",             "§7Set your chunk biome to Swamp"));
        gui.setItem(31, make(Material.SPONGE,          "§e§lBiome: Beach",             "§7Set your chunk biome to Beach"));
        gui.setItem(32, make(Material.NETHERRACK,      "§c§lBiome: Nether Wastes",     "§7Set your chunk biome to Nether"));
        gui.setItem(33, make(Material.END_STONE,       "§8§lBiome: The End",           "§7Set your chunk biome to End"));
        gui.setItem(34, make(Material.OAK_SAPLING,     "§2§lBiome: Forest",            "§7Set your chunk biome to Forest"));
        gui.setItem(35, make(Material.BLUE_ORCHID,     "§9§lBiome: Ocean",             "§7Set your chunk biome to Ocean"));

        // Row 4: Advanced world ops
        gui.setItem(36, make(Material.BEACON,          "§b§lSpawn Lightning Storm",    "§730 lightning strikes","§7in random 20-block radius"));
        gui.setItem(37, make(Material.BONE_MEAL,       "§f§lGrow Entire Chunk",        "§7Bonemeal every growable block"));
        gui.setItem(38, make(Material.DIRT,            "§8§lFlatten Chunk",            "§7Replace everything > y=64 with air","§7(surface only)"));
        gui.setItem(39, make(Material.SPONGE,          "§9§lDrain Water (Chunk)",      "§7Remove all water blocks in chunk"));
        gui.setItem(40, make(Material.OBSIDIAN,        "§8§lFreeze Water (Chunk)",     "§7Convert water → ice in chunk"));
        gui.setItem(41, make(Material.ICE,             "§b§lMelt Ice (Chunk)",         "§7Convert ice → water in chunk"));
        gui.setItem(42, make(Material.CHEST,           "§6§lFill Chunk with Chests",   "§7Replaces air with a chest grid","§7(debug / loot room use)"));
        gui.setItem(43, make(Material.GRASS_BLOCK,     "§a§lRegen Surface Layer",      "§7Replaces top blocks with grass/sand"));
        gui.setItem(44, make(Material.STRUCTURE_BLOCK, "§5§lSave Chunk Snapshot",      "§7Save current chunk to memory"));

        // Nav
        fillRow(gui, 5, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(45, backButton());
        fill(gui);

        registry.get(admin.getUniqueId()).screen = GUIRegistry.Screen.WORLD_EDIT;
        admin.openInventory(gui);
    }

    // ──────────────────────────────────────────────────────────────
    //  BLOCK PICKER (Replace Looked-At Block)
    // ──────────────────────────────────────────────────────────────
    private void openBlockPicker(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54,
                Component.text("§a§lBlock Picker §8// §7Select Replacement"));

        Material[] materials = {
            Material.STONE, Material.COBBLESTONE, Material.GRASS_BLOCK, Material.DIRT,
            Material.SAND, Material.GRAVEL, Material.GLASS, Material.OAK_PLANKS,
            Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG, Material.JUNGLE_LOG,
            Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK,
            Material.NETHERRACK, Material.NETHER_BRICKS, Material.END_STONE, Material.OBSIDIAN,
            Material.CRYING_OBSIDIAN, Material.BEDROCK, Material.TNT, Material.SPONGE,
            Material.LAVA, Material.WATER, Material.ICE, Material.PACKED_ICE,
            Material.SEA_LANTERN, Material.GLOWSTONE, Material.BEACON, Material.MAGMA_BLOCK,
            Material.AIR
        };

        for (int i = 0; i < materials.length && i < 45; i++) {
            if (materials[i] == Material.AIR)
                gui.setItem(i, make(Material.BARRIER, "§cAIR (Clear Block)", "§7Removes the block"));
            else
                gui.setItem(i, make(materials[i], "§f" + formatMaterial(materials[i]), "§eClick to replace target block"));
        }

        fillRow(gui, 5, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(45, make(Material.ARROW, "§7§l« Back", ""));
        fill(gui);

        registry.get(admin.getUniqueId()).screen = GUIRegistry.Screen.BLOCK_REPLACE;
        admin.openInventory(gui);
    }

    // ──────────────────────────────────────────────────────────────
    //  CLICK HANDLER
    // ──────────────────────────────────────────────────────────────
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player admin)) return;
        if (!registry.has(admin.getUniqueId())) return;

        GUIRegistry.Screen screen = registry.get(admin.getUniqueId()).screen;
        if (screen != GUIRegistry.Screen.WORLD_EDIT && screen != GUIRegistry.Screen.BLOCK_REPLACE) return;

        e.setCancelled(true);
        if (e.getClickedInventory() == null) return;
        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) return;

        ItemStack clicked = e.getCurrentItem();
        int slot = e.getSlot();
        World w = admin.getWorld();

        if (screen == GUIRegistry.Screen.BLOCK_REPLACE) {
            handleBlockPickerClick(admin, clicked, slot);
            return;
        }

        if (isPane(clicked) || clicked == null) return;
        if (slot == 45) { plugin.getMainMenu().open(admin); return; }

        switch (slot) {
            // ── Block ops ──
            case 0 -> {
                Block b = admin.getTargetBlockExact(10);
                if (b == null) { admin.sendMessage("§cNo block in line of sight (max 10 blocks)."); return; }
                targetBlock.put(admin.getUniqueId(), b);
                admin.sendMessage("§aTargeting §f" + b.getType().name() + " §aat " + b.getX()+","+b.getY()+","+b.getZ());
                openBlockPicker(admin);
            }
            case 1 -> { createExplosion(admin, 10f); admin.sendMessage("§c💥 Explosion!"); }
            case 2 -> openFillPicker(admin, false);
            case 3 -> { fillSphere(admin, 5, Material.AIR); admin.sendMessage("§a✔ Cleared 5-radius sphere"); open(admin); }
            case 4 -> { fillSphere(admin, 5, Material.WATER); admin.sendMessage("§9✔ Flooded area"); open(admin); }
            case 5 -> { fillSphere(admin, 5, Material.LAVA); admin.sendMessage("§6✔ Lava flooded area"); open(admin); }
            case 6 -> { growNearby(admin, 10); admin.sendMessage("§2✔ Grew all plants in 10-block radius"); open(admin); }
            case 7 -> { fireSphere(admin, 5); admin.sendMessage("§c✔ Set area on fire"); open(admin); }
            // ── Time ──
            case 9  -> { w.setTime(1000);  admin.sendMessage("§e☀ Set to Day"); open(admin); }
            case 10 -> { w.setTime(13000); admin.sendMessage("§8🌙 Set to Night"); open(admin); }
            case 11 -> { w.setTime(12000); admin.sendMessage("§b🌅 Set to Dusk"); open(admin); }
            // ── Weather ──
            case 12 -> { w.setStorm(false); w.setThundering(false); w.setWeatherDuration(72000); admin.sendMessage("§f☀ Weather cleared"); open(admin); }
            case 13 -> { w.setStorm(true);  w.setThundering(false); w.setWeatherDuration(72000); admin.sendMessage("§9🌧 Rain started"); open(admin); }
            case 14 -> { w.setStorm(true);  w.setThundering(true);  w.setWeatherDuration(72000); admin.sendMessage("§e⚡ Thunderstorm!"); open(admin); }
            case 15 -> {
                boolean cur = Boolean.TRUE.equals(w.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));
                w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, !cur);
                admin.sendMessage("§7doDaylightCycle → " + (!cur ? "§aON" : "§cOFF"));
                open(admin);
            }
            case 17 -> {
                w.setGameRule(GameRule.RANDOM_TICK_SPEED, 100);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> w.setGameRule(GameRule.RANDOM_TICK_SPEED, 3), 100L);
                admin.sendMessage("§6⚡ Tick speed x10 for 5 seconds!");
                open(admin);
            }
            // ── Gamerules ──
            case 18 -> toggle(admin, w, GameRule.KEEP_INVENTORY, "keepInventory");
            case 19 -> toggle(admin, w, GameRule.DO_MOB_SPAWNING, "doMobSpawning");
            case 21 -> {
                boolean cur = Boolean.TRUE.equals(w.getGameRuleValue(GameRule.DO_MOB_LOOT));
                w.setGameRule(GameRule.DO_MOB_LOOT, !cur);
                w.setGameRule(GameRule.DO_TILE_DROPS, !cur);
                admin.sendMessage("§aDrops toggled → " + (!cur?"§aON":"§cOFF")); open(admin);
            }
            case 22 -> toggle(admin, w, GameRule.DO_FIRE_TICK, "doFireTick");
            case 23 -> toggle(admin, w, GameRule.TNT_EXPLODES, "tntExplodes");
            case 24 -> toggle(admin, w, GameRule.LEAVES_DECAY, "leavesDecay");
            case 25 -> {
                int ts = w.getGameRuleValue(GameRule.RANDOM_TICK_SPEED) == 0 ? 3 : 0;
                w.setGameRule(GameRule.RANDOM_TICK_SPEED, ts);
                admin.sendMessage("§6randomTickSpeed → " + ts); open(admin);
            }
            // ── Biomes ──
            case 27 -> setBiome(admin, Biome.JUNGLE);
            case 28 -> setBiome(admin, Biome.DESERT);
            case 29 -> setBiome(admin, Biome.SNOWY_PLAINS);
            case 30 -> setBiome(admin, Biome.SWAMP);
            case 31 -> setBiome(admin, Biome.BEACH);
            case 32 -> setBiome(admin, Biome.NETHER_WASTES);
            case 33 -> setBiome(admin, Biome.THE_END);
            case 34 -> setBiome(admin, Biome.FOREST);
            case 35 -> setBiome(admin, Biome.OCEAN);
            // ── Advanced ──
            case 36 -> {
                for (int i = 0; i < 30; i++) {
                    org.bukkit.Location l = admin.getLocation().add(
                        (Math.random()-0.5)*40, 20, (Math.random()-0.5)*40);
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> w.strikeLightning(l), (long)(Math.random()*100));
                }
                admin.sendMessage("§e⚡ Lightning storm unleashed!");
                open(admin);
            }
            case 37 -> { growChunk(admin); admin.sendMessage("§2✔ Bonemeal'd entire chunk"); open(admin); }
            case 38 -> { flattenChunk(admin); admin.sendMessage("§8✔ Flattened chunk surface"); open(admin); }
            case 39 -> { drainWater(admin); admin.sendMessage("§9✔ Drained water in chunk"); open(admin); }
            case 40 -> { freezeWater(admin); admin.sendMessage("§b✔ Froze water in chunk"); open(admin); }
            case 41 -> { meltIce(admin); admin.sendMessage("§9✔ Melted ice in chunk"); open(admin); }
            case 43 -> { regenSurface(admin); admin.sendMessage("§2✔ Regenerated surface layer"); open(admin); }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!registry.has(p.getUniqueId())) return;
        GUIRegistry.Screen sc = registry.get(p.getUniqueId()).screen;
        if (sc == GUIRegistry.Screen.WORLD_EDIT || sc == GUIRegistry.Screen.BLOCK_REPLACE) e.setCancelled(true);
    }

    // ──────────────────────────────────────────────────────────────
    //  Block Picker Click
    // ──────────────────────────────────────────────────────────────
    private void handleBlockPickerClick(Player admin, ItemStack clicked, int slot) {
        if (slot == 45) { open(admin); return; }
        if (clicked == null || isPane(clicked)) return;

        Material mat = clicked.getType() == Material.BARRIER ? Material.AIR : clicked.getType();
        Block b = targetBlock.get(admin.getUniqueId());

        if (b != null) {
            b.setType(mat);
            admin.sendMessage("§a✔ Block replaced with §f" + mat.name());
        } else {
            // Fill sphere mode
            openFillWithMaterial(admin, mat);
            return;
        }

        open(admin);
    }

    // ──────────────────────────────────────────────────────────────
    //  Fill Picker
    // ──────────────────────────────────────────────────────────────
    private void openFillPicker(Player admin, boolean isClearMode) {
        targetBlock.put(admin.getUniqueId(), null); // null = fill mode
        openBlockPicker(admin);
    }

    private void openFillWithMaterial(Player admin, Material mat) {
        fillSphere(admin, 5, mat);
        admin.sendMessage("§a✔ Filled 5-radius sphere with §f" + mat.name());
        open(admin);
    }

    // ──────────────────────────────────────────────────────────────
    //  World Operations
    // ──────────────────────────────────────────────────────────────
    private void fillSphere(Player center, int radius, Material mat) {
        org.bukkit.Location loc = center.getLocation();
        World w = loc.getWorld();
        for (int x=-radius; x<=radius; x++)
        for (int y=-radius; y<=radius; y++)
        for (int z=-radius; z<=radius; z++)
            if (x*x+y*y+z*z <= radius*radius)
                w.getBlockAt(loc.getBlockX()+x, loc.getBlockY()+y, loc.getBlockZ()+z).setType(mat);
    }

    private void fireSphere(Player center, int radius) {
        org.bukkit.Location loc = center.getLocation();
        World w = loc.getWorld();
        for (int x=-radius; x<=radius; x++)
        for (int y=0; y<=radius; y++)
        for (int z=-radius; z<=radius; z++)
            if (x*x+y*y+z*z <= radius*radius) {
                org.bukkit.block.Block b = w.getBlockAt(loc.getBlockX()+x, loc.getBlockY()+y, loc.getBlockZ()+z);
                if (b.getType() == Material.AIR) b.setType(Material.FIRE);
            }
    }

    private void createExplosion(Player center, float power) {
        center.getWorld().createExplosion(center.getLocation(), power, false, false, center);
    }

    private void growNearby(Player center, int radius) {
        org.bukkit.Location loc = center.getLocation();
        World w = loc.getWorld();
        for (int x=-radius; x<=radius; x++)
        for (int z=-radius; z<=radius; z++)
        for (int y=-5; y<=5; y++) {
            org.bukkit.block.Block b = w.getBlockAt(loc.getBlockX()+x, loc.getBlockY()+y, loc.getBlockZ()+z);
            try {
                if (b.getBlockData() instanceof org.bukkit.block.data.Ageable) {
                    org.bukkit.block.data.Ageable age = (org.bukkit.block.data.Ageable)b.getBlockData();
                    age.setAge(age.getMaximumAge());
                    b.setBlockData(age);
                }
            } catch (Exception ignored) {}
        }
    }

    private void setBiome(Player admin, Biome biome) {
        org.bukkit.Chunk chunk = admin.getLocation().getChunk();
        World w = admin.getWorld();
        for (int x=0; x<16; x++)
        for (int z=0; z<16; z++)
        for (int y=w.getMinHeight(); y<w.getMaxHeight(); y++)
            w.setBiome(chunk.getX()*16+x, y, chunk.getZ()*16+z, biome);
        admin.sendMessage("§a✔ Set chunk biome to §f" + biome.name());
        // Refresh client
        admin.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
        open(admin);
    }

    private void growChunk(Player admin) {
        org.bukkit.Chunk ch = admin.getLocation().getChunk();
        World w = admin.getWorld();
        for (int x=0; x<16; x++)
        for (int z=0; z<16; z++) {
            org.bukkit.block.Block top = w.getHighestBlockAt(ch.getX()*16+x, ch.getZ()*16+z);
            w.applyBonemeal(top.getLocation());
        }
    }

    private void flattenChunk(Player admin) {
        org.bukkit.Chunk ch = admin.getLocation().getChunk();
        World w = admin.getWorld();
        int baseY = admin.getLocation().getBlockY();
        for (int x=0; x<16; x++)
        for (int z=0; z<16; z++)
        for (int y=baseY+1; y<w.getMaxHeight(); y++)
            w.getBlockAt(ch.getX()*16+x, y, ch.getZ()*16+z).setType(Material.AIR);
    }

    private void drainWater(Player admin) {
        org.bukkit.Chunk ch = admin.getLocation().getChunk();
        World w = admin.getWorld();
        for (int x=0; x<16; x++)
        for (int z=0; z<16; z++)
        for (int y=w.getMinHeight(); y<w.getMaxHeight(); y++) {
            org.bukkit.block.Block b = w.getBlockAt(ch.getX()*16+x, y, ch.getZ()*16+z);
            if (b.getType()==Material.WATER) b.setType(Material.AIR);
        }
    }

    private void freezeWater(Player admin) {
        org.bukkit.Chunk ch = admin.getLocation().getChunk();
        World w = admin.getWorld();
        for (int x=0; x<16; x++)
        for (int z=0; z<16; z++)
        for (int y=w.getMinHeight(); y<w.getMaxHeight(); y++) {
            org.bukkit.block.Block b = w.getBlockAt(ch.getX()*16+x, y, ch.getZ()*16+z);
            if (b.getType()==Material.WATER) b.setType(Material.ICE);
        }
    }

    private void meltIce(Player admin) {
        org.bukkit.Chunk ch = admin.getLocation().getChunk();
        World w = admin.getWorld();
        for (int x=0; x<16; x++)
        for (int z=0; z<16; z++)
        for (int y=w.getMinHeight(); y<w.getMaxHeight(); y++) {
            org.bukkit.block.Block b = w.getBlockAt(ch.getX()*16+x, y, ch.getZ()*16+z);
            if (b.getType()==Material.ICE||b.getType()==Material.PACKED_ICE||b.getType()==Material.BLUE_ICE) b.setType(Material.WATER);
        }
    }

    private void regenSurface(Player admin) {
        org.bukkit.Chunk ch = admin.getLocation().getChunk();
        World w = admin.getWorld();
        for (int x=0; x<16; x++)
        for (int z=0; z<16; z++) {
            int topY = w.getHighestBlockYAt(ch.getX()*16+x, ch.getZ()*16+z);
            org.bukkit.block.Block top = w.getBlockAt(ch.getX()*16+x, topY, ch.getZ()*16+z);
            if (top.getType()==Material.DIRT) top.setType(Material.GRASS_BLOCK);
        }
    }

    private <T extends Comparable<T>> void toggle(Player admin, World w, GameRule<Boolean> rule, String name) {
        boolean cur = Boolean.TRUE.equals(w.getGameRuleValue(rule));
        w.setGameRule(rule, !cur);
        admin.sendMessage("§a" + name + " → " + (!cur ? "§aON" : "§cOFF"));
        open(admin);
    }

    private String formatMaterial(Material m) {
        String raw = m.name().replace("_"," ").toLowerCase();
        String[] words = raw.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        return sb.toString().trim();
    }
}
