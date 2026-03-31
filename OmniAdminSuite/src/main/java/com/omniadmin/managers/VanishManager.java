package com.omniadmin.managers;

import com.omniadmin.OmniAdminSuite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager implements Listener {

    private final OmniAdminSuite plugin;
    private final Set<UUID> vanished = new HashSet<>();

    public VanishManager(OmniAdminSuite plugin) {
        this.plugin = plugin;
    }

    public void vanish(Player player) {
        vanished.add(player.getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) other.hidePlayer(plugin, player);
        }
        player.sendMessage("§7[Vanish] §aYou are now invisible.");
    }

    public void unvanish(Player player) {
        vanished.remove(player.getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {
            other.showPlayer(plugin, player);
        }
        player.sendMessage("§7[Vanish] §cYou are now visible.");
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public void restoreAll() {
        for (UUID id : vanished) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) unvanish(p);
        }
        vanished.clear();
    }

    // Re-hide vanished admins from new joiners
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player joiner = e.getPlayer();
        for (UUID id : vanished) {
            Player hidden = Bukkit.getPlayer(id);
            if (hidden != null && !hidden.equals(joiner))
                joiner.hidePlayer(plugin, hidden);
        }
    }
}
