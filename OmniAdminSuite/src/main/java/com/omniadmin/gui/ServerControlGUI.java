package com.omniadmin.gui;

import com.omniadmin.OmniAdminSuite;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

public class ServerControlGUI extends GuiBase implements Listener {

    private final OmniAdminSuite plugin;
    private final GUIRegistry registry;

    public ServerControlGUI(OmniAdminSuite plugin) {
        this.plugin   = plugin;
        this.registry = plugin.getMainMenu().registry;
    }

    public void open(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54,
                "§c§lServer Control §8// §7Global Powers");

        int online   = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();

        // Row 0: Server info header
        gui.setItem(4, make(Material.BEACON, "§e§lServer Status",
                "§7Players: §f" + online + "§7/§f" + maxPlayers,
                "§7TPS: §f" + "20.0",
                "§7Worlds: §f" + Bukkit.getWorlds().size(),
                "§7Version: §f" + Bukkit.getVersion()));

        // Row 1: Broadcasts & messages
        gui.setItem(9,  make(Material.PAPER,          "§e§lBroadcast Message",     "§7Send server-wide announcement"));
        gui.setItem(10, make(Material.MAP,             "§a§lBroadcast w/ Title",    "§7Send big screen title to all"));
        gui.setItem(11, make(Material.FIREWORK_ROCKET, "§6§lServer Event Start",    "§7Announce a server event","§7with fireworks for all players"));
        gui.setItem(12, make(Material.BELL,            "§c§lWarn All Players",      "§7Broadcast a red warning message","§7with sound effect"));
        gui.setItem(13, make(Material.OAK_SIGN,        "§b§lSet MOTD",             "§7Update the server MOTD","§7(persists until restart)"));
        gui.setItem(14, make(Material.DIAMOND,         "§d§lReward All Online",     "§7Give all players 32 diamonds"));
        gui.setItem(15, make(Material.GOLDEN_APPLE,    "§a§lHeal All Players",      "§7Full health + food for everyone"));
        gui.setItem(16, make(Material.COOKED_BEEF,     "§6§lFeed All Players",      "§7Max food level for everyone"));
        gui.setItem(17, make(Material.NETHER_STAR,     "§e§lXP Bomb (50 levels)",   "§7Give all players 50 XP levels"));

        // Row 2: Player management
        gui.setItem(18, make(Material.ENDER_PEARL,     "§d§lTP All to Spawn",       "§7Teleport everyone to world spawn"));
        gui.setItem(19, make(Material.WITHER_SKELETON_SKULL,"§c§lKick All Players", "§cKick everyone (you stay)"));
        gui.setItem(20, make(Material.IRON_BARS,       "§8§lList Bans",             "§7Show all banned players in chat"));
        gui.setItem(21, make(Material.BOOK,            "§a§lList Whitelist",        "§7Show all whitelisted players"));
        gui.setItem(22, make(Material.OAK_FENCE_GATE,  "§6§lToggle Whitelist",      "§7Current: §f" + (Bukkit.hasWhitelist()?"§aON":"§cOFF")));
        gui.setItem(23, make(Material.WITHER_ROSE,     "§c§lClear ALL Bans",        "§cUnbans every banned player"));
        gui.setItem(24, make(Material.SKELETON_SKULL,  "§c§lBan IP Range",          "§7Ban a specific IP address","§7(type in chat after click)"));
        gui.setItem(25, make(Material.BARRIER,         "§4§l⚠ Stop Server",        "§cGracefully stops the server","§c§lIRREVERSIBLE"));
        gui.setItem(26, make(Material.TNT,             "§4§l⚠ Restart Server",     "§cIssues /restart command","§c§lIRREVERSIBLE"));

