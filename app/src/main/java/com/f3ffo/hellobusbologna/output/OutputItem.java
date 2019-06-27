package com.f3ffo.hellobusbologna.output;

public class OutputItem {
    private String busNumber, busHour, busHourComplete, error;
    private int satelliteOrHour, handicap;

    public OutputItem(String busNumber, String busHour, String busHourComplete, int satelliteOrHour, int handicap) {
        this.busNumber = busNumber;
        this.busHour = busHour;
        this.busHourComplete = busHourComplete;
        this.satelliteOrHour = satelliteOrHour;
        this.handicap = handicap;
    }

    public OutputItem(String error) {
        this.error = error;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusHour() {
        return busHour;
    }

    public void setBusHour(String busHour) {
        this.busHour = busHour;
    }

    public String getBusHourComplete() {
        return busHourComplete;
    }

    public void setBusHourComplete(String busHourComplete) {
        this.busHourComplete = busHourComplete;
    }

    public int getSatelliteOrHour() {
        return satelliteOrHour;
    }

    public void setSatelliteOrHour(int satelliteOrHour) {
        this.satelliteOrHour = satelliteOrHour;
    }

    public int getHandicap() {
        return handicap;
    }

    public void setHandicap(int handicap) {
        this.handicap = handicap;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
