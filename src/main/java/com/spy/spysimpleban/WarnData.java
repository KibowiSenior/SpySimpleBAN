package com.spy.spysimpleban;

public class WarnData {
    private String playerName;
    private String playerUuid;
    private String reason;
    private long warnTime;
    private String warnerName;
    
    public WarnData(String playerName, String playerUuid, String reason, long warnTime, String warnerName) {
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.reason = reason;
        this.warnTime = warnTime;
        this.warnerName = warnerName;
    }
    
    // Getters and Setters
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public String getPlayerUuid() { return playerUuid; }
    public void setPlayerUuid(String playerUuid) { this.playerUuid = playerUuid; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public long getWarnTime() { return warnTime; }
    public void setWarnTime(long warnTime) { this.warnTime = warnTime; }
    
    public String getWarnerName() { return warnerName; }
    public void setWarnerName(String warnerName) { this.warnerName = warnerName; }
}