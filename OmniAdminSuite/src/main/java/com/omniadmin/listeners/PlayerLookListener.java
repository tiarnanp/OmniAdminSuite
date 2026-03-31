package com.omniadmin.listeners;

import com.omniadmin.OmniAdminSuite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerLookListener implements Listener {

    private final OmniAdminSuite plugin;

    public PlayerLookListener(OmniAdminSuite plugin) {
        this.plugin = plugin;
    }

    /**
     * Right-clicking another player while sneaking opens their admin detail panel directly.
     */
    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player admin = e.getPlayer();
        if (!admin.hasPermission("oas.use")) return;
        if (!admin.isSneaking()) return;

        Entity target = e.getRightClicked();
        if (!(target instanceof Player targetPlayer)) return;
        if (targetPlayer.equals(admin)) return;

        e.setCancelled(true);

        // Register session and open detail screen
        plugin.getMainMenu().registry.get(admin.getUniqueId()).targetId = targetPlayer.getUniqueId();
        plugin.getPlayerAdmin().openPlayerDetail(admin, targetPlayer);
    }
}
