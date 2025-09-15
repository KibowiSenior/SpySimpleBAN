package com.spy.spysimpleban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WarnCommand implements CommandExecutor {
    
    private final WarnManager warnManager;
    
    public WarnCommand(WarnManager warnManager) {
        this.warnManager = warnManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spy.warn") && !sender.hasPermission("spy.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /warn <player> <reason>");
            return true;
        }
        
        String targetName = args[0];
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();
        
        Player target = Bukkit.getPlayer(targetName);
        
        if (target != null) {
            // Player is online
            String uuid = target.getUniqueId().toString();
            
            warnManager.warnPlayer(targetName, uuid, reason, sender.getName());
            
            // Get total warn count for this player
            List<WarnData> playerWarns = warnManager.getPlayerWarns(targetName, uuid);
            int warnCount = playerWarns.size();
            
            target.sendMessage(ChatColor.YELLOW + "===============================");
            target.sendMessage(ChatColor.RED + "            WARNING            ");
            target.sendMessage(ChatColor.YELLOW + "===============================");
            target.sendMessage(ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + reason);
            target.sendMessage(ChatColor.YELLOW + "Warned by: " + ChatColor.WHITE + sender.getName());
            target.sendMessage(ChatColor.YELLOW + "Total warnings: " + ChatColor.RED + warnCount);
            target.sendMessage(ChatColor.YELLOW + "===============================");
            
            sender.sendMessage(ChatColor.GREEN + "Successfully warned " + targetName + " (Warning #" + warnCount + ").");
            
            // Notify staff
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("spy.admin") || staff.hasPermission("spy.warn")) {
                    if (!staff.equals(sender)) {
                        staff.sendMessage(ChatColor.GRAY + "[STAFF] " + sender.getName() + " warned " + targetName + " for: " + reason);
                    }
                }
            }
        } else {
            // Player is offline
            warnManager.warnPlayer(targetName, "", reason, sender.getName());
            List<WarnData> playerWarns = warnManager.getPlayerWarns(targetName);
            int warnCount = playerWarns.size();
            sender.sendMessage(ChatColor.GREEN + "Successfully warned " + targetName + " (offline warn - Warning #" + warnCount + ").");
        }
        
        return true;
    }
}