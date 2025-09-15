package com.spy.spysimpleban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnmuteCommand implements CommandExecutor {
    
    private final MuteManager muteManager;
    
    public UnmuteCommand(MuteManager muteManager) {
        this.muteManager = muteManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spy.mute") && !sender.hasPermission("spy.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unmute <player>");
            return true;
        }
        
        String targetName = args[0];
        
        boolean success = muteManager.unmutePlayer(targetName);
        
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Successfully unmuted " + targetName + ".");
            Bukkit.broadcastMessage(ChatColor.GREEN + targetName + " has been unmuted by " + sender.getName() + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Player " + targetName + " is not muted.");
        }
        
        return true;
    }
}