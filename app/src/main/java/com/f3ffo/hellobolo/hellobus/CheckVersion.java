package com.f3ffo.hellobolo.hellobus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.f3ffo.hellobolo.R;
import com.f3ffo.hellobolo.utility.Log;
import com.f3ffo.hellobolo.asyncInterface.AsyncResponseVersion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CheckVersion extends AsyncTask<Void, Void, Boolean> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String version;
    private AsyncResponseVersion delegate;

    public CheckVersion(Context context, AsyncResponseVersion delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    private boolean fileExist() {
        File[] listFiles = context.getFilesDir().listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.getName().equals("lineefermate_" + version + ".csv") && FileUtils.sizeOf(file) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean takeCurrVersion() {
        File[] listFiles = context.getFilesDir().listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.getName().contains("lineefermate_") && file.getName().contains(".csv")) {
                    version = file.getName().substring(file.getName().lastIndexOf("_") + 1, file.getName().lastIndexOf("."));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean csvVersion() {
        try {
            Request get = new Request.Builder().url("https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=opendata-versione&version=1&format=csv").build();
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(new OkHttpClient().newCall(get).execute().body()).byteStream(), StandardCharsets.UTF_8));
            String versionUpdate;
            do {
                versionUpdate = br.readLine();
                if (versionUpdate.contains("<!DOCTYPE html")) {
                    return false;
                }
            } while (!versionUpdate.startsWith("lineefermate"));
            version = versionUpdate.substring(versionUpdate.lastIndexOf(";") + 1);
            br.close();
        } catch (IOException e) {
            Log.logError(context, e);
            return false;
        }
        return true;
    }

    private boolean xmlVersion() {
        try {
            Request get = new Request.Builder().url("https://solweb.tper.it/web/tools/open-data/open-data-viewer.aspx?&filename=opendata-versione&version=1").build();
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(new OkHttpClient().newCall(get).execute().body()).byteStream(), StandardCharsets.UTF_8));
            String versionUpdate;
            do {
                versionUpdate = br.readLine();
            } while (!versionUpdate.contains("<td>lineefermate</td><td>"));
            version = versionUpdate.substring(versionUpdate.indexOf("</td><td>") + 9, versionUpdate.length() - 5);
            br.close();
        } catch (IOException e) {
            Log.logError(context, e);
            return false;
        }
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            if (!csvVersion() && !xmlVersion() && !takeCurrVersion()) {
                return false;
            }
            FileUtils.touch(new File(context.getFilesDir(), "cut_" + version + ".csv"));
            FileUtils.touch(new File(context.getFilesDir(), "favourites.properties"));
            Properties prop = new Properties();
            prop.load(context.openFileInput("favourites.properties"));
            if (prop.stringPropertyNames().size() == 0) {
                for (int i = 0; i < 10; i++) {
                    prop.setProperty("busStopCode.Fav." + i, "");
                    prop.store(context.openFileOutput("favourites.properties", Context.MODE_PRIVATE), "User favourites");
                }
            }
            return true;
        } catch (IOException e) {
            Log.logError(context, e);
            new MaterialAlertDialogBuilder(context, R.style.DialogTheme)
                    .setTitle(R.string.dialog_no_service_title)
                    .setPositiveButton(R.string.dialog_generic_yes, (DialogInterface dialog, int which) -> dialog.dismiss())
                    .show();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean && !fileExist()) {
            delegate.processFinisVersion(version);
        }
    }
}