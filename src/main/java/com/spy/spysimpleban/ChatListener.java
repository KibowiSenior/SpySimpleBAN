package com.spy.spysimpleban;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    
    private final MuteManager muteManager;
    
    public ChatListener(MuteManager muteManager) {
        this.muteManager = muteManager;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (muteManager.isMuted(event.getPlayer())) {
            MuteData mute = muteManager.getMute(event.getPlayer().getName(), event.getPlayer().getUniqueId().toString());
            
            if (mute != null) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You are muted and cannot speak!");
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Reason: " + mute.getReason());
                event.getPlayer().sendMessage(ChatColor.GRAY + "Muted by: " + mute.getMuterName());
            }
        }
    }
}