package com.f3ffo.hellobusbologna.favourite;

public class FavouritesViewItem {

    private String busStopCode, busStopName, busStopAddress;

    public FavouritesViewItem(String busStopCode, String busStopName, String busStopAddress) {
        this.busStopCode = busStopCode;
        this.busStopName = busStopName;
        this.busStopAddress = busStopAddress;
    }

    public String getBusStopCode() {
        return busStopCode;
    }

    public void setBusStopCode(String busStopCode) {
        this.busStopCode = busStopCode;
    }

    public String getBusStopName() {
        return busStopName;
    }

    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    public String getBusStopAddress() {
        return busStopAddress;
    }

    public void setBusStopAddress(String busStopAddress) {
        this.busStopAddress = busStopAddress;
    }
}
