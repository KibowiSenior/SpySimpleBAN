package com.spy.spysimpleban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand implements CommandExecutor {
    
    private final BanManager banManager;
    
    public BanCommand(BanManager banManager) {
        this.banManager = banManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spy.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /ban <player> <reason>");
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
            String ip = target.getAddress().getAddress().getHostAddress();
            
            banManager.banPlayer(targetName, uuid, ip, reason, 0, sender.getName()); // 0 = permanent
            target.kickPlayer(ChatColor.RED + "You have been permanently banned!\n\n" +
                            ChatColor.YELLOW + "Reason: " + reason + "\n" +
                            ChatColor.GRAY + "Banned by: " + sender.getName());
            
            sender.sendMessage(ChatColor.GREEN + "Successfully banned " + targetName + " (Name, IP, and UUID) permanently.");
            Bukkit.broadcastMessage(ChatColor.YELLOW + targetName + " has been banned by " + sender.getName() + " for: " + reason);
        } else {
            // Player is offline - ban by name only (UUID and IP will be updated when they try to join)
            banManager.banPlayer(targetName, "", "", reason, 0, sender.getName());
            sender.sendMessage(ChatColor.GREEN + "Successfully banned " + targetName + " (offline ban - will update IP and UUID on next join attempt).");
        }
        
        return true;
    }
}