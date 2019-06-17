package com.f3ffo.hellobusbologna.hellobus;

import android.content.Context;
import android.util.Log;

import com.f3ffo.hellobusbologna.model.FavouritesViewItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class Favourites {

    private String fileName = "favourites.properties";
    private List<FavouritesViewItem> favouritesList = new ArrayList<>();
    private Properties prop = new Properties();

    public List<FavouritesViewItem> getFavouritesList() {
        return favouritesList;
    }

    public void readFile(Context context) {
        try {
            prop.load(context.openFileInput(this.fileName));
            for (int i = 0; i < prop.size(); i++) {
                String line = prop.getProperty("busStopCode.Fav." + i);
                if (!line.equals("")) {
                    StringTokenizer token = new StringTokenizer(line, ",");
                    String busStopCode = token.nextToken();
                    String busStopName = token.nextToken();
                    String busStopAddress = token.nextToken();
                    favouritesList.add(new FavouritesViewItem(busStopCode, busStopName, busStopAddress));
                }
            }
        } catch (IOException e) {
            Log.e("ERROR readFileFav", e.getMessage());
            e.printStackTrace();
        }
    }

    public FavouritesViewItem addFavourite(Context context, String busStopCode, String busStopName, String busStopAddress) {
        FavouritesViewItem item = null;
        try {
            prop.load(context.openFileInput(this.fileName));
            boolean isAdded = false;
            for (int i = 0; i < 10 && !isAdded; i++) {
                if (prop.getProperty("busStopCode.Fav." + i).equals("")) {
                    isAdded = true;
                    prop.setProperty("busStopCode.Fav." + i, busStopCode + "," + busStopName + "," + busStopAddress);
                    prop.store(context.openFileOutput(this.fileName, Context.MODE_PRIVATE), "User favourite");
                    item = new FavouritesViewItem(busStopCode, busStopName, busStopAddress);
                }
            }
        } catch (IOException e) {
            Log.e("ERROR addFavourite", e.getMessage());
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
            Log.e("ERROR removeFavourite", e.getMessage());
            e.printStackTrace();
        }
        return ris;
    }
}
