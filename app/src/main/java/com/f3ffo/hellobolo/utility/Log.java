package com.f3ffo.hellobolo.utility;

import android.content.Context;
import android.os.Build;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Log {

    public static String LOG_FILENAME = "log.log";

    public static void logInfo(@NotNull Context context) {
        try {
            FileUtils.writeLines(new File(context.getFilesDir(), LOG_FILENAME), setInfoLog());
        } catch (IOException e) {
            logError(context, e);
        }
    }

    private static List<String> setInfoLog() {
        List<String> info = new ArrayList<>();
        info.add("--------- information");
        info.add("Manufacturer: " + Build.MANUFACTURER);
        info.add("Brand: " + Build.BRAND);
        info.add("Device: " + Build.DEVICE);
        info.add("Product: " + Build.PRODUCT);
        info.add("Model: " + Build.MODEL);
        info.add("Bootloader: " + Build.BOOTLOADER);
        info.add("Fingerprint: " + Build.FINGERPRINT);
        info.add("Android release: " + Build.VERSION.RELEASE);
        info.add("Sdk version: " + Build.VERSION.SDK_INT);
        info.add("--------- error");
        return info;
    }

    public static void logError(@NotNull Context context, @NotNull Exception error) {
        File file = new File(context.getFilesDir(), LOG_FILENAME);
        System.out.println(Double.parseDouble(String.valueOf(file.length())));
        if (Double.parseDouble(String.valueOf(file.length())) > 8e+6) {
            FileUtils.deleteQuietly(file);
            logInfo(context);
        }
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(file, true));
            error.printStackTrace(printWriter);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getLog(@NotNull Context context) {
        List<String> file = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(new File(LOG_FILENAME).getName()), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                file.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            logError(context, e);
        }
        return file;
    }
}