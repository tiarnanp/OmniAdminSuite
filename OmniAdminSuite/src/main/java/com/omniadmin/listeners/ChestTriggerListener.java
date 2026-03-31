package com.omniadmin.listeners;

import com.omniadmin.OmniAdminSuite;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestTriggerListener implements Listener {

    private final OmniAdminSuite plugin;
    private static final String TRIGGER = "OmniAdmin";

    public ChestTriggerListener(OmniAdminSuite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block b = e.getClickedBlock();
        if (b == null || b.getType() != Material.CHEST) return;
        Player p = e.getPlayer();
        if (!p.hasPermission("oas.use")) return;
        Chest chest = (Chest) b.getState();
        if (chest.getCustomName() == null) return;
        String name = chest.getCustomName();
        if (!name.equalsIgnoreCase(TRIGGER)) return;
        e.setCancelled(true);
        plugin.getMainMenu().open(p);
    }
}
