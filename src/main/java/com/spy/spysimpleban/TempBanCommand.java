package com.spy.spysimpleban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TempBanCommand implements CommandExecutor {
    
    private final BanManager banManager;
    private final Pattern timePattern = Pattern.compile("(\\d+)([smhd])");
    
    public TempBanCommand(BanManager banManager) {
        this.banManager = banManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spy.tempban") && !sender.hasPermission("spy.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /tempban <player> <time> <reason>");
            sender.sendMessage(ChatColor.YELLOW + "Time format: 1s, 5m, 1h, 2d (seconds, minutes, hours, days)");
            return true;
        }
        
        String targetName = args[0];
        String timeString = args[1];
        
        long duration = parseTime(timeString);
        if (duration <= 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time format! Use: 1s, 5m, 1h, 2d");
            return true;
        }
        
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();
        
        long expireTime = System.currentTimeMillis() + duration;
        
        Player target = Bukkit.getPlayer(targetName);
        
        if (target != null) {
            // Player is online
            String uuid = target.getUniqueId().toString();
            String ip = target.getAddress().getAddress().getHostAddress();
            
            banManager.banPlayer(targetName, uuid, ip, reason, expireTime, sender.getName());
            target.kickPlayer(ChatColor.RED + "You have been temporarily banned!\n\n" +
                            ChatColor.YELLOW + "Reason: " + reason + "\n" +
                            ChatColor.YELLOW + "Duration: " + timeString + "\n" +
                            ChatColor.GRAY + "Banned by: " + sender.getName());
            
            sender.sendMessage(ChatColor.GREEN + "Successfully temp-banned " + targetName + " (Name, IP, and UUID) for " + timeString + ".");
            Bukkit.broadcastMessage(ChatColor.YELLOW + targetName + " has been temp-banned by " + sender.getName() + " for " + timeString + ": " + reason);
        } else {
            // Player is offline
            banManager.banPlayer(targetName, "", "", reason, expireTime, sender.getName());
            sender.sendMessage(ChatColor.GREEN + "Successfully temp-banned " + targetName + " (offline ban - will update IP and UUID on next join attempt) for " + timeString + ".");
        }
        
        return true;
    }
    
    private long parseTime(String timeString) {
        Matcher matcher = timePattern.matcher(timeString.toLowerCase());
        long totalMillis = 0;
        
        while (matcher.find()) {
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);
            
            switch (unit) {
                case "s":
                    totalMillis += TimeUnit.SECONDS.toMillis(amount);
                    break;
                case "m":
                    totalMillis += TimeUnit.MINUTES.toMillis(amount);
                    break;
                case "h":
                    totalMillis += TimeUnit.HOURS.toMillis(amount);
                    break;
                case "d":
                    totalMillis += TimeUnit.DAYS.toMillis(amount);
                    break;
            }
        }
        
        return totalMillis;
    }
}