package com.f3ffo.hellobusbologna.model;

public class BusClass {

    private String busCode;
    private String busStopCode;
    private String busStopName;
    private String busStopAddress;

    public BusClass(String busCode, String busStopCode, String busStopName, String busStopAddress) {
        this.busCode = busCode;
        this.busStopCode = busStopCode;
        this.busStopName = busStopName;
        this.busStopAddress = busStopAddress;
    }

    public String getbusCode() {
        return busCode;
    }

    public void setbusCode(String busCode) {
        this.busCode = busCode;
    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
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
