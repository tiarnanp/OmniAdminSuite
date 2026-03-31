package com.omniadmin.gui;

import com.omniadmin.OmniAdminSuite;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.*;

import java.util.*;

public class PlayerAdminGUI extends GuiBase implements Listener {

    private final OmniAdminSuite plugin;
    private final GUIRegistry registry;

    // Tracks pending item action data per admin
    private final Map<UUID, Integer>  actionSlot   = new HashMap<>();
    private final Map<UUID, String>   actionSource = new HashMap<>();

    public PlayerAdminGUI(OmniAdminSuite plugin) {
        this.plugin   = plugin;
        this.registry = plugin.getMainMenu().registry;
    }

    // ──────────────────────────────────────────────────────────────
    //  SCREEN 1 – Player List
    // ──────────────────────────────────────────────────────────────
    public void openPlayerList(Player admin) {
        Collection<? extends Player> online = Bukkit.getOnlinePlayers();
        int size = Math.max(18, (int)(Math.ceil((online.size() + 9) / 9.0) * 9));
        size = Math.min(size, 54);

        Inventory gui = Bukkit.createInventory(null, size,
                "§e§lPlayer Control §8// §7Select Target");

        int slot = 0;
        for (Player t : online) {
            if (slot >= size - 9) break;
            gui.setItem(slot++, makeHead(t));
        }

        int navRow = (size / 9 - 1) * 9;
        fillRow(gui, size / 9 - 1, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(navRow, backButton());
        fill(gui);

        setScreen(admin, GUIRegistry.Screen.PLAYER_LIST);
        admin.openInventory(gui);
    }

    // ──────────────────────────────────────────────────────────────
    //  SCREEN 2 – Player Detail (HUD-style overview + action buttons)
    // ──────────────────────────────────────────────────────────────
    public void openPlayerDetail(Player admin, Player t) {
        Inventory gui = Bukkit.createInventory(null, 54,
                "§e§l" + t.getName( + " §8// §7Player Detail"));

        // ── Row 0: Player info head ──────────────────────────────
        gui.setItem(4, makeDetailHead(t));

        // ── Row 1-2: Quick action buttons ───────────────────────
        gui.setItem(9,  make(Material.CHEST,            "§a§lView Inventory",        "§7See and edit all items"));
        gui.setItem(10, make(Material.ENDER_CHEST,      "§5§lView Ender Chest",      "§7See and edit ender chest"));
        gui.setItem(11, make(Material.POTION,           "§b§lManage Effects",        "§7View/add/remove potion effects"));
        gui.setItem(12, make(Material.IRON_CHESTPLATE,  "§c§lStrip Armor",           "§7Instantly removes all armor","§7and offhand (drops at their feet)"));
        gui.setItem(13, make(Material.IRON_SWORD,       "§c§lDisarm",               "§7Remove hotbar item 1 (main hand)"));
        gui.setItem(14, make(Material.ENDER_PEARL,      "§d§lTeleport Options",      "§7TP to them, bring them,","§7send them to spawn, etc."));
        gui.setItem(15, make(Material.GOLDEN_APPLE,     "§a§lHeal & Feed",           "§7Full health + food + saturation"));
        gui.setItem(16, make(Material.BLAZE_POWDER,     "§6§lSet XP Level",          "§7Set exact XP level (0-999)"));
        gui.setItem(17, make(Material.BARRIER,          "§c§lKill Player",           "§7Instantly kills the player"));

        gui.setItem(18, make(Material.COMMAND_BLOCK,    "§b§lCycle Gamemode",        "§7Current: §f" + t.getGameMode().name()));
        gui.setItem(19, make(Material.LAVA_BUCKET,      "§4§l⚠ Clear Inventory",    "§cWipes everything including armor"));
        gui.setItem(20, make(Material.BOOK,             "§d§lCopy Their Inv to Me",  "§7Clone their inventory to yours"));
        gui.setItem(21, make(Material.WRITTEN_BOOK,     "§d§lCopy My Inv to Them",   "§7Clone your inventory to them"));
        gui.setItem(22, make(Material.FIRE_CHARGE,      "§6§lSet on Fire",           "§7Ignite for 10 seconds"));
        gui.setItem(23, make(Material.SNOWBALL,         "§b§lExtinguish",            "§7Put out any fire"));
        gui.setItem(24, make(Material.TNT,              "§c§lLightning Strike",      "§7Strike them with lightning"));
        gui.setItem(25, make(Material.FEATHER,          "§a§lLaunch Into Air",       "§7Yeeet"));
        gui.setItem(26, make(Material.NETHER_STAR,      "§e§lGive OP",              "§7Grant operator status"));

        gui.setItem(27, make(Material.OAK_SIGN,         "§7§lKick",                 "§7Kick from server"));
        gui.setItem(28, make(Material.IRON_BARS,        "§c§lBan",                  "§cPermanent ban"));
        gui.setItem(29, make(Material.CLOCK,            "§6§lTempban 1h",           "§7Ban for 1 hour"));
        gui.setItem(30, make(Material.COBWEB,           "§7§lFreeze Player",        "§7Locks them in place (slowness 255)"));
        gui.setItem(31, make(Material.BOOK,             "§a§lUnfreeze",             "§7Removes freeze effects"));
        gui.setItem(32, make(Material.BELL,             "§6§lBroadcast About Player","§7Announce something about them"));
        gui.setItem(33, make(Material.COMPASS,          "§b§lLocate Player",        "§7Prints exact coordinates"));
        gui.setItem(34, make(Material.PLAYER_HEAD,      "§e§lSpy Mode",             "§7Teleport to them invisibly"));

        // Nav row
        fillRow(gui, 5, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(45, backButton());
        fill(gui);

        setScreen(admin, GUIRegistry.Screen.PLAYER_DETAIL);
        registry.get(admin.getUniqueId()).targetId = t.getUniqueId();
        admin.openInventory(gui);
    }

    // ──────────────────────────────────────────────────────────────
    //  SCREEN 3 – Inventory Viewer
    // ──────────────────────────────────────────────────────────────
    public void openInventoryViewer(Player admin, Player t) {
        Inventory gui = Bukkit.createInventory(null, 54,
                "§2§l" + t.getName( + "'s Inventory"));

        PlayerInventory inv = t.getInventory();

        // Armor row (slots 0-4)
        gui.setItem(0, orPane(inv.getHelmet(),    "§7Helmet"));
        gui.setItem(1, orPane(inv.getChestplate(),"§7Chestplate"));
        gui.setItem(2, orPane(inv.getLeggings(),  "§7Leggings"));
        gui.setItem(3, orPane(inv.getBoots(),     "§7Boots"));
        gui.setItem(4, orPane(inv.getItemInOffHand(), "§7Offhand"));
        for (int i = 5; i < 9; i++) gui.setItem(i, make(Material.GRAY_STAINED_GLASS_PANE, " "));

        // Main inventory slots 9–35
        for (int i = 9; i <= 35; i++) gui.setItem(i, orPane(inv.getItem(i), "§8Empty"));

        // Hotbar slots 0-8 → GUI 36-44
        for (int i = 0; i < 9; i++) gui.setItem(36 + i, orPane(inv.getItem(i), "§8Empty"));

        // Nav row
        fillRow(gui, 5, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(45, make(Material.ARROW, "§7§l« Back to Detail", ""));
        gui.setItem(49, make(Material.ENDER_CHEST, "§5§lSwitch to Ender Chest", ""));
        gui.setItem(53, make(Material.LAVA_BUCKET, "§4§l⚠ Clear Inventory", "§cRemoves all items"));

        setScreen(admin, GUIRegistry.Screen.PLAYER_INVENTORY);
        admin.openInventory(gui);
    }

    // ──────────────────────────────────────────────────────────────
    //  SCREEN 4 – Ender Chest Viewer
    // ──────────────────────────────────────────────────────────────
    public void openEnderChestViewer(Player admin, Player t) {
        Inventory gui = Bukkit.createInventory(null, 54,
                "§5§l" + t.getName( + "'s Ender Chest"));

        Inventory ec = t.getEnderChest();
        for (int i = 0; i < 27; i++) gui.setItem(9 + i, orPane(ec.getItem(i), "§8Empty"));

        for (int i = 0; i < 9; i++) gui.setItem(i, make(Material.PURPLE_STAINED_GLASS_PANE, " "));
        fillRow(gui, 4, Material.PURPLE_STAINED_GLASS_PANE);
        fillRow(gui, 5, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(45, make(Material.ARROW, "§7§l« Back to Detail", ""));
        gui.setItem(49, make(Material.CHEST, "§2§lSwitch to Main Inventory", ""));
        gui.setItem(53, make(Material.LAVA_BUCKET, "§4§l⚠ Clear Ender Chest", "§cRemoves all ender chest items"));

        setScreen(admin, GUIRegistry.Screen.PLAYER_ENDERCHEST);
        admin.openInventory(gui);
    }

    // ──────────────────────────────────────────────────────────────
    //  SCREEN 5 – Effects Manager
    // ──────────────────────────────────────────────────────────────
    public void openEffectsMenu(Player admin, Player t) {
        Inventory gui = Bukkit.createInventory(null, 54,
                "§b§l" + t.getName( + "'s Effects"));

        // Show current active effects
        int slot = 0;
        for (PotionEffect fx : t.getActivePotionEffects()) {
            Material icon = potionIcon(fx.getType());
            int durationSecs = fx.getDuration() / 20;
            gui.setItem(slot++, make(icon,
                    "§b" + fx.getType().translationKey(),
                    "§7Amplifier: §f" + (fx.getAmplifier() + 1),
                    "§7Duration: §f" + (fx.getDuration() == Integer.MAX_VALUE ? "∞" : durationSecs + "s"),
                    "§cLeft-click §7to remove"));
        }

        // Fill rest with placeholders
        for (int i = slot; i < 36; i++) gui.setItem(i, make(Material.GRAY_STAINED_GLASS_PANE, "§8No effect"));

        // Common effects to apply (bottom rows)
        fillRow(gui, 4, Material.CYAN_STAINED_GLASS_PANE);
        gui.setItem(36, make(Material.GOLDEN_APPLE,   "§a§lApply: Speed 2 (1m)",    "§7Fast movement"));
        gui.setItem(37, make(Material.DIAMOND,        "§b§lApply: Strength 2 (1m)", "§7Strong attacks"));
        gui.setItem(38, make(Material.IRON_CHESTPLATE,"§7§lApply: Resistance 4 (1m)","§7Damage resistance"));
        gui.setItem(39, make(Material.FEATHER,        "§f§lApply: Slow Fall (1m)",  "§7No fall damage"));
        gui.setItem(40, make(Material.CLOCK,          "§6§lApply: Haste 2 (1m)",    "§7Fast mining"));
        gui.setItem(41, make(Material.BONE,           "§8§lApply: Wither (10s)",    "§7Damage-over-time"));
        gui.setItem(42, make(Material.FIRE_CHARGE,    "§c§lApply: Poison 4 (10s)",  "§7Toxic damage"));
        gui.setItem(43, make(Material.NETHER_STAR,    "§e§lApply: Glowing (1m)",    "§7Visible through walls"));
        gui.setItem(44, make(Material.REDSTONE,       "§c§lClear ALL Effects",      "§7Remove every active effect"));

        fillRow(gui, 5, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(45, make(Material.ARROW, "§7§l« Back to Detail", ""));

        setScreen(admin, GUIRegistry.Screen.PLAYER_EFFECTS);
        admin.openInventory(gui);
    }

    // ──────────────────────────────────────────────────────────────
    //  SCREEN 6 – Item Action Menu
    // ──────────────────────────────────────────────────────────────
    private void openItemAction(Player admin, Player t, ItemStack item, int srcSlot, String src) {
        Inventory gui = Bukkit.createInventory(null, 27,
                "§c§lItem: §f" + plainName(item));

        gui.setItem(13, item.clone());

        gui.setItem(10, make(Material.BARRIER,          "§c§lRemove Item",       "§7Deletes from " + t.getName() + "'s inventory"));
        gui.setItem(11, make(Material.ENDER_PEARL,      "§b§lCopy to My Inv",   "§7Gives you a copy"));
        gui.setItem(12, make(Material.HOPPER,           "§e§lMove to My Inv",   "§7Takes from them, gives to you"));
        gui.setItem(14, make(Material.NETHER_STAR,      "§6§lDuplicate for Them","§7Adds another copy to their inv"));
        gui.setItem(15, make(Material.ANVIL,            "§d§lMax Stack",        "§7Sets to maximum stack size"));
        gui.setItem(16, make(Material.DIAMOND_SWORD,    "§5§lEnchant All",      "§7Applies top-tier enchants to item"));
        gui.setItem(22, make(Material.ARROW, "§7§l« Back", ""));

        fillEmpty(gui, 27);

        actionSlot.put(admin.getUniqueId(), srcSlot);
        actionSource.put(admin.getUniqueId(), src);
        setScreen(admin, GUIRegistry.Screen.ITEM_ACTION);
        admin.openInventory(gui);
    }

    // ──────────────────────────────────────────────────────────────
    //  CLICK ROUTING
    // ──────────────────────────────────────────────────────────────
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player admin)) return;
        if (!registry.has(admin.getUniqueId())) return;

        GUIRegistry.Session s = registry.get(admin.getUniqueId());
        if (!isPlayerAdminScreen(s.screen)) return;

        e.setCancelled(true);
        if (e.getClickedInventory() == null) return;
        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) return;

        ItemStack clicked = e.getCurrentItem();
        int slot = e.getSlot();
        Player t = s.targetId != null ? Bukkit.getPlayer(s.targetId) : null;

        switch (s.screen) {
            case PLAYER_LIST     -> handlePlayerListClick(admin, clicked);
            case PLAYER_DETAIL   -> handleDetailClick(admin, t, slot);
            case PLAYER_INVENTORY-> handleInvViewClick(admin, t, clicked, slot);
            case PLAYER_ENDERCHEST->handleECViewClick(admin, t, clicked, slot);
            case PLAYER_EFFECTS  -> handleEffectsClick(admin, t, clicked, slot);
            case ITEM_ACTION     -> handleItemActionClick(admin, t, slot);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player admin)) return;
        if (!registry.has(admin.getUniqueId())) return;
        if (isPlayerAdminScreen(registry.get(admin.getUniqueId()).screen)) e.setCancelled(true);
    }

