package com.f3ffo.hellobolo.favourite;

public class FavouritesItem {

    private String busStopCode, busStopName, busStopAddress;

    public FavouritesItem(String busStopCode, String busStopName, String busStopAddress) {
        this.busStopCode = busStopCode;
        this.busStopName = busStopName;
        this.busStopAddress = busStopAddress;
    }

    public String getBusStopCode() {
        return busStopCode;
    }

    public String getBusStopName() {
        return busStopName;
    }

    public String getBusStopAddress() {
        return busStopAddress;
    }
}