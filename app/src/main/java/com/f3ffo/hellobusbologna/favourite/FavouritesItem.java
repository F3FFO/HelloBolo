package com.f3ffo.hellobusbologna.favourite;

public class FavouritesItem {

    private String busStopCode, busStopName, busStopAddress, latitude, longitude;

    public FavouritesItem(String busStopCode, String busStopName, String busStopAddress, String latitude, String longitude) {
        this.busStopCode = busStopCode;
        this.busStopName = busStopName;
        this.busStopAddress = busStopAddress;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
