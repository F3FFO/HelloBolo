package com.f3ffo.hellobusbologna.model;

public class BusClass {

    private String busCode;
    private String stopCode;
    private String stopName;
    private String stopAddress;

    public BusClass(String busCode, String stopCode, String stopName, String stopAddress) {
        this.busCode = busCode;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.stopAddress = stopAddress;
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

    public String getStopAddress() {
        return stopAddress;
    }

    public void setStopAddress(String stopAddress) {
        this.stopAddress = stopAddress;
    }
}
