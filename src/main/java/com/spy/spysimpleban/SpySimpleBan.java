package com.spy.spysimpleban;

import org.bukkit.plugin.java.JavaPlugin;

public class SpySimpleBan extends JavaPlugin {
    
    private BanManager banManager;
    private MuteManager muteManager;
    private WarnManager warnManager;
    
    @Override
    public void onEnable() {
        getLogger().info("SpySimpleBan plugin enabled!");
        
        // Initialize managers
        banManager = new BanManager(this);
        muteManager = new MuteManager(this);
        warnManager = new WarnManager(this);
        
        // Register commands
        getCommand("ban").setExecutor(new BanCommand(banManager));
        getCommand("tempban").setExecutor(new TempBanCommand(banManager));
        getCommand("unban").setExecutor(new UnbanCommand(banManager));
        getCommand("banlist").setExecutor(new BanListCommand(banManager));
        getCommand("mute").setExecutor(new MuteCommand(muteManager));
        getCommand("unmute").setExecutor(new UnmuteCommand(muteManager));
        getCommand("mutelist").setExecutor(new MuteListCommand(muteManager));
        getCommand("warn").setExecutor(new WarnCommand(warnManager));
        getCommand("unwarn").setExecutor(new UnwarnCommand(warnManager));
        getCommand("warnlist").setExecutor(new WarnListCommand(warnManager));
        
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(banManager, muteManager, warnManager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(muteManager), this);
        
        // Start auto-unban scheduler
        getServer().getScheduler().runTaskTimer(this, () -> {
            banManager.checkExpiredBans();
        }, 20L, 20L); // Run every second
    }
    
    @Override
    public void onDisable() {
        getLogger().info("SpySimpleBan plugin disabled!");
    }
    
    public BanManager getBanManager() {
        return banManager;
    }
    
    public MuteManager getMuteManager() {
        return muteManager;
    }
    
    public WarnManager getWarnManager() {
        return warnManager;
    }
}