        // Row 3: World management
        gui.setItem(27, make(Material.GRASS_BLOCK,     "§a§lSave All Worlds",       "§7Force-saves all world data"));
        gui.setItem(28, make(Material.DIRT,            "§7§lSet World Spawn",       "§7Set world spawn to your location"));
        gui.setItem(29, make(Material.COMMAND_BLOCK,   "§e§lRun Console Command",   "§7Type a command to run as console","§7(type in chat after click)"));
        gui.setItem(30, make(Material.CLOCK,           "§b§lSet Server TPS Target", "§7Adjust simulation tick speed"));
        gui.setItem(31, make(Material.FIRE,            "§c§lKill All Mobs (Global)","§7Remove ALL hostile mobs","§7from every loaded chunk"));
        gui.setItem(32, make(Material.GRASS_BLOCK,     "§2§lClear All Drops",       "§7Remove all item entities","§7from every loaded chunk"));
        gui.setItem(33, make(Material.ENDER_CHEST,     "§5§lWipe All Ender Chests", "§c⚠ Clears ender chest for","§call online players"));
        gui.setItem(34, make(Material.LAVA_BUCKET,     "§4§lWipe Inventories (All)","§c⚠ Clears inventory of","§call online players"));
        gui.setItem(35, make(Material.BOOK,            "§d§lPlugin List",           "§7Show all loaded plugins in chat"));

        // Row 4: Advanced
        gui.setItem(36, make(Material.COMMAND_BLOCK,   "§b§lOP All Players",        "§7Grant OP to everyone online"));
        gui.setItem(37, make(Material.BARRIER,         "§8§lDeOP All Players",      "§7Remove OP from everyone"));
        gui.setItem(38, make(Material.BEACON,          "§e§lToggle PvP (Global)",   "§7Toggles PvP server-wide","§7via game rule"));
        gui.setItem(39, make(Material.DIAMOND_SWORD,   "§c§lKill All Players",      "§c⚠ Kills every online player"));
        gui.setItem(40, make(Material.GOLDEN_APPLE,    "§a§lRevive All Players",    "§7Heal + respawn all dead players","§7(sends to spawn with full health)"));
        gui.setItem(41, make(Material.WATER_BUCKET,    "§9§lRain on All Worlds",    "§7Set storm on all loaded worlds"));
        gui.setItem(42, make(Material.SUNFLOWER,      "§e§lDay on All Worlds",     "§7Set day on all loaded worlds"));
        gui.setItem(43, make(Material.NETHER_STAR,     "§6§lServer Broadcast Stats","§7Show online count, TPS,","§7memory usage to everyone"));
        gui.setItem(44, make(Material.EXPERIENCE_BOTTLE,"§a§lEnable All Gamerules", "§7Reset all gamerules to default"));

