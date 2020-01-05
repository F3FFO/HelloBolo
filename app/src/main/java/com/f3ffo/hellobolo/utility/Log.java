package com.f3ffo.hellobolo.utility;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Log {

    public static String LOG_FILENAME = "log.log";

    public static void logFile(@NotNull Context context, @NotNull Exception e) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(new File(context.getFilesDir(), LOG_FILENAME), true));
            e.printStackTrace(printWriter);
            printWriter.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static ArrayList<String> getLog(@NotNull Context context) {
        ArrayList<String> file = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(new File(LOG_FILENAME).getName()), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                file.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            logFile(context, e);
        }
        return file;
    }
}