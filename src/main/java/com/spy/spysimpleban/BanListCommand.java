package com.spy.spysimpleban;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BanListCommand implements CommandExecutor {
    
    private final BanManager banManager;
    
    public BanListCommand(BanManager banManager) {
        this.banManager = banManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spy.banlist") && !sender.hasPermission("spy.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        List<BanData> allBans = banManager.getBans();
        List<BanData> activeBans = allBans.stream()
            .filter(ban -> !ban.isExpired())
            .collect(java.util.stream.Collectors.toList());
        
        if (activeBans.isEmpty()) {
            sender.sendMessage(ChatColor.GREEN + "No players are currently banned.");
            return true;
        }
        
        sender.sendMessage(ChatColor.YELLOW + "================= BAN LIST =================");
        sender.sendMessage(ChatColor.YELLOW + "Total active bans: " + ChatColor.RED + activeBans.size());
        sender.sendMessage(ChatColor.YELLOW + "===========================================");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        
        for (int i = 0; i < activeBans.size(); i++) {
            BanData ban = activeBans.get(i);
            sender.sendMessage(ChatColor.GRAY + "--- Ban #" + (i + 1) + " ---");
            sender.sendMessage(ChatColor.YELLOW + "Player: " + ChatColor.WHITE + ban.getPlayerName());
            sender.sendMessage(ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + ban.getReason());
            sender.sendMessage(ChatColor.YELLOW + "Banned by: " + ChatColor.WHITE + ban.getBannerName());
            sender.sendMessage(ChatColor.YELLOW + "Date: " + ChatColor.WHITE + dateFormat.format(new Date(ban.getBanTime())));
            
            if (ban.getExpireTime() == 0) {
                sender.sendMessage(ChatColor.YELLOW + "Type: " + ChatColor.RED + "Permanent");
            } else {
                long remainingTime = ban.getExpireTime() - System.currentTimeMillis();
                String timeLeft = formatDuration(remainingTime);
                sender.sendMessage(ChatColor.YELLOW + "Type: " + ChatColor.GOLD + "Temporary");
                sender.sendMessage(ChatColor.YELLOW + "Expires: " + ChatColor.WHITE + dateFormat.format(new Date(ban.getExpireTime())));
                sender.sendMessage(ChatColor.YELLOW + "Time left: " + ChatColor.WHITE + timeLeft);
            }
            
            if (i < activeBans.size() - 1) {
                sender.sendMessage("");
            }
        }
        
        sender.sendMessage(ChatColor.YELLOW + "===========================================");
        return true;
    }
    
    private String formatDuration(long millis) {
        if (millis <= 0) {
            return "Expired";
        }
        
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "d " + (hours % 24) + "h " + (minutes % 60) + "m";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }
}