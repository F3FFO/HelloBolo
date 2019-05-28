package com.f3ffo.hellobusbologna.hellobus;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(this.fileName, Context.MODE_PRIVATE);
            outputStream.write((busStopCode + "," + busStopName + "," + busStopAddress).getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e("ERROR: ", e.getMessage());
        }
    }
}
