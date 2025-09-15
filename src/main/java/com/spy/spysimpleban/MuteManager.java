package com.spy.spysimpleban;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MuteManager {
    
    private final JavaPlugin plugin;
    private final File dataFile;
    private final Gson gson;
    private List<MuteData> mutes;
    
    public MuteManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "mutes.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.mutes = new CopyOnWriteArrayList<>();
        
        loadMutes();
    }
    
    private void loadMutes() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            saveMutes();
            return;
        }
        
        try (FileReader reader = new FileReader(dataFile)) {
            Type listType = new TypeToken<List<MuteData>>(){}.getType();
            List<MuteData> loadedMutes = gson.fromJson(reader, listType);
            if (loadedMutes != null) {
                mutes = new CopyOnWriteArrayList<>(loadedMutes);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load mutes: " + e.getMessage());
        }
    }
    
    private synchronized void saveMutes() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            // Create a snapshot for JSON serialization to avoid concurrent modification during gson write
            List<MuteData> snapshot = new ArrayList<>(mutes);
            gson.toJson(snapshot, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save mutes: " + e.getMessage());
        }
    }
    
    public boolean isMuted(Player player) {
        return isMuted(player.getName(), player.getUniqueId().toString());
    }
    
    public boolean isMuted(String playerName, String uuid) {
        for (MuteData mute : mutes) {
            if (mute.getPlayerName().equalsIgnoreCase(playerName) || 
                mute.getPlayerUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }
    
    public MuteData getMute(String playerName, String uuid) {
        for (MuteData mute : mutes) {
            if (mute.getPlayerName().equalsIgnoreCase(playerName) || 
                mute.getPlayerUuid().equals(uuid)) {
                return mute;
            }
        }
        return null;
    }
    
    public void mutePlayer(Player target, String reason, String muterName) {
        mutePlayer(target.getName(), target.getUniqueId().toString(), reason, muterName);
    }
    
    public void mutePlayer(String playerName, String uuid, String reason, String muterName) {
        // Remove existing mute if any
        unmutePlayer(playerName, uuid);
        
        MuteData muteData = new MuteData(playerName, uuid, reason, System.currentTimeMillis(), muterName);
        mutes.add(muteData);
        saveMutes();
    }
    
    public boolean unmutePlayer(String playerName) {
        boolean removed = mutes.removeIf(mute -> mute.getPlayerName().equalsIgnoreCase(playerName));
        
        if (removed) {
            saveMutes();
        }
        return removed;
    }
    
    public boolean unmutePlayer(String playerName, String uuid) {
        boolean removed = mutes.removeIf(mute -> {
            if (mute.getPlayerName().equalsIgnoreCase(playerName) ||
                mute.getPlayerUuid().equals(uuid)) {
                return true;
            }
            return false;
        });
        
        if (removed) {
            saveMutes();
        }
        return removed;
    }
    
    public void enrichOfflineMuteData(String playerName, String uuid) {
        boolean updated = false;
        for (MuteData mute : mutes) {
            if (mute.getPlayerName().equalsIgnoreCase(playerName)) {
                if (mute.getPlayerUuid().isEmpty()) {
                    mute.setPlayerUuid(uuid);
                    updated = true;
                }
            }
        }
        if (updated) {
            saveMutes();
        }
    }
    
    public List<MuteData> getMutes() {
        return new ArrayList<>(mutes);
    }
}