        // Nav
        fillRow(gui, 5, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(45, backButton());
        fill(gui);

        registry.get(admin.getUniqueId()).screen = GUIRegistry.Screen.SERVER_CONTROL;
        admin.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player admin)) return;
        if (!registry.has(admin.getUniqueId())) return;
        if (registry.get(admin.getUniqueId()).screen != GUIRegistry.Screen.SERVER_CONTROL) return;

        e.setCancelled(true);
        if (e.getClickedInventory() == null) return;
        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) return;
        ItemStack clicked = e.getCurrentItem();
        if (isPane(clicked) || clicked == null) return;
        int slot = e.getSlot();
        if (slot == 45) { plugin.getMainMenu().open(admin); return; }

        switch (slot) {
            case 9  -> broadcast(admin, "§e§l[Announcement] §fAdmin has a message for the server.");
            case 10 -> {
                Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(
                    "§6§l⚡ SERVER EVENT", "§eCheck the chat for details!", 10, 80, 10));
                admin.sendMessage("§a✔ Title sent to all players");
            }
            case 11 -> {
                broadcast(admin, "§6§l[EVENT] §eA server event is starting! Get ready!");
                Bukkit.getOnlinePlayers().forEach(p -> {
                    org.bukkit.Location l = p.getLocation();
                    p.getWorld().spawnEntity(l, org.bukkit.entity.EntityType.FIREWORK_ROCKET);
                });
            }
            case 12 -> {
                Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f));
                broadcast(admin, "§c§l⚠ WARNING: §cAdmin notice. Please pay attention to the server.");
            }
            case 14 -> {
                ItemStack reward = new ItemStack(Material.DIAMOND, 32);
                Bukkit.getOnlinePlayers().forEach(p -> p.getInventory().addItem(reward.clone()));
                broadcast(admin, "§b§l[REWARD] §7All players received §b32 Diamonds§7!");
            }
            case 15 -> {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.setHealth(p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
                    p.setFoodLevel(20); p.setSaturation(20);
                });
                admin.sendMessage("§a✔ Healed all players");
            }
            case 16 -> {
                Bukkit.getOnlinePlayers().forEach(p -> { p.setFoodLevel(20); p.setSaturation(20); });
                admin.sendMessage("§6✔ Fed all players");
            }
            case 17 -> {
                Bukkit.getOnlinePlayers().forEach(p -> p.giveExpLevels(50));
                broadcast(admin, "§a§l[XP BOMB] §7Everyone received §a50 XP Levels§7!");
            }
            case 18 -> {
                org.bukkit.Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
                Bukkit.getOnlinePlayers().forEach(p -> p.teleport(spawn));
                admin.sendMessage("§a✔ Teleported everyone to spawn");
            }
            case 19 -> {
                Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.equals(admin))
                    .forEach(p -> p.kickPlayer("§cServer is restarting."));
                admin.sendMessage("§c✔ Kicked all other players");
            }
            case 20 -> {
                admin.sendMessage("§c§lBanned players:");
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).getBanEntries()
                    .forEach(ban -> admin.sendMessage("§7 - §f" + ban.getTarget() + " §8("+ban.getReason()+")"));
            }
            case 21 -> {
                admin.sendMessage("§a§lWhitelisted players:");
                Bukkit.getWhitelistedPlayers().forEach(p -> admin.sendMessage("§7 - §f" + p.getName()));
            }
            case 22 -> {
                Bukkit.setWhitelist(!Bukkit.hasWhitelist());
                admin.sendMessage("§6Whitelist: " + (Bukkit.hasWhitelist()?"§aON":"§cOFF"));
                open(admin);
            }
            case 23 -> {
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).getBanEntries()
                    .forEach(ban -> Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(ban.getTarget()));
                admin.sendMessage("§a✔ All player bans cleared");
            }
            case 25 -> { Bukkit.shutdown(); }
            case 26 -> { plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "restart"); }
            case 27 -> { Bukkit.savePlayers(); Bukkit.getWorlds().forEach(World::save); admin.sendMessage("§a✔ All worlds saved"); }
            case 28 -> {
                admin.getWorld().setSpawnLocation(admin.getLocation());
                admin.sendMessage("§a✔ World spawn set to your location");
            }
            case 29 -> promptConsoleCommand(admin);
            case 31 -> {
                int count = 0;
                for (World w : Bukkit.getWorlds())
                    for (org.bukkit.entity.Entity en : w.getEntities())
                        if (en instanceof org.bukkit.entity.Monster) { en.remove(); count++; }
                admin.sendMessage("§c✔ Removed §e" + count + " §cmobs globally");
            }
            case 32 -> {
                int count = 0;
                for (World w : Bukkit.getWorlds())
                    for (org.bukkit.entity.Entity en : w.getEntities())
                        if (en instanceof org.bukkit.entity.Item) { en.remove(); count++; }
                admin.sendMessage("§2✔ Cleared §e" + count + " §2item drops globally");
            }
            case 33 -> {
                Bukkit.getOnlinePlayers().forEach(p -> p.getEnderChest().clear());
                admin.sendMessage("§5✔ Wiped ender chests for all online players");
            }
            case 34 -> {
                Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.equals(admin))
                    .forEach(p -> p.getInventory().clear());
                admin.sendMessage("§4✔ Wiped inventories for all online players");
            }
            case 35 -> {
                admin.sendMessage("§d§lLoaded Plugins:");
                for (org.bukkit.plugin.Plugin p : Bukkit.getPluginManager().getPlugins())
                    admin.sendMessage((p.isEnabled()?"§a":"§c") + " - " + p.getName() + " §8v" + p.getDescription().getVersion());
            }
            case 36 -> {
                Bukkit.getOnlinePlayers().forEach(p -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "op " + p.getName()));
                admin.sendMessage("§b✔ OP granted to all online players");
            }
            case 37 -> {
                Bukkit.getOnlinePlayers().forEach(p -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "deop " + p.getName()));
                admin.sendMessage("§8✔ OP removed from all online players");
            }
            case 38 -> {
                World w = Bukkit.getWorlds().get(0);
                boolean cur = Boolean.TRUE.equals(w.getGameRuleValue(GameRule.MOB_GRIEFING));
                w.setGameRule(GameRule.MOB_GRIEFING, !cur);
                admin.sendMessage("§ePvP (global): " + (!cur?"§aON":"§cOFF"));
                open(admin);
            }
            case 39 -> {
                Bukkit.getOnlinePlayers().stream().filter(p->!p.equals(admin)).forEach(p->p.setHealth(0));
                admin.sendMessage("§c✔ Killed all other players");
            }
            case 40 -> {
                org.bukkit.Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.teleport(spawn);
                    p.setHealth(p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
                    p.setFoodLevel(20);
                });
                admin.sendMessage("§a✔ Revived and teleported all players");
            }
            case 41 -> {
                Bukkit.getWorlds().forEach(w -> { w.setStorm(true); w.setWeatherDuration(72000); });
                admin.sendMessage("§9✔ Rain set on all worlds");
            }
            case 42 -> {
                Bukkit.getWorlds().forEach(w -> { w.setTime(1000); w.setStorm(false); });
                admin.sendMessage("§e✔ Day set on all worlds");
            }
            case 43 -> {
                Runtime rt = Runtime.getRuntime();
                long usedMem = (rt.totalMemory()-rt.freeMemory())/1048576;
                long maxMem  = rt.maxMemory()/1048576;
                broadcast(admin, "§b§l[Server Stats] §7Players: §f"+Bukkit.getOnlinePlayers().size()
                    +" §7| TPS: §f"+String.format("%.1f",20.0)
                    +" §7| RAM: §f"+usedMem+"MB§7/§f"+maxMem+"MB");
            }
            case 44 -> {
                World w = admin.getWorld();
                w.setGameRule(GameRule.KEEP_INVENTORY, false);
                w.setGameRule(GameRule.DO_MOB_SPAWNING, true);
                w.setGameRule(GameRule.DO_MOB_LOOT, true);
                w.setGameRule(GameRule.DO_FIRE_TICK, true);
                w.setGameRule(GameRule.RANDOM_TICK_SPEED, 3);
                w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                w.setGameRule(GameRule.MOB_GRIEFING, true);
                admin.sendMessage("§a✔ All gamerules reset to defaults");
                open(admin);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!registry.has(p.getUniqueId())) return;
        if (registry.get(p.getUniqueId()).screen == GUIRegistry.Screen.SERVER_CONTROL) e.setCancelled(true);
    }

    private void broadcast(Player admin, String msg) {
        Bukkit.broadcastMessage(msg);
        admin.sendMessage("§a✔ Broadcast sent.");
    }

    private void promptConsoleCommand(Player admin) {
        admin.closeInventory();
        admin.sendMessage("§e§lType the console command to execute (no slash needed):");
        admin.sendMessage("§7Type §ccancel §7to abort.");

        plugin.getServer().getPluginManager().registerEvents(new ConsoleCommandListener(plugin, admin, this), plugin);
        registry.get(admin.getUniqueId()).screen = GUIRegistry.Screen.NONE;
    }

    public static class ConsoleCommandListener implements Listener {
        private final OmniAdminSuite plugin; private final Player admin; private final ServerControlGUI parent; private boolean done=false;
        public ConsoleCommandListener(OmniAdminSuite p, Player a, ServerControlGUI sc) { plugin=p; admin=a; parent=sc; }
        @EventHandler public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent e) {
            if (done||!e.getPlayer().equals(admin)) return;
            e.setCancelled(true); done=true;
            String msg = e.getMessage();
            org.bukkit.event.HandlerList.unregisterAll(this);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!msg.equalsIgnoreCase("cancel")) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), msg);
                    admin.sendMessage("§a✔ Executed: §f/" + msg);
                } else {
                    admin.sendMessage("§7Command cancelled.");
                }
                parent.open(admin);
            });
        }
    }
}
