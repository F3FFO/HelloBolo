package com.f3ffo.hellobusbologna.hellobus;

import android.content.Context;

import com.f3ffo.hellobusbologna.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Favourites {
    private Context context;

    public Favourites(Context context) {
        this.context = context;
    }

    public void createFavouriteFile() {
        String path = "";
        Properties favourite = new Properties();
        try {
            favourite.load(new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.favourites), StandardCharsets.UTF_8)));
        } catch (IOException e) {

        }
    }
}
