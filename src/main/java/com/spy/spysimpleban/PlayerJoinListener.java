package com.spy.spysimpleban;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerJoinListener implements Listener {
    
    private final BanManager banManager;
    private final MuteManager muteManager;
    private final WarnManager warnManager;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
    public PlayerJoinListener(BanManager banManager, MuteManager muteManager, WarnManager warnManager) {
        this.banManager = banManager;
        this.muteManager = muteManager;
        this.warnManager = warnManager;
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        String uuid = event.getPlayer().getUniqueId().toString();
        String ip = event.getAddress().getHostAddress();
        
        // Check for expired bans first
        banManager.checkExpiredBans();
        
        // Update offline data for bans, mutes, and warns BEFORE checking if banned
        banManager.enrichOfflineBanData(playerName, uuid, ip);
        muteManager.enrichOfflineMuteData(playerName, uuid);
        warnManager.enrichOfflineWarnData(playerName, uuid);
        
        // Check if player is banned
        if (banManager.isBanned(playerName, uuid, ip)) {
            BanData ban = banManager.getBan(playerName, uuid, ip);
            
            if (ban != null) {
                String kickMessage = ChatColor.RED + "You are banned from this server!\n\n";
                kickMessage += ChatColor.YELLOW + "Reason: " + ban.getReason() + "\n";
                kickMessage += ChatColor.YELLOW + "Banned by: " + ban.getBannerName() + "\n";
                kickMessage += ChatColor.YELLOW + "Ban date: " + dateFormat.format(new Date(ban.getBanTime())) + "\n";
                
                if (ban.isPermanent()) {
                    kickMessage += ChatColor.RED + "This ban is permanent.";
                } else {
                    long remainingTime = ban.getExpireTime() - System.currentTimeMillis();
                    kickMessage += ChatColor.YELLOW + "Ban expires in: " + formatTimeRemaining(remainingTime);
                }
                
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessage);
                return;
            }
        }
    }
    
    private String formatTimeRemaining(long millis) {
        if (millis <= 0) {
            return "0 seconds";
        }
        
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        
        StringBuilder result = new StringBuilder();
        
        if (days > 0) {
            result.append(days).append(days == 1 ? " day" : " days");
        }
        if (hours > 0) {
            if (result.length() > 0) result.append(" ");
            result.append(hours).append(hours == 1 ? " hour" : " hours");
        }
        if (minutes > 0 && days == 0) { // Only show minutes if no days
            if (result.length() > 0) result.append(" ");
            result.append(minutes).append(minutes == 1 ? " minute" : " minutes");
        }
        if (seconds > 0 && days == 0 && hours == 0) { // Only show seconds if no days or hours
            if (result.length() > 0) result.append(" ");
            result.append(seconds).append(seconds == 1 ? " second" : " seconds");
        }
        
        return result.length() > 0 ? result.toString() : "less than 1 second";
    }
}