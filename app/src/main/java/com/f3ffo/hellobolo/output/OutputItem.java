package com.f3ffo.hellobolo.output;

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

    public String getBusHour() {
        return busHour;
    }

    public String getBusHourComplete() {
        return busHourComplete;
    }

    public int getSatelliteOrHour() {
        return satelliteOrHour;
    }

    public int getHandicap() {
        return handicap;
    }

    public String getError() {
        return error;
    }
}