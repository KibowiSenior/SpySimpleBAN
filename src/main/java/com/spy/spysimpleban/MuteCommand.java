package com.spy.spysimpleban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {
    
    private final MuteManager muteManager;
    
    public MuteCommand(MuteManager muteManager) {
        this.muteManager = muteManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spy.mute") && !sender.hasPermission("spy.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /mute <player> <reason>");
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
            
            muteManager.mutePlayer(targetName, uuid, reason, sender.getName());
            target.sendMessage(ChatColor.RED + "You have been muted!\n" +
                            ChatColor.YELLOW + "Reason: " + reason + "\n" +
                            ChatColor.GRAY + "Muted by: " + sender.getName());
            
            sender.sendMessage(ChatColor.GREEN + "Successfully muted " + targetName + ".");
            Bukkit.broadcastMessage(ChatColor.YELLOW + targetName + " has been muted by " + sender.getName() + " for: " + reason);
        } else {
            // Player is offline
            muteManager.mutePlayer(targetName, "", reason, sender.getName());
            sender.sendMessage(ChatColor.GREEN + "Successfully muted " + targetName + " (offline mute - will update UUID on next join).");
        }
        
        return true;
    }
}