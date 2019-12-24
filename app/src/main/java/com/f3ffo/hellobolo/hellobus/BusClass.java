package com.f3ffo.hellobolo.hellobus;

import java.util.Comparator;

public class BusClass {

    private String busCode, busStopCode, busStopName, busStopAddress, latitude, longitude;

    public BusClass(String busCode, String busStopCode, String busStopName, String busStopAddress, String latitude, String longitude) {
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

    public static Comparator<BusClass> sortByLat = (BusClass obj1, BusClass obj2) -> obj1.latitude.compareTo(obj2.latitude);
}