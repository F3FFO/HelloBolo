package com.f3ffo.hellobusbologna.hellobus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CheckVersion extends AsyncTask<Void, Void, Boolean> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String version;

    public CheckVersion(Context context) {
        this.context = context;
    }

    private boolean takeFile() {
        File[] listFiles = context.getFilesDir().listFiles();
        boolean fileExists = false;
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.getName().equals("lineefermate_" + version + ".csv") && FileUtils.sizeOf(file) != 0) {
                    fileExists = true;
                }
            }
        }
        return fileExists;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Request get = new Request.Builder().url("https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=opendata-versione&version=1&format=csv").build();
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(new OkHttpClient().newCall(get).execute().body()).byteStream(), StandardCharsets.UTF_8));
            String versionUpdate;
            do {
                versionUpdate = br.readLine();
            } while (!versionUpdate.startsWith("lineefermate"));
            this.version = versionUpdate.substring(versionUpdate.lastIndexOf(";") + 1);
            Log.i("CONTROLLO VERSIONE", version);
            FileUtils.touch(new File(context.getFilesDir(), "cut_" + version + ".csv"));
            FileUtils.touch(new File(context.getFilesDir(), "favourites.properties"));
            return true;
        } catch (Exception e) {
            Log.e("ERROR CheckVersion01", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean && !takeFile()) {
            try {
                new DownloadCsv(this.context, this.version).execute().get();
            } catch (Exception e) {
                Log.e("ERROR CheckVersion02", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
