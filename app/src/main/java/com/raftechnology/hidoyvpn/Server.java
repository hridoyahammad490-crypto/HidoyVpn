package com.raftechnology.hidoyvpn;

public class Server {
    private final String countryName;
    private final String flagEmoji;
    private final String configAssetName; // e.g. "us.ovpn" - put matching file in assets/
    private final int pingMs;

    public Server(String countryName, String flagEmoji, String configAssetName, int pingMs) {
        this.countryName = countryName;
        this.flagEmoji = flagEmoji;
        this.configAssetName = configAssetName;
        this.pingMs = pingMs;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getFlagEmoji() {
        return flagEmoji;
    }

    public String getConfigAssetName() {
        return configAssetName;
    }

    public int getPingMs() {
        return pingMs;
    }
}