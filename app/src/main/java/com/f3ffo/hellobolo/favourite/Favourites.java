package com.f3ffo.hellobolo.favourite;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class Favourites {

    private String fileName = "favourites.properties";
    private List<FavouritesItem> favouritesList = new ArrayList<>();
    private Properties prop = new Properties();

    public List<FavouritesItem> getFavouritesList() {
        return favouritesList;
    }

    public void readFile(Context context) {
        try {
            prop.load(context.openFileInput(this.fileName));
            for (int i = 0; i < prop.size(); i++) {
                String line = prop.getProperty("busStopCode.Fav." + i);
                if (!line.equals("")) {
                    StringTokenizer token = new StringTokenizer(line, ";");
                    String busStopCode = token.nextToken();
                    String busStopName = token.nextToken();
                    String busStopAddress = token.nextToken();
                    String latitude = token.nextToken();
                    String longitude = token.nextToken();
                    favouritesList.add(new FavouritesItem(busStopCode, busStopName, busStopAddress, latitude, longitude));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FavouritesItem addFavourite(Context context, String busStopCode, String busStopName, String busStopAddress, String latitude, String longitude) {
        FavouritesItem item = null;
        try {
            prop.load(context.openFileInput(this.fileName));
            boolean isAdded = false;
            for (int i = 0; i < 10 && !isAdded; i++) {
                if (prop.getProperty("busStopCode.Fav." + i).equals("")) {
                    isAdded = true;
                    prop.setProperty("busStopCode.Fav." + i, busStopCode + ";" + busStopName + ";" + busStopAddress + ";" + latitude + ";" + longitude);
                    prop.store(context.openFileOutput(this.fileName, Context.MODE_PRIVATE), "User favourite");
                    item = new FavouritesItem(busStopCode, busStopName, busStopAddress, latitude, longitude);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    public boolean removeFavourite(Context context, String busStopCode) {
        boolean ris = false;
        try {
            prop.load(context.openFileInput(this.fileName));
            String line;
            for (int i = 0; i < prop.stringPropertyNames().size(); i++) {
                line = prop.getProperty("busStopCode.Fav." + i);
                if (line != null && line.contains(busStopCode)) {
                    prop.setProperty("busStopCode.Fav." + i, "");
                    prop.store(context.openFileOutput(this.fileName, Context.MODE_PRIVATE), "User favourite");
                    ris = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ris;
    }
}