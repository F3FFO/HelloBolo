package com.f3ffo.hellobusbologna.hellobus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public class DownloadCsv extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String version;

    public DownloadCsv(Context context) {
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
            Log.e("ERROR checkFile", e.getMessage());
        }
        File[] listFiles = context.getFilesDir().listFiles();
        if (listFiles.length != 0) {
            for (File listFile : listFiles) {
                if (!listFile.isDirectory() && listFile.getName().contains(this.version) && !listFile.getName().contains("cut_")) {
                    try {
                        FileUtils.touch(new File(context.getFilesDir(), "cut_" + version + ".csv"));
                        FileUtils.touch(new File(context.getFilesDir(), "favourites.properties"));
                    } catch (IOException e) {
                        Log.e("ERROR createCutFile", e.getMessage());
                    }
                }
            }
        } else {
            try {
                FileUtils.touch(new File(context.getFilesDir(), "cut_" + version + ".csv"));
                FileUtils.touch(new File(context.getFilesDir(), "favourites.properties"));
            } catch (IOException e) {
                Log.e("ERROR createCutFile", e.getMessage());
            }
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        checkFile();
        File[] listFiles = context.getFilesDir().listFiles();
        for (File listFile : listFiles) {
            if (!listFile.isDirectory() && !listFile.getName().contains(this.version) && !listFile.getName().equals("favourites.properties")) {
                try {
                    FileUtils.forceDelete(new File(context.getFilesDir(), listFile.getName()));
                    Log.i("FILE DELETED", listFile.getName());
                } catch (IOException e) {
                    Log.e("FILE DELETED", listFile.getName());
                }
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ReadableByteChannel readableByteChannel = null;
            FileOutputStream outputStream = null;
            try {
                URL url = new URL("https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=lineefermate&version=" + version + "&format=csv");
                readableByteChannel = Channels.newChannel(url.openStream());
                outputStream = context.openFileOutput("lineefermate_" + version + ".csv", Context.MODE_PRIVATE);
                outputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                Log.e("ERROR fileDownload_O", e.getMessage());
            } finally {
                try {
                    if (outputStream != null && readableByteChannel != null) {
                        outputStream.close();
                        readableByteChannel.close();
                    }
                } catch (IOException e) {
                    Log.e("ERROR closeDownload", e.getMessage());
                }
            }
        } else {
            try {
                URL url = new URL("https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=lineefermate&version=" + version + "&format=csv");
                FileUtils.copyURLToFile(url, new File(context.getFilesDir() + "/lineefermate_" + version + ".csv"));
            } catch (IOException e) {
                Log.e("ERROR fileDownload_M", e.getMessage());
            }
        }
        return null;
    }
}
