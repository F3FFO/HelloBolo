package com.f3ffo.hellobusbologna.search;

public class SearchListViewItem {

    private int imageFavourite;
    private String busStopCode, busStopName, busStopAddress;

    public SearchListViewItem(String busStopCode, String busStopName, String busStopAddress, int imageFavourite) {
        this.busStopCode = busStopCode;
        this.busStopName = busStopName;
        this.busStopAddress = busStopAddress;
        this.imageFavourite = imageFavourite;
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

    public int getImageFavourite() {
        return imageFavourite;
    }

    public void setImageFavourite(int imageFavourite) {
        this.imageFavourite = imageFavourite;
    }
}
