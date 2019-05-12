package com.f3ffo.hellobusbologna;

public class CardViewItem {
    private String busNumber, busHour;
    private int image;

    public CardViewItem(String busNumber, String busHour, int image) {
        this.busNumber = busNumber;
        this.busHour = busHour;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
