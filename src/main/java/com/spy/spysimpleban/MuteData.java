package com.spy.spysimpleban;

public class MuteData {
    private String playerName;
    private String playerUuid;
    private String reason;
    private long muteTime;
    private String muterName;
    
    public MuteData(String playerName, String playerUuid, String reason, long muteTime, String muterName) {
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.reason = reason;
        this.muteTime = muteTime;
        this.muterName = muterName;
    }
    
    // Getters and Setters
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public String getPlayerUuid() { return playerUuid; }
    public void setPlayerUuid(String playerUuid) { this.playerUuid = playerUuid; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public long getMuteTime() { return muteTime; }
    public void setMuteTime(long muteTime) { this.muteTime = muteTime; }
    
    public String getMuterName() { return muterName; }
    public void setMuterName(String muterName) { this.muterName = muterName; }
}