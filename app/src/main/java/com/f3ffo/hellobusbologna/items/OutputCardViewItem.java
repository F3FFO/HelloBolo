package com.f3ffo.hellobusbologna.items;

public class OutputCardViewItem {
    private String busNumber, busHour, busHourComplete;
    private int image;

    public OutputCardViewItem(String busNumber, String busHour, String busHourComplete, int image) {
        this.busNumber = busNumber;
        this.busHour = busHour;
        this.busHourComplete = busHourComplete;
        this.image = image;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
