package com.spy.spysimpleban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnbanCommand implements CommandExecutor {
    
    private final BanManager banManager;
    
    public UnbanCommand(BanManager banManager) {
        this.banManager = banManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spy.unban") && !sender.hasPermission("spy.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unban <player>");
            return true;
        }
        
        String targetName = args[0];
        
        boolean success = banManager.unbanPlayer(targetName);
        
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Successfully unbanned " + targetName + " (removed all ban data: name, IP, and UUID).");
            Bukkit.broadcastMessage(ChatColor.GREEN + targetName + " has been unbanned by " + sender.getName() + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Player " + targetName + " is not banned or ban data not found.");
        }
        
        return true;
    }
}