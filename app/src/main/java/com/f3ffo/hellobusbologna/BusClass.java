package com.f3ffo.hellobusbologna;

public class BusClass {

    private String busCode;
    private String stopCode;
    private String stopName;
    private String zoneCode;

    public BusClass(String busCode, String stopCode, String stopName, String zoneCode) {
        this.busCode = busCode;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.zoneCode = zoneCode;
    }

    public BusClass(String busCode, String stopCode, String stopName) {
        this.busCode = busCode;
        this.stopCode = stopCode;
        this.stopName = stopName;
    }

    public BusClass(String busCode, String stopCode) {
        this.busCode = busCode;
        this.stopCode = stopCode;
    }

    public BusClass(String busCode) {
        this.busCode = busCode;
    }

    public BusClass() {
    }

    public String getbusCode() {
        return busCode;
    }

    public void setbusCode(String busCode) {
        this.busCode = busCode;
    }

    public String getStopCode() {
        return stopCode;
    }

    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }
}
