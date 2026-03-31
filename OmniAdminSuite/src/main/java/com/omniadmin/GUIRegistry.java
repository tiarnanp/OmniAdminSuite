package com.omniadmin.gui;

import org.bukkit.inventory.ItemStack;
import java.util.*;

/**
 * Central state registry so all GUI classes share session data cleanly.
 */
public class GUIRegistry {

    public enum Screen {
        NONE, MAIN_MENU,
        PLAYER_LIST, PLAYER_DETAIL, PLAYER_INVENTORY, PLAYER_ENDERCHEST,
        PLAYER_EFFECTS, ITEM_ACTION,
        WORLD_EDIT, BLOCK_REPLACE,
        SERVER_CONTROL,
        SELF_POWERS
    }

    public static class Session {
        public Screen screen      = Screen.NONE;
        public UUID   targetId    = null;   // player being managed
        public int    actionSlot  = -1;     // slot clicked for item action
        public String actionSrc   = null;   // "inventory" or "enderchest"
        public ItemStack pendingItem = null;
        // for block-replace: original material
        public String blockMaterial = null;
    }

    private final Map<UUID, Session> sessions = new HashMap<>();

    public Session get(UUID adminId) {
        return sessions.computeIfAbsent(adminId, k -> new Session());
    }

    public void clear(UUID adminId) {
        sessions.remove(adminId);
    }

    public boolean has(UUID adminId) {
        return sessions.containsKey(adminId);
    }
}
