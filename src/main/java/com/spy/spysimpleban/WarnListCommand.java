package com.spy.spysimpleban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WarnListCommand implements CommandExecutor {
    
    private final WarnManager warnManager;
    
    public WarnListCommand(WarnManager warnManager) {
        this.warnManager = warnManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spy.warnlist") && !sender.hasPermission("spy.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /warnlist <player>");
            return true;
        }
        
        String targetName = args[0];
        
        // Try to get UUID if player is online for more accurate results
        Player target = Bukkit.getPlayer(targetName);
        List<WarnData> playerWarns;
        
        if (target != null) {
            String uuid = target.getUniqueId().toString();
            playerWarns = warnManager.getPlayerWarns(targetName, uuid);
        } else {
            playerWarns = warnManager.getPlayerWarns(targetName);
        }
        
        if (playerWarns.isEmpty()) {
            sender.sendMessage(ChatColor.GREEN + "Player " + targetName + " has no warnings.");
            return true;
        }
        
        sender.sendMessage(ChatColor.YELLOW + "============= WARNINGS FOR " + targetName.toUpperCase() + " =============");
        sender.sendMessage(ChatColor.YELLOW + "Total warnings: " + ChatColor.RED + playerWarns.size());
        sender.sendMessage(ChatColor.YELLOW + "===============================================");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        
        for (int i = 0; i < playerWarns.size(); i++) {
            WarnData warn = playerWarns.get(i);
            sender.sendMessage(ChatColor.GRAY + "--- Warning #" + (i + 1) + " ---");
            sender.sendMessage(ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + warn.getReason());
            sender.sendMessage(ChatColor.YELLOW + "Warned by: " + ChatColor.WHITE + warn.getWarnerName());
            sender.sendMessage(ChatColor.YELLOW + "Date: " + ChatColor.WHITE + dateFormat.format(new Date(warn.getWarnTime())));
            
            if (i < playerWarns.size() - 1) {
                sender.sendMessage("");
            }
        }
        
        sender.sendMessage(ChatColor.YELLOW + "===============================================");
        return true;
    }
}