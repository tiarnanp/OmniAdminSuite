package com.omniadmin;

import com.omniadmin.gui.*;
import com.omniadmin.listeners.*;
import com.omniadmin.managers.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class OmniAdminSuite extends JavaPlugin {

    // GUI Panels
    private MainMenuGUI mainMenu;
    private PlayerAdminGUI playerAdmin;
    private WorldEditGUI worldEdit;
    private ServerControlGUI serverControl;
    private SelfPowersGUI selfPowers;

    // Managers
    private SnapshotManager snapshotManager;
    private VanishManager vanishManager;
    private GodModeManager godModeManager;

    @Override
    public void onEnable() {
        // Managers
        snapshotManager  = new SnapshotManager(this);
        vanishManager    = new VanishManager(this);
        godModeManager   = new GodModeManager(this);

        // GUIs
        playerAdmin  = new PlayerAdminGUI(this);
        worldEdit    = new WorldEditGUI(this);
        serverControl= new ServerControlGUI(this);
        selfPowers   = new SelfPowersGUI(this);
        mainMenu     = new MainMenuGUI(this);

        // Listeners
        getServer().getPluginManager().registerEvents(mainMenu,      this);
        getServer().getPluginManager().registerEvents(playerAdmin,   this);
        getServer().getPluginManager().registerEvents(worldEdit,     this);
        getServer().getPluginManager().registerEvents(serverControl, this);
        getServer().getPluginManager().registerEvents(selfPowers,    this);
        getServer().getPluginManager().registerEvents(new ChestTriggerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLookListener(this),   this);
        getServer().getPluginManager().registerEvents(godModeManager, this);
        getServer().getPluginManager().registerEvents(vanishManager,  this);

        getLogger().info("OmniAdminSuite enabled. /oas to open.");
    }

    @Override
    public void onDisable() {
        vanishManager.restoreAll();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("§cPlayers only."); return true; }
        if (!p.hasPermission("oas.use"))   { p.sendMessage("§cNo permission."); return true; }
        if (cmd.getName().equalsIgnoreCase("oas"))       mainMenu.open(p);
        if (cmd.getName().equalsIgnoreCase("oasreload")) p.sendMessage("§aOmniAdmin reloaded.");
        return true;
    }

    // Getters
    public MainMenuGUI      getMainMenu()      { return mainMenu; }
    public PlayerAdminGUI   getPlayerAdmin()   { return playerAdmin; }
    public WorldEditGUI     getWorldEdit()     { return worldEdit; }
    public ServerControlGUI getServerControl() { return serverControl; }
    public SelfPowersGUI    getSelfPowers()    { return selfPowers; }
    public SnapshotManager  getSnapshotManager(){ return snapshotManager; }
    public VanishManager    getVanishManager() { return vanishManager; }
    public GodModeManager   getGodModeManager(){ return godModeManager; }
}
