package com.f3ffo.hellobusbologna.hellobus;

import android.content.Context;
import android.util.Log;

import com.f3ffo.hellobusbologna.model.FavouritesViewItem;

import org.apache.commons.io.FileUtils;

import java.io.File;
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

    public void readFile(Context context, boolean isFirstTime) {
        try {
            prop.load(context.openFileInput(this.fileName));
            if (isFirstTime) {
                for (int i = 0; i < prop.stringPropertyNames().size(); i++) {
                    String line;
                    line = prop.getProperty("busStopCode.Fav." + i);
                    StringTokenizer token = new StringTokenizer(line, ",");
                    String busStopCode = token.nextToken();
                    String busStopName = token.nextToken();
                    String busStopAddress = token.nextToken();
                    favouritesList.add(new FavouritesViewItem(busStopCode, busStopName, busStopAddress));
                }
            } else {
                String line;
                line = prop.getProperty("busStopCode.Fav." + (prop.stringPropertyNames().size() - 1));
                StringTokenizer token = new StringTokenizer(line, ",");
                String busStopCode = token.nextToken();
                String busStopName = token.nextToken();
                String busStopAddress = token.nextToken();
                favouritesList.add(new FavouritesViewItem(busStopCode, busStopName, busStopAddress));
            }
        } catch (IOException e) {
            Log.e("ERROR readFileFav: ", e.getMessage());
        }
    }

    private void createFile(Context context) {
        File[] listFiles = context.getFilesDir().listFiles();
        for (File listFile : listFiles) {
            if (!listFile.getName().equals(this.fileName)) {
                try {
                    FileUtils.touch(new File(context.getFilesDir(), this.fileName));
                } catch (IOException e) {
                    Log.e("ERROR createFileFav: ", e.getMessage());
                }
            }
        }
    }

    public boolean addFavourite(Context context, String busStopCode, String busStopName, String busStopAddress) {
        try {
            createFile(context);
            prop.load(context.openFileInput(this.fileName));
            if (prop.stringPropertyNames().size() < 10) {
                prop.setProperty("busStopCode.Fav." + prop.stringPropertyNames().size(), busStopCode + "," + busStopName + "," + busStopAddress);
                prop.store(context.openFileOutput(this.fileName, Context.MODE_PRIVATE), "User favourite");
                return true;
            }
            return false;
        } catch (IOException e) {
            Log.e("ERROR addFavourite: ", e.getMessage());
            return false;
        }
    }

    public void removeFavourite(Context context, String busStopCode) {
        try {
            prop.load(context.openFileInput(this.fileName));
            String line;
            for (int i = 0; i < prop.stringPropertyNames().size(); i++) {
                //TODO create delete
            }
        } catch (IOException e) {
            Log.e("ERROR: ", e.getMessage());
        }
    }
}
