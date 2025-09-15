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
import java.util.UUID;

public class BanManager {
    
    private final JavaPlugin plugin;
    private final File dataFile;
    private final Gson gson;
    private List<BanData> bans;
    
    public BanManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "bans.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.bans = new ArrayList<>();
        
        loadBans();
    }
    
    private void loadBans() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            saveBans();
            return;
        }
        
        try (FileReader reader = new FileReader(dataFile)) {
            Type listType = new TypeToken<List<BanData>>(){}.getType();
            List<BanData> loadedBans = gson.fromJson(reader, listType);
            if (loadedBans != null) {
                bans = loadedBans;
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load bans: " + e.getMessage());
        }
    }
    
    private void saveBans() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(bans, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save bans: " + e.getMessage());
        }
    }
    
    public boolean isBanned(Player player) {
        return isBanned(player.getName(), player.getUniqueId().toString(), getPlayerIP(player));
    }
    
    public boolean isBanned(String playerName, String uuid, String ip) {
        for (BanData ban : bans) {
            if (!ban.isExpired()) {
                if (ban.getPlayerName().equalsIgnoreCase(playerName) ||
                    ban.getPlayerUuid().equals(uuid) ||
                    ban.getPlayerIp().equals(ip)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public BanData getBan(String playerName, String uuid, String ip) {
        for (BanData ban : bans) {
            if (!ban.isExpired()) {
                if (ban.getPlayerName().equalsIgnoreCase(playerName) ||
                    ban.getPlayerUuid().equals(uuid) ||
                    ban.getPlayerIp().equals(ip)) {
                    return ban;
                }
            }
        }
        return null;
    }
    
    public void banPlayer(Player target, String reason, long expireTime, String bannerName) {
        banPlayer(target.getName(), target.getUniqueId().toString(), getPlayerIP(target), reason, expireTime, bannerName);
    }
    
    public void banPlayer(String playerName, String uuid, String ip, String reason, long expireTime, String bannerName) {
        // Remove existing ban if any
        unbanPlayer(playerName, uuid, ip);
        
        BanData banData = new BanData(playerName, uuid, ip, reason, System.currentTimeMillis(), expireTime, bannerName);
        bans.add(banData);
        saveBans();
    }
    
    public boolean unbanPlayer(String playerName) {
        boolean removed = bans.removeIf(ban -> ban.getPlayerName().equalsIgnoreCase(playerName));
        
        if (removed) {
            saveBans();
        }
        return removed;
    }
    
    public boolean unbanPlayer(String playerName, String uuid, String ip) {
        boolean removed = bans.removeIf(ban -> {
            if (ban.getPlayerName().equalsIgnoreCase(playerName) ||
                ban.getPlayerUuid().equals(uuid) ||
                ban.getPlayerIp().equals(ip)) {
                return true;
            }
            return false;
        });
        
        if (removed) {
            saveBans();
        }
        return removed;
    }
    
    public void enrichOfflineBanData(String playerName, String uuid, String ip) {
        boolean updated = false;
        for (BanData ban : bans) {
            if (ban.getPlayerName().equalsIgnoreCase(playerName)) {
                if (ban.getPlayerUuid().isEmpty()) {
                    ban.setPlayerUuid(uuid);
                    updated = true;
                }
                if (ban.getPlayerIp().isEmpty()) {
                    ban.setPlayerIp(ip);
                    updated = true;
                }
            }
        }
        if (updated) {
            saveBans();
        }
    }
    
    public void checkExpiredBans() {
        boolean removed = bans.removeIf(BanData::isExpired);
        if (removed) {
            saveBans();
        }
    }
    
    private String getPlayerIP(Player player) {
        return player.getAddress().getAddress().getHostAddress();
    }
    
    public List<BanData> getBans() {
        return new ArrayList<>(bans);
    }
}