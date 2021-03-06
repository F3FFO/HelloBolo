package com.f3ffo.hellobolo.hellobus;

public class BusClass {

    private String busCode, busStopCode, busStopName, busStopAddress, latitude, longitude;

    BusClass(String busCode, String busStopCode, String busStopName, String busStopAddress, String latitude, String longitude) {
        this.busCode = busCode;
        this.busStopCode = busStopCode;
        this.busStopName = busStopName;
        this.busStopAddress = busStopAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getBusCode() {
        return busCode;
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