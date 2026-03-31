package com.omniadmin.gui;

import com.omniadmin.OmniAdminSuite;
import com.omniadmin.gui.GUIRegistry;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class PlayerAdminGUI extends GuiBase implements Listener {

    private final OmniAdminSuite plugin;
    private final GUIRegistry registry;

    public PlayerAdminGUI(OmniAdminSuite plugin) {
        this.plugin = plugin;
        this.registry = plugin.getMainMenu().registry;
    }

    // ─────────────────────────────────────────────
    // ✅ REQUIRED (fixes MainMenuGUI error)
    // ─────────────────────────────────────────────
    public void openPlayerList(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 27, "§e§lPlayers");

        int slot = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (slot >= 27) break;
            gui.setItem(slot++, makeHead(p));
        }

        admin.openInventory(gui);
    }

    // ─────────────────────────────────────────────
    // Player Detail
    // ─────────────────────────────────────────────
    public void openPlayerDetail(Player admin, Player t) {
        Inventory gui = Bukkit.createInventory(null, 54,
                "§e§l" + t.getName() + " §8// §7Player Detail");

        gui.setItem(4, makeDetailHead(t));

        gui.setItem(9, make(Material.CHEST, "§a§lView Inventory", "§7See and edit all items"));
        gui.setItem(10, make(Material.ENDER_CHEST, "§5§lView Ender Chest", "§7See and edit ender chest"));
        gui.setItem(11, make(Material.POTION, "§b§lManage Effects", "§7View/add/remove potion effects"));

        fillRow(gui, 5, Material.GRAY_STAINED_GLASS_PANE);
        gui.setItem(45, backButton());
        fill(gui);

        setScreen(admin, GUIRegistry.Screen.PLAYER_DETAIL);
        registry.get(admin.getUniqueId()).targetId = t.getUniqueId();
        admin.openInventory(gui);
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────
    private void setScreen(Player admin, GUIRegistry.Screen screen) {
        registry.get(admin.getUniqueId()).screen = screen;
    }

    // 🔥 FIX: changed to protected (matches GuiBase)
    @Override
    protected ItemStack make(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    // 🔥 FIX: protected
    @Override
    protected void fillRow(Inventory inv, int row, Material mat) {
        for (int i = row * 9; i < row * 9 + 9; i++) {
            inv.setItem(i, new ItemStack(mat));
        }
    }

    // 🔥 FIX: protected
    @Override
    protected void fill(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }
        }
    }

    // 🔥 FIX: protected
    @Override
    protected ItemStack backButton() {
        return make(Material.ARROW, "§7§l« Back");
    }

    private ItemStack makeHead(Player p) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) head.getItemMeta();
        if (sm != null) {
            sm.setOwningPlayer(p);
            sm.setDisplayName("§e§l" + p.getName());
            head.setItemMeta(sm);
        }
        return head;
    }

    private ItemStack makeDetailHead(Player p) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) head.getItemMeta();
        if (sm != null) {
            sm.setOwningPlayer(p);
            sm.setDisplayName("§e§l" + p.getName());
            head.setItemMeta(sm);
        }
        return head;
    }
}
