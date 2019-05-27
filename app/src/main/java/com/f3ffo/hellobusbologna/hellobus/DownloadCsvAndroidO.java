package com.f3ffo.hellobusbologna.hellobus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

public class DownloadCsvAndroidO extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String version;

    public DownloadCsvAndroidO(Context context) {
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
        File[] listFiles = new File(context.getFilesDir().getAbsolutePath()).listFiles();
        for (File listFile : listFiles) {
            if (!listFile.getName().contains(this.version)) {
                URL urlObj;
                ReadableByteChannel rbcObj = null;
                FileOutputStream outputStream = null;
                try {
                    urlObj = new URL("https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=lineefermate&version=" + version + "&format=csv");
                    rbcObj = Channels.newChannel(urlObj.openStream());
                    outputStream = context.openFileOutput("lineefermate_" + version + ".csv", Context.MODE_PRIVATE);
                    outputStream = context.openFileOutput("lineefermate_" + version + ".csv", Context.MODE_PRIVATE);
                    outputStream.getChannel().transferFrom(rbcObj, 0, Long.MAX_VALUE);
                    Log.i("fileDownload", "File successfully downloaded");
                } catch (IOException e) {
                    Log.e("Error fileDownload", e.getMessage());
                } finally {
                    if (!listFile.getName().equals("lineefermate_" + this.version + ".csv")) {
                        listFile.delete();
                        Log.i("File eliminato: ", listFile.getName());
                    }
                    try {
                        if (outputStream != null && rbcObj != null) {
                            outputStream.close();
                            rbcObj.close();
                        }
                    } catch (IOException ioExObj) {
                        Log.e("Error fileDownload", ioExObj.getMessage());
                    }
                }
            }
        }
        return null;
    }
}
