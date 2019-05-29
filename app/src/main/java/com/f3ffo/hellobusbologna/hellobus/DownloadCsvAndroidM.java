package com.f3ffo.hellobusbologna.hellobus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DownloadCsvAndroidM extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String version;

    public DownloadCsvAndroidM(Context context) {
        this.context = context;
    }

    private void checkFile() {
        try {
            HttpURLConnection huc = (HttpURLConnection) new URL("https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=opendata-versione&version=1&format=csv").openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream(), StandardCharsets.UTF_8));
            String versionUpdate;
            do {
                versionUpdate = br.readLine();
            } while (!versionUpdate.startsWith("lineefermate"));
            this.version = versionUpdate.substring(versionUpdate.lastIndexOf(";") + 1);
        } catch (IOException e) {
            Log.e("ERROR: ", e.getMessage());
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        checkFile();
        File[] listFiles = context.getFilesDir().listFiles();
        for (File listFile : listFiles) {
            if (!listFile.getName().contains(this.version) && !listFile.getName().equals("favourites.properties")) {
                try {
                FileUtils.forceDelete(listFile);
                Log.i("FILE DELETED: ", listFile.getName());
            }
        }
        File[] listFiles2 = context.getFilesDir().listFiles();
        if (listFiles2.length == 0 || !listFiles2[0].getName().contains(this.version)) {
            try {
                URL url = new URL("https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=lineefermate&version=" + version + "&format=csv");
                FileUtils.copyURLToFile(url, new File(context.getFilesDir() + "/lineefermate_" + version + ".csv"));
            } catch (IOException e) {
                Log.e("ERROR fileDownloadM: ", e.getMessage());
            }
        }
        return null;
    }
}
