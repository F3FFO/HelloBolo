package com.f3ffo.hellobolo.search;

public class SearchItem {

    private int imageFavourite;
    private String busStopCode, busStopName, busStopAddress, latitude, longitude;

    public SearchItem(String busStopCode, String busStopName, String busStopAddress, int imageFavourite, String latitude, String longitude) {
        this.busStopCode = busStopCode;
        this.busStopName = busStopName;
        this.busStopAddress = busStopAddress;
        this.imageFavourite = imageFavourite;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getBusStopCode() {
        return busStopCode;
    }

    public String getBusStopName() {
        return busStopName;
    }

    public String getBusStopAddress() {
        return busStopAddress;
    }

    public int getImageFavourite() {
        return imageFavourite;
    }

    public void setImageFavourite(int imageFavourite) {
        this.imageFavourite = imageFavourite;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}