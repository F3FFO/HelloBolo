package com.f3ffo.hellobolo;

import android.content.Context;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Log {

    public static String LOG_FILENAME = "log.log";

    public static void logFile(Context context, @NotNull Exception e) {
        try {
            FileUtils.writeStringToFile(new File(context.getFilesDir(), LOG_FILENAME), e.getMessage(), StandardCharsets.UTF_8, true);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}