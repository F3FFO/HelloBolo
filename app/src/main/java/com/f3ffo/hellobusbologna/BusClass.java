package com.f3ffo.hellobusbologna;

public class BusClass {

    private String lineCode;
    private String stopCode;
    private String stopName;
    private String zoneCode;

    public BusClass(String lineCode, String stopCode, String stopName, String zoneCode) {
        this.lineCode = lineCode;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.zoneCode = zoneCode;
    }

    public BusClass(String lineCode, String stopCode, String stopName) {
        this.lineCode = lineCode;
        this.stopCode = stopCode;
        this.stopName = stopName;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
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
