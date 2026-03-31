package com.omniadmin.gui;

import com.omniadmin.OmniAdminSuite;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.potion.*;

public class SelfPowersGUI extends GuiBase implements Listener {

    private final OmniAdminSuite plugin;
    private final GUIRegistry registry;

    public SelfPowersGUI(OmniAdminSuite plugin) {
        this.plugin   = plugin;
        this.registry = plugin.getMainMenu().registry;
    }

    public void open(Player admin) {
        boolean godMode   = plugin.getGodModeManager().isGod(admin);
        boolean vanish    = plugin.getVanishManager().isVanished(admin);
        boolean fly       = admin.getAllowFlight();

        Inventory gui = Bukkit.createInventory(null, 54,
                "§b§lSelf Powers §8// §7" + admin.getName());

        // Row 0: Toggles
        gui.setItem(0,  toggleButton(Material.TOTEM_OF_UNDYING, "§c§lGod Mode",   godMode));
        gui.setItem(1,  toggleButton(Material.FERMENTED_SPIDER_EYE, "§7§lVanish", vanish));
        gui.setItem(2,  toggleButton(Material.FEATHER, "§f§lFly",                 fly));
        gui.setItem(3,  make(Material.SUGAR,           "§e§lSpeed Boost",         "§7Give yourself Speed 5 (5min)"));
        gui.setItem(4,  make(Material.BLAZE_POWDER,    "§6§lStrength Boost",      "§7Give yourself Strength 5 (5min)"));
        gui.setItem(5,  make(Material.MAGMA_CREAM,     "§c§lFire Resistance",     "§7Immune to fire (5min)"));
        gui.setItem(6,  make(Material.GOLDEN_CARROT,   "§e§lNight Vision",        "§7See in the dark (5min)"));
        gui.setItem(7,  make(Material.GHAST_TEAR,      "§a§lRegeneration 4",      "§7Rapid health regen (5min)"));
        gui.setItem(8,  make(Material.NETHER_STAR,     "§d§lAll Buffs (5min)",    "§7Apply ALL positive effects","§7at max level for 5 minutes"));

        // Row 1: Inventory/Items
        gui.setItem(9,  make(Material.DIAMOND_SWORD,   "§b§lGive OP Loadout",     "§7Best gear + enchants in your inv"));
        gui.setItem(10, make(Material.CHEST,           "§6§lFill Inv w/ Food",    "§7Stack your inventory with steak"));
        gui.setItem(11, make(Material.DIAMOND,         "§b§lFill Inv w/ Diamonds","§7Stack your inventory with diamonds"));
        gui.setItem(12, make(Material.EXPERIENCE_BOTTLE,"§a§lMax XP (Level 100)", "§7Set your XP to level 100"));
        gui.setItem(13, make(Material.NETHER_STAR,     "§e§lRepair All Items",    "§7Fully repair everything you hold"));
        gui.setItem(14, make(Material.ENCHANTING_TABLE,"§5§lEnchant All Items",   "§7Max enchant entire inventory"));
        gui.setItem(15, make(Material.LAVA_BUCKET,     "§4§l⚠ Clear My Inv",     "§cWipes your own inventory"));
        gui.setItem(16, make(Material.SKELETON_SKULL,  "§8§lClear My Effects",    "§7Remove all potion effects from self"));
        gui.setItem(17, make(Material.FIRE_CHARGE,     "§c§lKill All Nearby Mobs","§7Removes all hostile mobs within","§750 blocks"));

        // Row 2: Self-management
        gui.setItem(18, make(Material.GOLDEN_APPLE,    "§a§lFull Heal Self",      "§7Health + food + saturation + air"));
        gui.setItem(19, make(Material.ENDER_PEARL,     "§d§lTP to World Spawn",   "§7Teleport yourself to 0,64,0"));
        gui.setItem(20, make(Material.COMPASS,         "§7§lTP to Last Death",    "§7Teleport to your last death point","§7(if available)"));
        gui.setItem(21, make(Material.BLAZE_ROD,       "§6§lInfinite Reach",      "§7Add 50-block reach (Speed hack)","§7via velocity trick"));
        gui.setItem(22, make(Material.EMERALD,         "§a§lAdd 1000 Gold",       "§7Credits you 1000 economy gold","§7(requires Vault)"));
        gui.setItem(23, make(Material.BOOK,            "§d§lOpen My Ender Chest", "§7View your own ender chest via GUI"));
        gui.setItem(24, make(Material.BEACON,          "§b§lSet Fly Speed",       "§7Open fly speed selector"));
        gui.setItem(25, make(Material.CLOCK,           "§6§lSet Walk Speed",      "§7Open walk speed selector"));
        gui.setItem(26, make(Material.DIAMOND_BOOTS,   "§b§lNo Fall Damage",      "§7Toggle fall damage immunity"));

        // Row 3: Creative shortcuts
        gui.setItem(27, make(Material.COMMAND_BLOCK,    "§e§lGamemode: Creative", "§7Set yourself to Creative"));
        gui.setItem(28, make(Material.GRASS_BLOCK,      "§a§lGamemode: Survival", "§7Set yourself to Survival"));
        gui.setItem(29, make(Material.SPECTRAL_ARROW,   "§7§lGamemode: Spectator","§7Set yourself to Spectator"));
        gui.setItem(30, make(Material.SHIELD,           "§b§lGamemode: Adventure","§7Set yourself to Adventure"));
        gui.setItem(31, make(Material.ENDER_EYE,        "§5§lSee Through Walls",  "§7Toggle Glowing effect on all","§7nearby players (see them)"));
        gui.setItem(32, make(Material.WITHER_SKELETON_SKULL,"§8§lToggle No-Clip", "§7Spectator mode trick: noclip","§7then back to survival"));
        gui.setItem(33, make(Material.SLIME_BALL,       "§a§lSet Jump Boost 10",  "§7Super jumps for 5 min"));
        gui.setItem(34, make(Material.LIGHTNING_ROD,    "§e§lSummon tame Lightning","§7Safe lightning at your feet","§7(no fire/damage)"));
        gui.setItem(35, make(Material.NETHER_STAR,      "§d§lAdmin Bundle",       "§7Give yourself the Admin Bundle:","§7compass, wand, kit, checklist"));

        // Nav
        fillRow(gui, 5, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(45, backButton());
        fill(gui);

        registry.get(admin.getUniqueId()).screen = GUIRegistry.Screen.SELF_POWERS;
        admin.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player admin)) return;
        if (!registry.has(admin.getUniqueId())) return;
        if (registry.get(admin.getUniqueId()).screen != GUIRegistry.Screen.SELF_POWERS) return;