    // ──────────────────────────────────────────────────────────────
    //  HANDLER: Player List Click
    // ──────────────────────────────────────────────────────────────
    private void handlePlayerListClick(Player admin, ItemStack clicked) {
        if (clicked == null || isPane(clicked)) return;

        if (clicked.getType() == Material.ARROW) {
            plugin.getMainMenu().open(admin);
            return;
        }

        if (!(clicked.getItemMeta() instanceof SkullMeta sm)) return;
        if (sm.getOwnerProfile() == null || sm.getOwnerProfile().getName() == null) return;
        Player t = Bukkit.getPlayerExact(sm.getOwnerProfile().getName());
        if (t == null) { admin.sendMessage("§cPlayer went offline."); openPlayerList(admin); return; }

        registry.get(admin.getUniqueId()).targetId = t.getUniqueId();
        openPlayerDetail(admin, t);
    }

    // ──────────────────────────────────────────────────────────────
    //  HANDLER: Player Detail Click
    // ──────────────────────────────────────────────────────────────
    private void handleDetailClick(Player admin, Player t, int slot) {
        if (t == null) { admin.sendMessage("§cTarget went offline."); openPlayerList(admin); return; }

        switch (slot) {
            case 9  -> openInventoryViewer(admin, t);
            case 10 -> openEnderChestViewer(admin, t);
            case 11 -> openEffectsMenu(admin, t);
            case 12 -> {
                // Strip armor – drops items at their feet
                PlayerInventory inv = t.getInventory();
                dropIfExists(t, inv.getHelmet());    inv.setHelmet(null);
                dropIfExists(t, inv.getChestplate());inv.setChestplate(null);
                dropIfExists(t, inv.getLeggings());  inv.setLeggings(null);
                dropIfExists(t, inv.getBoots());     inv.setBoots(null);
                dropIfExists(t, inv.getItemInOffHand()); inv.setItemInOffHand(new org.bukkit.inventory.ItemStack(Material.AIR));
                t.sendMessage("§c§lYour armor was stripped by an admin.");
                admin.sendMessage("§a✔ Stripped all armor from §e" + t.getName());
                refresh(admin, t, GUIRegistry.Screen.PLAYER_DETAIL);
            }
            case 13 -> {
                // Disarm main hand
                ItemStack main = t.getInventory().getItemInMainHand();
                if (main.getType() != Material.AIR) {
                    dropIfExists(t, main);
                    t.getInventory().setItemInMainHand(new org.bukkit.inventory.ItemStack(Material.AIR));
                    admin.sendMessage("§a✔ Disarmed §e" + t.getName());
                } else {
                    admin.sendMessage("§e" + t.getName() + " §7has nothing in main hand.");
                }
            }
            case 14 -> openTeleportOptions(admin, t);
            case 15 -> {
                t.setHealth(Objects.requireNonNull(t.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                t.setFoodLevel(20); t.setSaturation(20f);
                t.sendMessage("§aHealed by an admin.");
                admin.sendMessage("§a✔ Healed & fed §e" + t.getName());
                refresh(admin, t, GUIRegistry.Screen.PLAYER_DETAIL);
            }
            case 16 -> openXpSetter(admin, t);
            case 17 -> { t.setHealth(0); admin.sendMessage("§a✔ Killed §e" + t.getName()); refresh(admin, t, GUIRegistry.Screen.PLAYER_DETAIL); }
            case 18 -> {
                GameMode next = switch(t.getGameMode()) {
                    case SURVIVAL -> GameMode.CREATIVE; case CREATIVE -> GameMode.ADVENTURE;
                    case ADVENTURE -> GameMode.SPECTATOR; case SPECTATOR -> GameMode.SURVIVAL;
                };
                t.setGameMode(next);
                t.sendMessage("§eGamemode set to §b" + next.name() + " §eby admin.");
                admin.sendMessage("§a✔ Set §e" + t.getName() + " §ato §b" + next.name());
                refresh(admin, t, GUIRegistry.Screen.PLAYER_DETAIL);
            }
            case 19 -> {
                t.getInventory().clear();
                t.sendMessage("§cYour inventory was cleared by an admin.");
                admin.sendMessage("§a✔ Cleared §e" + t.getName() + "'s inventory");
            }
            case 20 -> {
                copyInv(t, admin); admin.sendMessage("§a✔ Copied §e" + t.getName() + "'s inventory to yours");
            }
            case 21 -> {
                copyInv(admin, t); t.sendMessage("§eAdmin gave you their inventory.");
                admin.sendMessage("§a✔ Copied your inventory to §e" + t.getName());
            }
            case 22 -> {
                t.setFireTicks(200);
                admin.sendMessage("§a✔ Ignited §e" + t.getName());
            }
            case 23 -> {
                t.setFireTicks(0);
                admin.sendMessage("§a✔ Extinguished §e" + t.getName());
            }
            case 24 -> {
                t.getWorld().strikeLightning(t.getLocation());
                admin.sendMessage("§a✔ Struck §e" + t.getName() + " §awith lightning");
            }
            case 25 -> {
                t.setVelocity(new org.bukkit.util.Vector(0, 3, 0));
                admin.sendMessage("§a✔ Launched §e" + t.getName() + " §ainto the air");
            }
            case 26 -> {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "op " + t.getName());
                admin.sendMessage("§a✔ Granted OP to §e" + t.getName());
            }
            case 27 -> {
                t.kickPlayer("§cYou were kicked by an admin.");
                admin.sendMessage("§a✔ Kicked §e" + t.getName());
                openPlayerList(admin);
            }
            case 28 -> {
                plugin.getServer().getBanList(org.bukkit.BanList.Type.NAME).addBan(t.getName(), "Banned by admin", null, "Admin");
                t.kickPlayer("§cYou have been permanently banned.");
                admin.sendMessage("§a✔ Banned §e" + t.getName());
                openPlayerList(admin);
            }
            case 29 -> {
                java.util.Date exp = new java.util.Date(System.currentTimeMillis() + 3600000);
                plugin.getServer().getBanList(org.bukkit.BanList.Type.NAME).addBan(t.getName(), "Tempbanned 1h by admin", exp, "Admin");
                t.kickPlayer("§cYou have been banned for 1 hour.");
                admin.sendMessage("§a✔ Tempbanned §e" + t.getName() + " §afor 1 hour");
                openPlayerList(admin);
            }
            case 30 -> {
                t.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 255, false, false));
                t.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, -10, false, false));
                t.sendMessage("§cYou have been frozen by an admin.");
                admin.sendMessage("§a✔ Froze §e" + t.getName());
            }
            case 31 -> {
                t.removePotionEffect(PotionEffectType.SLOWNESS);
                t.removePotionEffect(PotionEffectType.JUMP_BOOST);
                t.sendMessage("§aYou have been unfrozen.");
                admin.sendMessage("§a✔ Unfroze §e" + t.getName());
            }
            case 32 -> {
                Bukkit.broadcastMessage("§e§l[ADMIN] §fPlayer §e" + t.getName( + " §fis under admin review."));
                admin.sendMessage("§aBroadcast sent.");
            }
            case 33 -> {
                org.bukkit.Location loc = t.getLocation();
                admin.sendMessage("§e" + t.getName() + " §7is at §f" + loc.getWorld().getName()
                        + " §7X:§f" + (int)loc.getX() + " §7Y:§f" + (int)loc.getY() + " §7Z:§f" + (int)loc.getZ());
            }
            case 34 -> {
                plugin.getVanishManager().vanish(admin);
                admin.teleport(t.getLocation());
                admin.sendMessage("§7[Spy Mode] Teleported to §e" + t.getName() + " §7while vanished. §7/oas to toggle vanish off.");
                admin.closeInventory();
            }
            case 45 -> openPlayerList(admin);
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  HANDLER: Inventory View Click
    // ──────────────────────────────────────────────────────────────
    private void handleInvViewClick(Player admin, Player t, ItemStack clicked, int slot) {
        if (t == null) { admin.sendMessage("§cTarget offline."); openPlayerList(admin); return; }

        if (slot == 45) { openPlayerDetail(admin, t); return; }
        if (slot == 49) { openEnderChestViewer(admin, t); return; }
        if (slot == 53) {
            t.getInventory().clear();
            admin.sendMessage("§a✔ Cleared §e" + t.getName() + "'s inventory");
            openInventoryViewer(admin, t);
            return;
        }
        if (slot >= 45) return;

        if (clicked == null || isPane(clicked)) return;

        int playerSlot = guiToPlayerSlot(slot);
        if (playerSlot == -1) return;

        openItemAction(admin, t, clicked, playerSlot, "inventory");
    }

    // ──────────────────────────────────────────────────────────────
    //  HANDLER: Ender Chest View Click
    // ──────────────────────────────────────────────────────────────
    private void handleECViewClick(Player admin, Player t, ItemStack clicked, int slot) {
        if (t == null) { admin.sendMessage("§cTarget offline."); openPlayerList(admin); return; }

        if (slot == 45) { openPlayerDetail(admin, t); return; }
        if (slot == 49) { openInventoryViewer(admin, t); return; }
        if (slot == 53) {
            t.getEnderChest().clear();
            admin.sendMessage("§a✔ Cleared §e" + t.getName() + "'s ender chest");
            openEnderChestViewer(admin, t);
            return;
        }
        if (slot < 9 || slot > 35 || clicked == null || isPane(clicked)) return;

        openItemAction(admin, t, clicked, slot - 9, "enderchest");
    }

    // ──────────────────────────────────────────────────────────────
    //  HANDLER: Effects Click
    // ──────────────────────────────────────────────────────────────
    private void handleEffectsClick(Player admin, Player t, ItemStack clicked, int slot) {
        if (t == null) { admin.sendMessage("§cTarget offline."); openPlayerList(admin); return; }
        if (slot == 45) { openPlayerDetail(admin, t); return; }

        if (slot < 36 && clicked != null && !isPane(clicked)) {
            // Remove effect by matching position
            List<PotionEffect> effects = new ArrayList<>(t.getActivePotionEffects());
            if (slot < effects.size()) {
                t.removePotionEffect(effects.get(slot).getType());
                admin.sendMessage("§a✔ Removed effect from §e" + t.getName());
            }
            openEffectsMenu(admin, t);
            return;
        }

        switch (slot) {
            case 36 -> t.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,        1200, 1));
            case 37 -> t.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,     1200, 1));
            case 38 -> t.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,   1200, 3));
            case 39 -> t.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1200, 0));
            case 40 -> t.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,        1200, 1));
            case 41 -> t.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,        200, 2));
            case 42 -> t.addPotionEffect(new PotionEffect(PotionEffectType.POISON,        200, 3));
            case 43 -> t.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,      1200, 0));
            case 44 -> {
                for (PotionEffect fx : new ArrayList<>(t.getActivePotionEffects()))
                    t.removePotionEffect(fx.getType());
                admin.sendMessage("§a✔ Cleared all effects from §e" + t.getName());
            }
        }
        if (slot >= 36 && slot <= 43) admin.sendMessage("§a✔ Applied effect to §e" + t.getName());
        openEffectsMenu(admin, t);
    }

    // ──────────────────────────────────────────────────────────────
    //  HANDLER: Item Action Click
    // ──────────────────────────────────────────────────────────────
    private void handleItemActionClick(Player admin, Player t, int slot) {
        if (t == null) { admin.sendMessage("§cTarget offline."); openPlayerList(admin); return; }

        int srcSlot = actionSlot.getOrDefault(admin.getUniqueId(), -1);
        String src  = actionSource.getOrDefault(admin.getUniqueId(), "inventory");

        Inventory srcInv = src.equals("enderchest") ? t.getEnderChest() : t.getInventory();
        ItemStack target = srcInv.getItem(srcSlot);

        switch (slot) {
            case 10 -> { srcInv.setItem(srcSlot, null); admin.sendMessage("§c✔ Removed item."); }
            case 11 -> { if (target!=null){ admin.getInventory().addItem(target.clone()); admin.sendMessage("§b✔ Copied to your inventory."); } }
            case 12 -> { if (target!=null){ admin.getInventory().addItem(target.clone()); srcInv.setItem(srcSlot,null); admin.sendMessage("§e✔ Moved to your inventory."); } }
            case 14 -> { if (target!=null){ t.getInventory().addItem(target.clone()); admin.sendMessage("§6✔ Duplicated for "+t.getName()); } }
            case 15 -> { if (target!=null){ target.setAmount(target.getMaxStackSize()); srcInv.setItem(srcSlot,target); admin.sendMessage("§6✔ Maxed stack."); } }
            case 16 -> { if (target!=null){ applyMaxEnchants(target); srcInv.setItem(srcSlot,target); admin.sendMessage("§5✔ Enchanted item."); } }
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (src.equals("enderchest")) openEnderChestViewer(admin, t);
            else openInventoryViewer(admin, t);
        }, 1L);
    }

    // ──────────────────────────────────────────────────────────────
    //  Teleport Options
    // ──────────────────────────────────────────────────────────────
    private void openTeleportOptions(Player admin, Player t) {
        Inventory gui = Bukkit.createInventory(null, 27,
                "§d§lTeleport: §e" + t.getName());

        gui.setItem(10, make(Material.ENDER_PEARL,  "§d§lTP to Them",           "§7Teleport yourself to " + t.getName()));
        gui.setItem(11, make(Material.ENDER_EYE,    "§b§lBring Them Here",      "§7Pull " + t.getName() + " to your location"));
        gui.setItem(12, make(Material.COMPASS,      "§a§lSend to World Spawn",  "§7TP " + t.getName() + " to world spawn (0,64,0)"));
        gui.setItem(13, make(Material.RED_BED,          "§e§lSend to Their Bed",    "§7TP to their last bed spawn"));
        gui.setItem(14, make(Material.RESPAWN_ANCHOR,"§5§lSend to Nether",      "§7TP " + t.getName() + " to nether spawn"));
        gui.setItem(15, make(Material.END_STONE,    "§8§lSend to The End",      "§7TP " + t.getName() + " to end spawn"));
        gui.setItem(16, make(Material.GRASS_BLOCK,  "§2§lSend to Overworld",    "§7TP " + t.getName() + " to overworld spawn"));
        gui.setItem(22, make(Material.ARROW, "§7§l« Back", ""));
        fillEmpty(gui, 27);

        setScreen(admin, GUIRegistry.Screen.NONE);  // Temporarily reuse NONE
        // Register a one-shot click handler
        plugin.getServer().getPluginManager().registerEvents(
            new TeleportClickHandler(plugin, admin, t, this), plugin);
        admin.openInventory(gui);
    }

    public static class TeleportClickHandler implements Listener {
        private final OmniAdminSuite plugin; private final Player admin, target;
        private final PlayerAdminGUI parent; private boolean done = false;

        public TeleportClickHandler(OmniAdminSuite plugin, Player admin, Player target, PlayerAdminGUI parent) {
            this.plugin=plugin; this.admin=admin; this.target=target; this.parent=parent;
        }

        @EventHandler public void onClick(InventoryClickEvent e) {
            if (done || !e.getWhoClicked().equals(admin)) return;
            if (e.getClickedInventory()==null||!e.getClickedInventory().equals(e.getView().getTopInventory())) {e.setCancelled(true);return;}
            e.setCancelled(true);
            if (e.getCurrentItem()==null||e.getCurrentItem().getType()==Material.AIR) return;
            switch(e.getSlot()) {
                case 10 -> { admin.teleport(target.getLocation()); admin.sendMessage("§a✔ Teleported to §e"+target.getName()); }
                case 11 -> { target.teleport(admin.getLocation()); target.sendMessage("§eYou were teleported by admin."); admin.sendMessage("§a✔ Brought §e"+target.getName()); }
                case 12 -> { org.bukkit.Location sp = target.getWorld().getSpawnLocation(); target.teleport(sp); admin.sendMessage("§a✔ Sent to world spawn"); }
                case 13 -> { org.bukkit.Location bed = target.getRespawnLocation(); if(bed!=null){target.teleport(bed);admin.sendMessage("§a✔ Sent to bed spawn");}else admin.sendMessage("§eNo bed spawn set."); }
                case 14 -> { org.bukkit.World nether=plugin.getServer().getWorld("world_nether"); if(nether!=null){target.teleport(nether.getSpawnLocation());admin.sendMessage("§a✔ Sent to Nether");}else admin.sendMessage("§cNether world not found."); }
                case 15 -> { org.bukkit.World end=plugin.getServer().getWorld("world_the_end"); if(end!=null){target.teleport(end.getSpawnLocation());admin.sendMessage("§a✔ Sent to The End");}else admin.sendMessage("§cEnd world not found."); }
                case 16 -> { org.bukkit.World ow=plugin.getServer().getWorld("world"); if(ow!=null){target.teleport(ow.getSpawnLocation());admin.sendMessage("§a✔ Sent to Overworld");}else admin.sendMessage("§cOverworld not found."); }
                case 22 -> {}
            }
            done=true; org.bukkit.event.HandlerList.unregisterAll(this);
            plugin.getServer().getScheduler().runTaskLater(plugin,()->parent.openPlayerDetail(admin,target),1L);
        }
        @EventHandler public void onClose(InventoryCloseEvent e) {
            if(!e.getPlayer().equals(admin)||done)return; done=true;
            org.bukkit.event.HandlerList.unregisterAll(this);
            plugin.getServer().getScheduler().runTaskLater(plugin,()->parent.openPlayerDetail(admin,target),1L);
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  XP Setter (via book trick – gives level options)
    // ──────────────────────────────────────────────────────────────
    private void openXpSetter(Player admin, Player t) {
        Inventory gui = Bukkit.createInventory(null, 27,
                "§6§lSet XP Level: §e" + t.getName());

        int[] levels = {0,1,5,10,15,20,30,50,100};
        int[] slots  = {10,11,12,13,14,15,16,17,9};
        Material[] icons = {Material.DIRT,Material.OAK_SAPLING,Material.COAL,Material.IRON_INGOT,
                Material.GOLD_INGOT,Material.DIAMOND,Material.EMERALD,Material.NETHER_STAR,Material.DRAGON_EGG};
        for (int i=0;i<levels.length;i++)
            gui.setItem(slots[i], make(icons[i], "§6Level §e"+levels[i],"§7Set "+t.getName()+"'s XP to level "+levels[i]));
        gui.setItem(22, make(Material.ARROW,"§7§l« Back",""));
        fillEmpty(gui,27);

        plugin.getServer().getPluginManager().registerEvents(new XpClickHandler(plugin,admin,t,levels,slots,this),plugin);
        setScreen(admin, GUIRegistry.Screen.NONE);
        admin.openInventory(gui);
    }

    public static class XpClickHandler implements Listener {
        private final OmniAdminSuite plugin; private final Player admin, target;
        private final int[] levels, slots; private final PlayerAdminGUI parent; private boolean done=false;
        public XpClickHandler(OmniAdminSuite p,Player a,Player t,int[]l,int[]s,PlayerAdminGUI pg){plugin=p;admin=a;target=t;levels=l;slots=s;parent=pg;}
        @EventHandler public void onClick(InventoryClickEvent e){
            if(done||!e.getWhoClicked().equals(admin))return;
            if(e.getClickedInventory()==null||!e.getClickedInventory().equals(e.getView().getTopInventory())){e.setCancelled(true);return;}
            e.setCancelled(true); int sl=e.getSlot();
            for(int i=0;i<slots.length;i++){if(slots[i]==sl){target.setLevel(levels[i]);admin.sendMessage("§a✔ Set §e"+target.getName()+"§a to level §e"+levels[i]);break;}}
            done=true;org.bukkit.event.HandlerList.unregisterAll(this);
            plugin.getServer().getScheduler().runTaskLater(plugin,()->parent.openPlayerDetail(admin,target),1L);
        }
        @EventHandler public void onClose(InventoryCloseEvent e){if(!e.getPlayer().equals(admin)||done)return;done=true;org.bukkit.event.HandlerList.unregisterAll(this);plugin.getServer().getScheduler().runTaskLater(plugin,()->parent.openPlayerDetail(admin,target),1L);}
    }

    // ──────────────────────────────────────────────────────────────
    //  HELPERS
    // ──────────────────────────────────────────────────────────────
    private boolean isPlayerAdminScreen(GUIRegistry.Screen s) {
        return s == GUIRegistry.Screen.PLAYER_LIST || s == GUIRegistry.Screen.PLAYER_DETAIL
            || s == GUIRegistry.Screen.PLAYER_INVENTORY || s == GUIRegistry.Screen.PLAYER_ENDERCHEST
            || s == GUIRegistry.Screen.PLAYER_EFFECTS || s == GUIRegistry.Screen.ITEM_ACTION;
    }

    private void setScreen(Player admin, GUIRegistry.Screen screen) {
        registry.get(admin.getUniqueId()).screen = screen;
    }

    private void refresh(Player admin, Player t, GUIRegistry.Screen screen) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (screen == GUIRegistry.Screen.PLAYER_DETAIL) openPlayerDetail(admin, t);
        }, 1L);
    }

    private ItemStack makeHead(Player p) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) head.getItemMeta();
        sm.setOwningPlayer(p);
        double maxHp = Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        sm.setDisplayName("§e§l" + p.getName());
        sm.setLore(List.of(
            "§7HP: §c" + String.format("%.1f",p.getHealth() + "§7/§c" + (int)maxHp),
            "§7Gamemode: §f" + p.getGameMode(.name()),
            "§7Level: §a" + p.getLevel(),
            "§7World: §f" + p.getWorld(.getName()),
            "§7Ping: §f" + p.getPing( + "ms"),
            "",
            "§eClick to manage"
        ));
        head.setItemMeta(sm);
        return head;
    }

    private ItemStack makeDetailHead(Player p) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) head.getItemMeta();
        sm.setOwningPlayer(p);
        double maxHp = Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        sm.setDisplayName("§e§l" + p.getName());
        sm.setLore(List.of(
            "§7Health: §c" + String.format("%.1f",p.getHealth() + "§7/§c"+(int)maxHp),
            "§7Food: §6" + p.getFoodLevel( + "§7/§620"),
            "§7Gamemode: §f" + p.getGameMode(.name()),
            "§7XP Level: §a" + p.getLevel(),
            "§7Fly: §f" + (p.isFlying(?"§aON":"§cOFF")),
            "§7Vanish: §f" + (p.isInvisible(?"§aON":"§cOFF")),
            "§7World: §f" + p.getWorld(.getName()),
            "§7Ping: §f" + p.getPing( + "ms"),
            "§7IP: §f" + (p.getAddress(!=null?p.getAddress().getAddress().getHostAddress():"unknown")),
            "§7Active Effects: §f" + p.getActivePotionEffects(.size())
        ));
        head.setItemMeta(sm);
        return head;
    }

    private ItemStack orPane(ItemStack item, String fallbackLabel) {
        if (item == null || item.getType() == Material.AIR)
            return make(Material.GRAY_STAINED_GLASS_PANE, fallbackLabel);
        return item;
    }

    private int guiToPlayerSlot(int guiSlot) {
        if (guiSlot == 0) return -10; // helmet  – handled by armor map
        if (guiSlot == 1) return -11; // chest
        if (guiSlot == 2) return -12; // legs
        if (guiSlot == 3) return -13; // boots
        if (guiSlot == 4) return -14; // offhand
        if (guiSlot >= 9 && guiSlot <= 35)  return guiSlot;
        if (guiSlot >= 36 && guiSlot <= 44) return guiSlot - 36;
        return -1;
    }

    private void dropIfExists(Player p, ItemStack item) {
        if (item != null && item.getType() != Material.AIR)
            p.getWorld().dropItemNaturally(p.getLocation(), item);
    }

    private void copyInv(Player from, Player to) {
        to.getInventory().clear();
        PlayerInventory fi = from.getInventory(), ti = to.getInventory();
        if (fi.getHelmet()!=null)    ti.setHelmet(fi.getHelmet().clone());
        if (fi.getChestplate()!=null)ti.setChestplate(fi.getChestplate().clone());
        if (fi.getLeggings()!=null)  ti.setLeggings(fi.getLeggings().clone());
        if (fi.getBoots()!=null)     ti.setBoots(fi.getBoots().clone());
        ti.setItemInOffHand(fi.getItemInOffHand().clone());
        for (int i=0;i<36;i++){ItemStack it=fi.getItem(i);if(it!=null)ti.setItem(i,it.clone());}
    }

    @SuppressWarnings("deprecation")
    private void applyMaxEnchants(ItemStack item) {
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        for (org.bukkit.enchantments.Enchantment enc : org.bukkit.enchantments.Enchantment.values()) {
            try { meta.addEnchant(enc, enc.getMaxLevel(), true); } catch (Exception ignored) {}
        }
        item.setItemMeta(meta);
    }

    private void fillEmpty(Inventory inv, int size) {
        ItemStack pane = make(Material.BLACK_STAINED_GLASS_PANE," ");
        for (int i=0;i<size;i++) if(inv.getItem(i)==null) inv.setItem(i,pane);
    }

    private Material potionIcon(PotionEffectType type) {
        if (type==PotionEffectType.SPEED) return Material.SUGAR;
        if (type==PotionEffectType.STRENGTH) return Material.BLAZE_POWDER;
        if (type==PotionEffectType.POISON) return Material.SPIDER_EYE;
        if (type==PotionEffectType.REGENERATION) return Material.GHAST_TEAR;
        if (type==PotionEffectType.FIRE_RESISTANCE) return Material.MAGMA_CREAM;
        if (type==PotionEffectType.INVISIBILITY) return Material.FERMENTED_SPIDER_EYE;
        if (type==PotionEffectType.BLINDNESS) return Material.INK_SAC;
        if (type==PotionEffectType.NIGHT_VISION) return Material.GOLDEN_CARROT;
        if (type==PotionEffectType.WITHER) return Material.WITHER_ROSE;
        if (type==PotionEffectType.GLOWING) return Material.GLOWSTONE_DUST;
        return Material.POTION;
    }
}
