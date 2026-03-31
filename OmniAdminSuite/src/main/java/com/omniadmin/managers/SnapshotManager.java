package com.omniadmin.managers;

import com.omniadmin.OmniAdminSuite;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class SnapshotManager {

    private final OmniAdminSuite plugin;
    private final File dataFolder;

    public SnapshotManager(OmniAdminSuite plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "snapshots");
        if (!dataFolder.exists()) dataFolder.mkdirs();
    }

    public void save(Player player) {
        File file = new File(dataFolder, player.getUniqueId() + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();

        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++)
            if (contents[i] != null) cfg.set("inv." + i, contents[i]);

        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++)
            if (armor[i] != null) cfg.set("armor." + i, armor[i]);

        cfg.set("offhand", player.getInventory().getItemInOffHand());
        cfg.set("name", player.getName());
        cfg.set("ts", System.currentTimeMillis());

        try { cfg.save(file); } catch (IOException ex) {
            plugin.getLogger().warning("Failed to save snapshot: " + ex.getMessage());
        }
    }

    public boolean load(Player player) {
        File file = new File(dataFolder, player.getUniqueId() + ".yml");
        if (!file.exists()) return false;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        player.getInventory().clear();

        if (cfg.contains("inv"))
            for (String key : cfg.getConfigurationSection("inv").getKeys(false)) {
                ItemStack it = cfg.getItemStack("inv." + key);
                if (it != null) player.getInventory().setItem(Integer.parseInt(key), it);
            }

        if (cfg.contains("armor")) {
            ItemStack[] armor = new ItemStack[4];
            for (String key : cfg.getConfigurationSection("armor").getKeys(false))
                armor[Integer.parseInt(key)] = cfg.getItemStack("armor." + key);
            player.getInventory().setArmorContents(armor);
        }

        ItemStack offhand = cfg.getItemStack("offhand");
        if (offhand != null) player.getInventory().setItemInOffHand(offhand);
        return true;
    }
}
