package com.omniadmin.managers;

import com.omniadmin.OmniAdminSuite;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GodModeManager implements Listener {

    private final OmniAdminSuite plugin;
    private final Set<UUID> godPlayers = new HashSet<>();

    public GodModeManager(OmniAdminSuite plugin) {
        this.plugin = plugin;
    }

    public void toggle(Player player) {
        if (godPlayers.contains(player.getUniqueId())) godPlayers.remove(player.getUniqueId());
        else godPlayers.add(player.getUniqueId());
    }

    public boolean isGod(Player player) {
        return godPlayers.contains(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (godPlayers.contains(p.getUniqueId())) e.setCancelled(true);
    }
}
