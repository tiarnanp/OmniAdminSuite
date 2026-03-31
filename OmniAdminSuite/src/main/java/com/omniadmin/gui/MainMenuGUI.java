package com.omniadmin.gui;

import com.omniadmin.OmniAdminSuite;
import com.omniadmin.gui.GUIRegistry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

public class MainMenuGUI extends GuiBase implements Listener {

    private final OmniAdminSuite plugin;
    public final GUIRegistry registry = new GUIRegistry();

    public MainMenuGUI(OmniAdminSuite plugin) {
        this.plugin = plugin;
    }

    public void open(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 27,
                "§0§lOmniAdmin §8// §6Command Center");

        // Row 0: decorative header
        fillRow(gui, 0, Material.GRAY_STAINED_GLASS_PANE);

        // Main category buttons
        gui.setItem(10, make(Material.PLAYER_HEAD,   "§e§lPlayer Control",   "§7Manage any online player.", "§7Inventory, effects, powers,","§7teleport, strip, and more.","","§eClick to open"));
        gui.setItem(11, make(Material.GRASS_BLOCK,   "§a§lWorld Control",    "§7Edit blocks, weather, time,","§7gamerules, biomes & more.","","§eClick to open"));
        gui.setItem(13, make(Material.NETHER_STAR,   "§b§lSelf Powers",      "§7God mode, fly, vanish,","§7infinite items, XP, speed.","","§eClick to open"));
        gui.setItem(15, make(Material.COMMAND_BLOCK, "§c§lServer Control",   "§7Broadcast, whitelist, bans,","§7stop/restart, simulation speed.","","§eClick to open"));
        gui.setItem(16, make(Material.BOOK,          "§d§lSnapshots",        "§7Save & restore any player's","§7full inventory state.","","§eClick to open"));

        // Bottom row
        fillRow(gui, 2, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(22, make(Material.BARRIER, "§7Close", "§7Close this menu"));

        fill(gui);

        GUIRegistry.Session s = registry.get(admin.getUniqueId());
        s.screen = GUIRegistry.Screen.MAIN_MENU;
        admin.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player admin)) return;
        if (!registry.has(admin.getUniqueId())) return;

        GUIRegistry.Session session = registry.get(admin.getUniqueId());
        if (session.screen != GUIRegistry.Screen.MAIN_MENU) return;
        if (e.getClickedInventory() == null) return;
        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) { e.setCancelled(true); return; }

        e.setCancelled(true);
        if (isPane(e.getCurrentItem()) || e.getCurrentItem() == null) return;

        switch (e.getSlot()) {
            case 10 -> plugin.getPlayerAdmin().openPlayerList(admin);
            case 11 -> plugin.getWorldEdit().open(admin);
            case 13 -> plugin.getSelfPowers().open(admin);
            case 15 -> plugin.getServerControl().open(admin);
            case 16 -> openSnapshotMenu(admin);
            case 22 -> { registry.clear(admin.getUniqueId()); admin.closeInventory(); }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player admin)) return;
        if (registry.has(admin.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player admin)) return;
        if (!registry.has(admin.getUniqueId())) return;
        GUIRegistry.Session s = registry.get(admin.getUniqueId());
        if (s.screen == GUIRegistry.Screen.MAIN_MENU) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (registry.has(admin.getUniqueId()) &&
                    registry.get(admin.getUniqueId()).screen == GUIRegistry.Screen.MAIN_MENU)
                    registry.clear(admin.getUniqueId());
            }, 3L);
        }
    }

    private void openSnapshotMenu(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 36,
                "§d§lSnapshots");

        int slot = 0;
        for (Player target : plugin.getServer().getOnlinePlayers()) {
            if (slot >= 27) break;
            org.bukkit.inventory.ItemStack head = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
            org.bukkit.inventory.meta.SkullMeta sm = (org.bukkit.inventory.meta.SkullMeta) head.getItemMeta();
            sm.setOwningPlayer(target);
            sm.setDisplayName("§e" + target.getName());
            sm.setLore(java.util.List.of(
                "§aLeft-click §7to save snapshot",
                "§cRight-click §7to load snapshot"
            ));
            head.setItemMeta(sm);
            gui.setItem(slot++, head);
        }

        fillRow(gui, 3, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(31, backButton());
        fill(gui);

        GUIRegistry.Session s = registry.get(admin.getUniqueId());
        s.screen = GUIRegistry.Screen.NONE; // use NONE as a generic "sub-screen" flag
        admin.openInventory(gui);

        // Override click handler inline via scheduler trick — handled in PlayerAdminGUI via its own listener
        // We register a one-shot inventory listener here
        plugin.getServer().getPluginManager().registerEvents(new SnapshotClickHandler(plugin, admin, this), plugin);
    }

    // ── Inner class for snapshot menu clicks ────────────────────────────────
    public static class SnapshotClickHandler implements Listener {
        private final OmniAdminSuite plugin;
        private final Player admin;
        private final MainMenuGUI parent;
        private boolean done = false;

        public SnapshotClickHandler(OmniAdminSuite plugin, Player admin, MainMenuGUI parent) {
            this.plugin = plugin; this.admin = admin; this.parent = parent;
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (done) return;
            if (!e.getWhoClicked().equals(admin)) return;
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(e.getView().getTopInventory())) {
                e.setCancelled(true); return;
            }
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

            org.bukkit.inventory.meta.SkullMeta sm = (org.bukkit.inventory.meta.SkullMeta) e.getCurrentItem().getItemMeta();
            if (sm == null || sm.getOwnerProfile() == null) return;
            Player target = plugin.getServer().getPlayerExact(sm.getOwnerProfile().getName());
            if (target == null) { admin.sendMessage("§cPlayer offline."); return; }

            if (e.getClick().isLeftClick()) {
                plugin.getSnapshotManager().save(target);
                admin.sendMessage("§aSnapshot saved for §e" + target.getName());
            } else if (e.getClick().isRightClick()) {
                boolean ok = plugin.getSnapshotManager().load(target);
                admin.sendMessage(ok ? "§aSnapshot loaded for §e" + target.getName() : "§cNo snapshot found.");
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            if (!e.getPlayer().equals(admin) || done) return;
            done = true;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> parent.open(admin), 1L);
            org.bukkit.event.HandlerList.unregisterAll(this);
        }
    }
}
