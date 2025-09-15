package com.spy.spysimpleban;

public class BanData {
    private String playerName;
    private String playerUuid;
    private String playerIp;
    private String reason;
    private long banTime;
    private long expireTime; // 0 = permanent ban
    private String bannerName;
    
    public BanData(String playerName, String playerUuid, String playerIp, String reason, long banTime, long expireTime, String bannerName) {
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.playerIp = playerIp;
        this.reason = reason;
        this.banTime = banTime;
        this.expireTime = expireTime;
        this.bannerName = bannerName;
    }
    
    public boolean isPermanent() {
        return expireTime == 0;
    }
    
    public boolean isExpired() {
        return !isPermanent() && System.currentTimeMillis() > expireTime;
    }
    
    // Getters and Setters
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public String getPlayerUuid() { return playerUuid; }
    public void setPlayerUuid(String playerUuid) { this.playerUuid = playerUuid; }
    
    public String getPlayerIp() { return playerIp; }
    public void setPlayerIp(String playerIp) { this.playerIp = playerIp; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public long getBanTime() { return banTime; }
    public void setBanTime(long banTime) { this.banTime = banTime; }
    
    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
    
    public String getBannerName() { return bannerName; }
    public void setBannerName(String bannerName) { this.bannerName = bannerName; }
}