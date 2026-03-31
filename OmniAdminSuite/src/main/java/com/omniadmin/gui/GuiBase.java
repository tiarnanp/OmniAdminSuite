package com.omniadmin.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiBase {

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

    protected ItemStack backButton() {
        return make(Material.BARRIER, "§c§lBack", "§7Return to previous menu");
    }

    protected ItemStack toggleButton(Material mat, String name, boolean enabled) {
        String state  = enabled ? "§a§lON"  : "§c§lOFF";
        String action = enabled ? "§7Click to disable" : "§7Click to enable";
        return make(mat, name, "§7Status: " + state, action);
    }
}
