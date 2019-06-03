package com.f3ffo.hellobusbologna.hellobus;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Favourites {

    private String fileName = "favourites.properties";

    private void createFile(Context context) {
        File[] listFiles = context.getFilesDir().listFiles();
        for (File listFile : listFiles) {
            if (!listFile.getName().equals(this.fileName)) {
                new File(context.getFilesDir(), this.fileName);
            }
        }
    }

    public void addFavourite(Context context, String busStopCode, String busStopName, String busStopAddress) {
        createFile(context);
        try {
            FileUtils.writeStringToFile(new File(context.getFilesDir() + "/" + this.fileName), busStopCode + "," + busStopName + "," + busStopAddress + "\n", StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            Log.e("ERROR: ", e.getMessage());
        }
    }

    public void removeFavourite(Context context, String busStopCode) {
        File file = new File(context.getFilesDir() + "/" + this.fileName);
    }
}
