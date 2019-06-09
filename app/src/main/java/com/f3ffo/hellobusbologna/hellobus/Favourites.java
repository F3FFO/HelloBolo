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
            for (int i = 0; i < prop.stringPropertyNames().size(); i++) {
                String line;
                line = prop.getProperty("busStopCode.Fav." + i);
                StringTokenizer token = new StringTokenizer(line, ",");
                String busStopCode = token.nextToken();
                String busStopName = token.nextToken();
                String busStopAddress = token.nextToken();
                favouritesList.add(new FavouritesViewItem(busStopCode, busStopName, busStopAddress));
            }
        } catch (IOException e) {
            Log.e("ERROR readFileFav", e.getMessage());
        }
    }

    public FavouritesViewItem addFavourite(Context context, String busStopCode, String busStopName, String busStopAddress) {
        FavouritesViewItem item = null;
        try {
            prop.load(context.openFileInput(this.fileName));
            if (prop.stringPropertyNames().size() < 10) {
                prop.setProperty("busStopCode.Fav." + prop.stringPropertyNames().size(), busStopCode + "," + busStopName + "," + busStopAddress);
                prop.store(context.openFileOutput(this.fileName, Context.MODE_PRIVATE), "User favourite");
                item = new FavouritesViewItem(busStopCode, busStopName, busStopAddress);
            }
        } catch (IOException e) {
            Log.e("ERROR addFavourite", e.getMessage());
        }
        return item;
    }

    public boolean removeFavourite(Context context, String busStopCode) {
        try {
            prop.load(context.openFileInput(this.fileName));
            String line;
            for (int i = 0; i < prop.stringPropertyNames().size(); i++) {
                line = prop.getProperty("busStopCode.Fav." + i);
                if (line.contains(busStopCode)) {
                    prop.remove("busStopCode.Fav." + i);
                }
            }
            return true;
        } catch (IOException e) {
            Log.e("ERROR removeFavourite", e.getMessage());
            return false;
        }
    }
}
