package com.f3ffo.hellobusbologna.hellobus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DownloadCsv extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String version;

    public DownloadCsv(Context context, String version) {
        this.context = context;
        this.version = version;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File[] listFiles = context.getFilesDir().listFiles();
        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (!listFile.isDirectory() && !listFile.getName().contains(this.version) && !listFile.getName().equals("favourites.properties")) {
                    try {
                        FileUtils.forceDelete(new File(context.getFilesDir(), listFile.getName()));
                        Log.i("FILE_DELETED", listFile.getName());
                    } catch (IOException e) {
                        Log.e("ERROR FILE_DELETE", listFile.getName());
                    }
                }
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ReadableByteChannel readableByteChannel;
            FileOutputStream outputStream;
            try {
                URL url = new URL("https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=lineefermate&version=" + version + "&format=csv");
                readableByteChannel = Channels.newChannel(url.openStream());
                outputStream = context.openFileOutput("lineefermate_" + version + ".csv", Context.MODE_PRIVATE);
                outputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                outputStream.close();
                readableByteChannel.close();
            } catch (IOException e) {
                Log.e("ERROR fileDownload_O", e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                URL url = new URL("https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=lineefermate&version=" + version + "&format=csv");
                FileUtils.copyURLToFile(url, new File(context.getFilesDir() + "/lineefermate_" + version + ".csv"));
            } catch (IOException e) {
                Log.e("ERROR fileDownload_M", e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
}
