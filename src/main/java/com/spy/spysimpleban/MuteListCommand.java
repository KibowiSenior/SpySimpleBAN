package com.spy.spysimpleban;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MuteListCommand implements CommandExecutor {
    
    private final MuteManager muteManager;
    
    public MuteListCommand(MuteManager muteManager) {
        this.muteManager = muteManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spy.mutelist") && !sender.hasPermission("spy.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        List<MuteData> allMutes = muteManager.getMutes();
        
        if (allMutes.isEmpty()) {
            sender.sendMessage(ChatColor.GREEN + "No players are currently muted.");
            return true;
        }
        
        sender.sendMessage(ChatColor.YELLOW + "================= MUTE LIST =================");
        sender.sendMessage(ChatColor.YELLOW + "Total muted players: " + ChatColor.RED + allMutes.size());
        sender.sendMessage(ChatColor.YELLOW + "===========================================");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        
        for (int i = 0; i < allMutes.size(); i++) {
            MuteData mute = allMutes.get(i);
            sender.sendMessage(ChatColor.GRAY + "--- Mute #" + (i + 1) + " ---");
            sender.sendMessage(ChatColor.YELLOW + "Player: " + ChatColor.WHITE + mute.getPlayerName());
            sender.sendMessage(ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + mute.getReason());
            sender.sendMessage(ChatColor.YELLOW + "Muted by: " + ChatColor.WHITE + mute.getMuterName());
            sender.sendMessage(ChatColor.YELLOW + "Date: " + ChatColor.WHITE + dateFormat.format(new Date(mute.getMuteTime())));
            
            if (i < allMutes.size() - 1) {
                sender.sendMessage("");
            }
        }
        
        sender.sendMessage(ChatColor.YELLOW + "===========================================");
        return true;
    }
}