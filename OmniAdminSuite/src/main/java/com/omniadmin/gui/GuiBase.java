package com.omniadmin.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class GuiBase {

    private static final Set<Material> PANES = Set.of(
        Material.WHITE_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE,
        Material.MAGENTA_STAINED_GLASS_PANE, Material.LIGHT_BLUE_STAINED_GLASS_PANE,
        Material.YELLOW_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE,
        Material.PINK_STAINED_GLASS_PANE, Material.GRAY_STAINED_GLASS_PANE,
        Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE,
        Material.PURPLE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE,
        Material.BROWN_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE,
        Material.RED_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE,
        Material.GLASS_PANE
    );

    /** Create a named item with optional lore lines. */
    protected ItemStack make(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.displayName(Component.text(name));
        if (lore.length > 0) {
            List<Component> loreList = new ArrayList<>();
            for (String line : lore) loreList.add(Component.text(line));
            meta.lore(loreList);
        }
        item.setItemMeta(meta);
        return item;
    }

    /** Standard back/close button. */
    protected ItemStack backButton() {
        return make(Material.BARRIER, "§c§lBack", "§7Return to previous menu");
    }

    /** Toggle button that shows ON/OFF state. */
    protected ItemStack toggleButton(Material mat, String name, boolean enabled) {
        String state  = enabled ? "§a§lON"  : "§c§lOFF";
        String action = enabled ? "§7Click to disable" : "§7Click to enable";
        return make(mat, name, "§7Status: " + state, action);
    }

    /** Fill an entire inventory with gray glass panes (empty slots only). */
    protected void fill(Inventory inv) {
        ItemStack pane = make(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) inv.setItem(i, pane);
        }
    }

    /** Fill a specific row (0-indexed) with the given material. */
    protected void fillRow(Inventory inv, int row, Material mat) {
        ItemStack pane = make(mat, " ");
        int start = row * 9;
        for (int i = start; i < start + 9; i++) {
            inv.setItem(i, pane);
        }
    }

    /** Returns true if the item is a stained glass pane (used as UI decoration). */
    protected boolean isPane(ItemStack item) {
        if (item == null) return false;
        return PANES.contains(item.getType());
    }

    /** Returns the plain display name of an item, or its material name as fallback. */
    protected String plainName(ItemStack item) {
        if (item == null) return "Air";
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            // Strip legacy colour codes
            return meta.displayName().toString()
                .replaceAll("§[0-9a-fk-or]", "")
                .replaceAll("<[^>]+>", "");
        }
        return item.getType().name().replace("_", " ");
    }
}
