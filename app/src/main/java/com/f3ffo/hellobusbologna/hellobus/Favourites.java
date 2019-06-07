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
    public int positionItemRemoved;

    public List<FavouritesViewItem> getFavouritesList() {
        return favouritesList;
    }

    public void readFile(Context context) {
        try {
            prop.load(context.openFileInput(this.fileName));
            //if (isFirstTime == 0) {
                for (int i = 0; i < prop.stringPropertyNames().size(); i++) {
                    String line;
                    line = prop.getProperty("busStopCode.Fav." + i);
                    StringTokenizer token = new StringTokenizer(line, ",");
                    String busStopCode = token.nextToken();
                    String busStopName = token.nextToken();
                    String busStopAddress = token.nextToken();
                    favouritesList.add(new FavouritesViewItem(busStopCode, busStopName, busStopAddress));
                }
            /*} else if (isFirstTime == 1) {
                String line;
                line = prop.getProperty("busStopCode.Fav." + (prop.stringPropertyNames().size() - 1));
                StringTokenizer token = new StringTokenizer(line, ",");
                String busStopCode = token.nextToken();
                String busStopName = token.nextToken();
                String busStopAddress = token.nextToken();
                favouritesList.add(new FavouritesViewItem(busStopCode, busStopName, busStopAddress));
            } else {
                favouritesList.remove();
            }*/
        } catch (IOException e) {
            Log.e("ERROR readFileFav: ", e.getMessage());
        }
    }

    public boolean addFavourite(Context context, String busStopCode, String busStopName, String busStopAddress) {
        try {
            prop.load(context.openFileInput(this.fileName));
            if (prop.stringPropertyNames().size() < 100) {
                prop.setProperty("busStopCode.Fav." + prop.stringPropertyNames().size(), busStopCode + "," + busStopName + "," + busStopAddress);
                prop.store(context.openFileOutput(this.fileName, Context.MODE_PRIVATE), "User favourite");
                favouritesList.get(0).setBusStopCode(busStopCode);
                favouritesList.get(0).setBusStopName(busStopName);
                favouritesList.get(0).setBusStopAddress(busStopAddress);
                //favouritesList.add(new FavouritesViewItem(busStopCode, busStopName, busStopAddress));
                return true;
            }
            return false;
        } catch (IOException e) {
            Log.e("ERROR addFavourite", e.getMessage());
            return false;
        }
    }

    public boolean removeFavourite(Context context, String busStopCode) {
        try {
            prop.load(context.openFileInput(this.fileName));
            String line;
            for (int i = 0; i < prop.stringPropertyNames().size(); i++) {
                line = prop.getProperty("busStopCode.Fav." + i);
                if (line.contains(busStopCode)) {
                    positionItemRemoved = i;
                    prop.remove("busStopCode.Fav." + i);
                }
            }
            return true;
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
            return false;
        }
    }
}
