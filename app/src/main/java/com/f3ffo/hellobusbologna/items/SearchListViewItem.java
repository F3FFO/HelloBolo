package com.f3ffo.hellobusbologna.items;

public class SearchListViewItem {
    private int imageSearch;
    private String busStopCode, busStopName, busStopAddres;

    public SearchListViewItem(int imageSearch, String busStopCode, String busStopName, String busStopAddres) {
        this.imageSearch = imageSearch;
        this.busStopCode = busStopCode;
        this.busStopName = busStopName;
        this.busStopAddres = busStopAddres;
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

    public String getBusStopAddres() {
        return busStopAddres;
    }

    public void setBusStopAddres(String busStopAddres) {
        this.busStopAddres = busStopAddres;
    }
}