        e.setCancelled(true);
        if (e.getClickedInventory() == null) return;
        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) return;
        ItemStack clicked = e.getCurrentItem();
        if (isPane(clicked) || clicked == null) return;

        int slot = e.getSlot();
        if (slot == 45) { plugin.getMainMenu().open(admin); return; }

        switch (slot) {
            // ── Toggles ──
            case 0 -> {
                plugin.getGodModeManager().toggle(admin);
                admin.sendMessage("§c§lGod Mode: " + (plugin.getGodModeManager().isGod(admin)?"§aON":"§cOFF"));
                open(admin);
            }
            case 1 -> {
                if (plugin.getVanishManager().isVanished(admin)) plugin.getVanishManager().unvanish(admin);
                else plugin.getVanishManager().vanish(admin);
                admin.sendMessage("§7Vanish: " + (plugin.getVanishManager().isVanished(admin)?"§aON":"§cOFF"));
                open(admin);
            }
            case 2 -> {
                admin.setAllowFlight(!admin.getAllowFlight());
                admin.sendMessage("§fFly: " + (admin.getAllowFlight()?"§aON":"§cOFF"));
                open(admin);
            }
            // ── Effects ──
            case 3 -> applyEffect(admin, PotionEffectType.SPEED,        6000, 4);
            case 4 -> applyEffect(admin, PotionEffectType.STRENGTH,     6000, 4);
            case 5 -> applyEffect(admin, PotionEffectType.FIRE_RESISTANCE, 6000, 0);
            case 6 -> applyEffect(admin, PotionEffectType.NIGHT_VISION, 6000, 0);
            case 7 -> applyEffect(admin, PotionEffectType.REGENERATION, 6000, 3);
            case 8 -> {
                int dur = 6000;
                admin.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, 4));
                admin.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, dur, 4));
                admin.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, dur, 0));
                admin.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, dur, 0));
                admin.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, dur, 3));
                admin.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, dur, 4));
                admin.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, dur, 3));
                admin.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, dur, 3));
                admin.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, dur, 0));
                admin.sendMessage("§d✨ All buffs applied for 5 minutes!");
            }
            // ── Items ──
            case 9  -> giveOpLoadout(admin);
            case 10 -> fillWith(admin, Material.COOKED_BEEF);
            case 11 -> fillWith(admin, Material.DIAMOND);
            case 12 -> { admin.setLevel(100); admin.sendMessage("§a✔ XP set to level 100"); }
            case 13 -> repairAll(admin);
            case 14 -> enchantAll(admin);
            case 15 -> { admin.getInventory().clear(); admin.sendMessage("§c✔ Cleared your inventory"); }
            case 16 -> {
                for (PotionEffect fx : admin.getActivePotionEffects())
                    admin.removePotionEffect(fx.getType());
                admin.sendMessage("§8✔ Cleared all effects");
            }
            case 17 -> killNearbyMobs(admin, 50);
            // ── Self-management ──
            case 18 -> {
                admin.setHealth(admin.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
                admin.setFoodLevel(20); admin.setSaturation(20); admin.setRemainingAir(admin.getMaximumAir());
                admin.sendMessage("§a✔ Fully healed");
            }
            case 19 -> {
                admin.teleport(admin.getWorld().getSpawnLocation());
                admin.sendMessage("§d✔ Teleported to world spawn");
            }
            case 21 -> admin.sendMessage("§7No-clip: set yourself to §bSpectator§7 mode to walk through blocks, then back.");
            case 24 -> openSpeedSelector(admin, "fly");
            case 25 -> openSpeedSelector(admin, "walk");
            case 26 -> {
                admin.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 0, false, false));
                admin.sendMessage("§b✔ No fall damage (Slow Fall permanent)");
            }
            case 27 -> { admin.setGameMode(GameMode.CREATIVE); admin.sendMessage("§e✔ Creative"); open(admin); }
            case 28 -> { admin.setGameMode(GameMode.SURVIVAL); admin.sendMessage("§a✔ Survival"); open(admin); }
            case 29 -> { admin.setGameMode(GameMode.SPECTATOR); admin.sendMessage("§7✔ Spectator"); open(admin); }
            case 30 -> { admin.setGameMode(GameMode.ADVENTURE); admin.sendMessage("§b✔ Adventure"); open(admin); }
            case 31 -> {
                for (Player p : Bukkit.getOnlinePlayers())
                    if (!p.equals(admin) && p.getLocation().distance(admin.getLocation()) < 50)
                        p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0, false, false));
                admin.sendMessage("§5✔ Applied Glowing to nearby players");
            }
            case 33 -> applyEffect(admin, PotionEffectType.JUMP_BOOST, 6000, 9);
            case 34 -> {
                admin.getWorld().strikeLightningEffect(admin.getLocation());
                admin.sendMessage("§e⚡ Safe lightning!");
            }
            case 35 -> giveAdminBundle(admin);
        }

        if (slot >= 3 && slot <= 8) open(admin);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!registry.has(p.getUniqueId())) return;
        if (registry.get(p.getUniqueId()).screen == GUIRegistry.Screen.SELF_POWERS) e.setCancelled(true);
    }

    // ──────────────────────────────────────────────────────────────
    //  Speed Selector
    // ──────────────────────────────────────────────────────────────
    private void openSpeedSelector(Player admin, String type) {
        Inventory gui = Bukkit.createInventory(null, 9,
                "§6§lSet " + (type.equals("fly") ? "Fly" : "Walk") + " Speed");
        float[] speeds = {0.1f,0.2f,0.3f,0.4f,0.5f,0.6f,0.7f,0.8f,1.0f};
        String[] labels = {"§7Slow","§7Normal","§f1.5x","§a2x","§a2.5x","§63x","§64x","§c5x","§4MAX"};
        Material[] icons = {Material.DIRT,Material.GRASS_BLOCK,Material.OAK_PLANKS,Material.GOLD_INGOT,
                Material.GOLD_BLOCK,Material.DIAMOND,Material.DIAMOND_BLOCK,Material.NETHER_STAR,Material.DRAGON_EGG};
        for (int i=0;i<9;i++) gui.setItem(i, make(icons[i], labels[i]+" §7("+speeds[i]+")", "§eClick to set"));
        plugin.getServer().getPluginManager().registerEvents(new SpeedClickHandler(plugin, admin, type, speeds, this), plugin);
        admin.openInventory(gui);
    }

    public static class SpeedClickHandler implements Listener {
        private final OmniAdminSuite plugin; private final Player admin; private final String type;
        private final float[] speeds; private final SelfPowersGUI parent; private boolean done=false;
        public SpeedClickHandler(OmniAdminSuite p, Player a, String t, float[] s, SelfPowersGUI pg) {
            plugin=p; admin=a; type=t; speeds=s; parent=pg;
        }
        @EventHandler public void onClick(InventoryClickEvent e) {
            if (done||!e.getWhoClicked().equals(admin)) return;
            if (e.getClickedInventory()==null||!e.getClickedInventory().equals(e.getView().getTopInventory())) {e.setCancelled(true);return;}
            e.setCancelled(true);
            int sl = e.getSlot();
            if (sl>=0&&sl<speeds.length) {
                if (type.equals("fly")) admin.setFlySpeed(speeds[sl]);
                else admin.setWalkSpeed(speeds[sl]);
                admin.sendMessage("§6✔ "+type+" speed set to §f"+speeds[sl]);
            }
            done=true; org.bukkit.event.HandlerList.unregisterAll(this);
            plugin.getServer().getScheduler().runTaskLater(plugin,()->parent.open(admin),1L);
        }
        @EventHandler public void onClose(InventoryCloseEvent e) {
            if(!e.getPlayer().equals(admin)||done) return; done=true;
            org.bukkit.event.HandlerList.unregisterAll(this);
            plugin.getServer().getScheduler().runTaskLater(plugin,()->parent.open(admin),1L);
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  Helpers
    // ──────────────────────────────────────────────────────────────
    private void applyEffect(Player p, PotionEffectType type, int ticks, int amp) {
        p.addPotionEffect(new PotionEffect(type, ticks, amp));
        p.sendMessage("§b✔ Effect applied.");
    }

    private void fillWith(Player p, Material mat) {
        p.getInventory().clear();
        for (int i=0;i<36;i++) p.getInventory().setItem(i, new ItemStack(mat, mat.getMaxStackSize()));
        p.sendMessage("§a✔ Inventory filled with §f" + mat.name());
    }

    private void repairAll(Player p) {
        for (ItemStack it : p.getInventory().getContents())
            if (it!=null&&it.getType()!=Material.AIR&&it.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable dm) {
                dm.setDamage(0); it.setItemMeta(dm);
            }
        p.sendMessage("§a✔ All items repaired");
    }

    @SuppressWarnings("deprecation")
    private void enchantAll(Player p) {
        for (ItemStack it : p.getInventory().getContents()) {
            if (it==null||it.getType()==Material.AIR) continue;
            org.bukkit.inventory.meta.ItemMeta meta = it.getItemMeta();
            if (meta==null) continue;
            for (org.bukkit.enchantments.Enchantment enc : org.bukkit.enchantments.Enchantment.values())
                try { meta.addEnchant(enc, enc.getMaxLevel(), true); } catch (Exception ignored){}
            it.setItemMeta(meta);
        }
        p.sendMessage("§5✔ Max enchanted entire inventory");
    }

    private void killNearbyMobs(Player admin, int radius) {
        int count = 0;
        for (org.bukkit.entity.Entity en : admin.getWorld().getNearbyEntities(admin.getLocation(), radius, radius, radius))
            if (en instanceof org.bukkit.entity.Monster) { en.remove(); count++; }
        admin.sendMessage("§c✔ Removed §e" + count + " §cmobs within " + radius + " blocks");
    }

    @SuppressWarnings("deprecation")
    private void giveOpLoadout(Player p) {
        p.getInventory().clear();
        Material[] weapons = {Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE,
                Material.DIAMOND_SHOVEL, Material.BOW, Material.CROSSBOW};
        for (Material m : weapons) {
            ItemStack it = new ItemStack(m);
            org.bukkit.inventory.meta.ItemMeta meta = it.getItemMeta();
            for (org.bukkit.enchantments.Enchantment enc : org.bukkit.enchantments.Enchantment.values())
                try { meta.addEnchant(enc, enc.getMaxLevel(), true); } catch (Exception ignored) {}
            it.setItemMeta(meta);
            p.getInventory().addItem(it);
        }
        // Armor
        Material[] armors = {Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET};
        for (Material m : armors) {
            ItemStack it = new ItemStack(m);
            org.bukkit.inventory.meta.ItemMeta meta = it.getItemMeta();
            for (org.bukkit.enchantments.Enchantment enc : org.bukkit.enchantments.Enchantment.values())
                try { meta.addEnchant(enc, enc.getMaxLevel(), true); } catch (Exception ignored) {}
            it.setItemMeta(meta);
        }
        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemStack legs  = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStack helm  = new ItemStack(Material.DIAMOND_HELMET);
        for (ItemStack it : new ItemStack[]{boots,legs,chest,helm}) {
            org.bukkit.inventory.meta.ItemMeta meta = it.getItemMeta();
            for (org.bukkit.enchantments.Enchantment enc : org.bukkit.enchantments.Enchantment.values())
                try { meta.addEnchant(enc, enc.getMaxLevel(), true); } catch(Exception ignored){}
            it.setItemMeta(meta);
        }
        p.getInventory().setBoots(boots); p.getInventory().setLeggings(legs);
        p.getInventory().setChestplate(chest); p.getInventory().setHelmet(helm);
        p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 64));
        p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
        p.sendMessage("§b✔ OP Loadout given!");
    }

    private void giveAdminBundle(Player p) {
        ItemStack compass = make(Material.COMPASS, "§e§lAdmin Compass", "§7Points to nearest player");
        ItemStack wand    = make(Material.BLAZE_ROD, "§5§lAdmin Wand", "§7Right-click blocks to inspect","§7Left-click to interact via GUI");
        ItemStack bundle  = make(Material.BUNDLE, "§6§lAdmin Kit", "§7Contains: compass, wand, golden apple","§7Use wisely.");
        p.getInventory().addItem(compass, wand, bundle);
        p.sendMessage("§d✔ Admin Bundle added to inventory");
    }
}
