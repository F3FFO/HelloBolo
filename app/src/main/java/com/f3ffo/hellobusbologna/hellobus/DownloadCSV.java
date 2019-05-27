package com.f3ffo.hellobusbologna.hellobus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.f3ffo.hellobusbologna.model.CheckFileDate;

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
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloadCSV extends AsyncTask<Void, Void, Void> {

    private String pathfile = "";
    private Context context;


    public DownloadCSV(Context context) {
        this.context = context;
        File directory = context.getFilesDir();
        if (directory.exists()) {
            this.pathfile = directory.getAbsolutePath();
        }
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // Checking If The File Exists At The Specified Location Or Not
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Path filePathObj = Paths.get(pathfile);
                CheckFileDate check = checkFile();
                if (new File(filePathObj.toString()).exists()) {
                    //file esiste
                    if (!check.isVersioncheck()) {
                        fileDownload(check.getVersion());
                    } else {
                        Log.e("Error prova()", "error in DownloadCSV.prova");
                    }
                } else {
                    //file non esiste
                    fileCreation(check.getVersion());
                }
            } else {
                CheckFileDate check = checkFile();
                if (new File(pathfile).exists()) {
                    //file esiste
                    if (!check.isVersioncheck()) {
                        fileDownload(check.getVersion());
                    } else {
                        Log.i("prova", "UltimaVersioneGiaPresente");
                    }
                } else {
                    //file non esiste
                    fileCreation(check.getVersion());
                }
            }
        } catch (IOException e) {
            Log.e("ERROR: ", e.getMessage());
        }
        return null;
    }

    private CheckFileDate checkFile() throws IOException {
        String s = "https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=opendata-versione&version=1&format=csv";
        HttpURLConnection huc = (HttpURLConnection) new URL(s).openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream(), StandardCharsets.UTF_8));
        String s2;
        do {
            s2 = br.readLine();
        } while (!s2.startsWith("lineefermate"));
        s2 = s2.substring(s2.lastIndexOf(";") + 1);
        File f1 = new File(pathfile);
        File[] f2 = f1.listFiles();
        if (f2.length != 0) {
            String version = f2[f2.length - 1].getName().substring(13, f2[f2.length - 1].getName().length() - 4);
            Log.i("checkFile", "verison number:" + version);
            if (s2.equals(version)) {
                return new CheckFileDate(true); //se il file è gia l'ultima verisone
            } else {
                f2[0].delete();
                return new CheckFileDate(false, s2); ////se il file non è l'ultima verisone
            }
        } else {
            fileCreation("lineefermate_" + s2 + ".csv");
            return new CheckFileDate(false, s2); ////se il file non è l'ultima verisone
        }
    }

    private File fileCreation(String filename) {
        String path = pathfile + filename;
        try {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("File " + filename + " gia esistene");
            } else if (file.createNewFile()) {
                System.out.println("File " + filename + " creato");
            } else {
                System.out.println("Il file " + filename + " non può essere creato");
            }
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    public void fileDownload(String version) {
        URL urlObj;
        ReadableByteChannel rbcObj = null;
        FileOutputStream fOutStream = null;
        try {
            urlObj = new URL("https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=lineefermate&version=" + version + "&format=csv");
            rbcObj = Channels.newChannel(urlObj.openStream());
            //fOutStream = new FileOutputStream(pathfile + "lineefermate_" + version + ".csv");
            fOutStream = context.openFileOutput("lineefermate_" + version + ".csv", Context.MODE_PRIVATE);
            fOutStream.getChannel().transferFrom(rbcObj, 0, Long.MAX_VALUE);
            Log.i("fileDownload", "File successfully downloaded");
        } catch (IOException ioExObj) {
            Log.e("Error fileDownload",ioExObj.getMessage());
        } finally {
            try {
                if (fOutStream != null) {
                    fOutStream.close();
                }
                if (rbcObj != null) {
                    rbcObj.close();
                }
            } catch (IOException ioExObj) {
                Log.e("Error fileDownload", ioExObj.getMessage());
            }
        }
    }

}

