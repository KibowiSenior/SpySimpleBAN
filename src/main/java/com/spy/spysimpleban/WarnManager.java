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

public class WarnManager {
    
    private final JavaPlugin plugin;
    private final File dataFile;
    private final Gson gson;
    private List<WarnData> warns;
    
    public WarnManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "warns.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.warns = new CopyOnWriteArrayList<>();
        
        loadWarns();
    }
    
    private void loadWarns() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            saveWarns();
            return;
        }
        
        try (FileReader reader = new FileReader(dataFile)) {
            Type listType = new TypeToken<List<WarnData>>(){}.getType();
            List<WarnData> loadedWarns = gson.fromJson(reader, listType);
            if (loadedWarns != null) {
                warns = new CopyOnWriteArrayList<>(loadedWarns);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load warns: " + e.getMessage());
        }
    }
    
    private synchronized void saveWarns() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            // Create a snapshot for JSON serialization to avoid concurrent modification during gson write
            List<WarnData> snapshot = new ArrayList<>(warns);
            gson.toJson(snapshot, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save warns: " + e.getMessage());
        }
    }
    
    public void warnPlayer(Player target, String reason, String warnerName) {
        warnPlayer(target.getName(), target.getUniqueId().toString(), reason, warnerName);
    }
    
    public void warnPlayer(String playerName, String uuid, String reason, String warnerName) {
        WarnData warnData = new WarnData(playerName, uuid, reason, System.currentTimeMillis(), warnerName);
        warns.add(warnData);
        saveWarns();
    }
    
    public List<WarnData> getPlayerWarns(String playerName) {
        List<WarnData> playerWarns = new ArrayList<>();
        for (WarnData warn : warns) {
            if (warn.getPlayerName().equalsIgnoreCase(playerName)) {
                playerWarns.add(warn);
            }
        }
        return playerWarns;
    }
    
    public List<WarnData> getPlayerWarns(String playerName, String uuid) {
        List<WarnData> playerWarns = new ArrayList<>();
        for (WarnData warn : warns) {
            if (warn.getPlayerName().equalsIgnoreCase(playerName) || 
                warn.getPlayerUuid().equals(uuid)) {
                playerWarns.add(warn);
            }
        }
        return playerWarns;
    }
    
    public void enrichOfflineWarnData(String playerName, String uuid) {
        boolean updated = false;
        for (WarnData warn : warns) {
            if (warn.getPlayerName().equalsIgnoreCase(playerName)) {
                if (warn.getPlayerUuid().isEmpty()) {
                    warn.setPlayerUuid(uuid);
                    updated = true;
                }
            }
        }
        if (updated) {
            saveWarns();
        }
    }
    
    public List<WarnData> getWarns() {
        return new ArrayList<>(warns);
    }
    
    public boolean clearPlayerWarns(String playerName) {
        boolean removed = warns.removeIf(warn -> warn.getPlayerName().equalsIgnoreCase(playerName));
        
        if (removed) {
            saveWarns();
        }
        return removed;
    }
    
    public boolean clearPlayerWarns(String playerName, String uuid) {
        boolean removed = warns.removeIf(warn -> 
            warn.getPlayerName().equalsIgnoreCase(playerName) || 
            warn.getPlayerUuid().equals(uuid));
        
        if (removed) {
            saveWarns();
        }
        return removed;
    }
}