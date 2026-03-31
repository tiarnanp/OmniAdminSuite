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

    public PlayerAdminGUI(OmniAdminSuite plugin) {
        this.plugin = plugin;
        this.registry = plugin.getMainMenu().registry;
    }

    // ─────────────────────────────────────────────
    // ✅ YOUR METHOD (now correctly inside class)
    // ─────────────────────────────────────────────
    public void openPlayerDetail(Player admin, Player t) {
        Inventory gui = Bukkit.createInventory(null, 54,
                "§e§l" + t.getName() + " §8// §7Player Detail");

        gui.setItem(4, makeDetailHead(t));

        gui.setItem(9,  make(Material.CHEST, "§a§lView Inventory", "§7See and edit all items"));
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
    // ✅ REQUIRED HELPERS (stubs so it compiles)
    // ─────────────────────────────────────────────
    private void setScreen(Player admin, GUIRegistry.Screen screen) {
        registry.get(admin.getUniqueId()).screen = screen;
    }

    private ItemStack make(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
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

    private void fillRow(Inventory inv, int row, Material mat) {
        for (int i = row * 9; i < row * 9 + 9; i++) {
            inv.setItem(i, new ItemStack(mat));
        }
    }

    private void fill(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }
        }
    }

    private ItemStack backButton() {
        return make(Material.ARROW, "§7§l« Back");
    }
}
