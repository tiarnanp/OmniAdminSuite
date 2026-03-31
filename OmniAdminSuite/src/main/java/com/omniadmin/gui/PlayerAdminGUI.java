public void openPlayerDetail(Player admin, Player t) {
    Inventory gui = Bukkit.createInventory(null, 54,
            "§e§l" + t.getName() + " §8// §7Player Detail");

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
