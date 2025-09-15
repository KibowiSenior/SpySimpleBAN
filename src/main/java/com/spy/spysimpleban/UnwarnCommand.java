package com.spy.spysimpleban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnwarnCommand implements CommandExecutor {
    
    private final WarnManager warnManager;
    
    public UnwarnCommand(WarnManager warnManager) {
        this.warnManager = warnManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spy.unwarn") && !sender.hasPermission("spy.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unwarn <player>");
            return true;
        }
        
        String targetName = args[0];
        
        // Try to get UUID if player is online
        Player target = Bukkit.getPlayer(targetName);
        
        // Check how many warnings the player currently has (use UUID if available for accuracy)
        List<WarnData> playerWarns;
        if (target != null) {
            String uuid = target.getUniqueId().toString();
            playerWarns = warnManager.getPlayerWarns(targetName, uuid);
        } else {
            playerWarns = warnManager.getPlayerWarns(targetName);
        }
        
        if (playerWarns.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Player " + targetName + " has no warnings to remove.");
            return true;
        }
        
        int warnCount = playerWarns.size();
        boolean success;
        
        if (target != null) {
            // Player is online
            String uuid = target.getUniqueId().toString();
            success = warnManager.clearPlayerWarns(targetName, uuid);
            
            if (success) {
                target.sendMessage(ChatColor.GREEN + "All your warnings have been cleared by " + sender.getName() + ".");
            }
        } else {
            // Player is offline
            success = warnManager.clearPlayerWarns(targetName);
        }
        
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + warnCount + " warning(s) from " + targetName + ".");
            
            // Notify staff only (no public broadcast to prevent spam)
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("spy.admin") || staff.hasPermission("spy.unwarn")) {
                    if (!staff.equals(sender)) {
                        staff.sendMessage(ChatColor.GRAY + "[STAFF] " + sender.getName() + " removed " + warnCount + " warning(s) from " + targetName);
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to remove warnings from " + targetName + ". Please try again.");
        }
        
        return true;
    }
}