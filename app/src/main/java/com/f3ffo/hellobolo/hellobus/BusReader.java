package com.f3ffo.hellobolo.hellobus;

import android.content.Context;

import com.f3ffo.hellobolo.utility.Log;
import com.f3ffo.hellobolo.R;
import com.f3ffo.hellobolo.search.SearchItem;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class BusReader {

    private List<Double> arrayLatitude = new ArrayList<>();
    public static double distance = 0.001;

    public ArrayList<BusClass> extractFromFile(@NotNull Context context) {
        ArrayList<BusClass> busClass = new ArrayList<>();
        File[] listFiles = context.getFilesDir().listFiles();
        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (listFile.getName().contains("lineefermate_") && listFile.getName().contains(".csv")) {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(listFile.getName()), StandardCharsets.UTF_8));
                        String line;
                        boolean doubleComma = false;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains(";;")) {
                                line = line.replace(";;", ";");
                                doubleComma = true;
                            }
                            if (!line.startsWith("codice_linea")) {
                                StringTokenizer token = new StringTokenizer(line, ";");
                                String busCode = token.nextToken();
                                String stopCode = token.nextToken();
                                String stopName = token.nextToken();
                                String stopAddress = StringUtils.lowerCase(token.nextToken());
                                token.nextToken();
                                if (doubleComma) {
                                    doubleComma = false;
                                } else {
                                    token.nextToken();
                                }
                                token.nextToken();
                                String latitude = token.nextToken();
                                if (latitude.length() < 9) {
                                    for (int i = 0; i < (9 - latitude.length()); i++) {
                                        new StringBuilder(latitude).append("0");
                                    }
                                }
                                latitude = latitude.replace(",", ".");
                                arrayLatitude.add(Double.parseDouble(latitude));
                                String longitude = token.nextToken();
                                if (longitude.length() < 9) {
                                    for (int i = 0; i < (9 - longitude.length()); i++) {
                                        new StringBuilder(longitude).append("0");
                                    }
                                }
                                longitude = longitude.replace(",", ".");
                                busClass.add(new BusClass(busCode, stopCode, stopName, StringUtils.capitalize(stopAddress), latitude, longitude));
                            }
                        }
                        bufferedReader.close();
                    } catch (IOException e) {
                        busClass.clear();
                        Log.logFile(context, e);
                    }
                }
            }
        }
        return busClass;
    }

    private File takeFileCut(@NotNull Context context) {
        File[] listFiles = context.getFilesDir().listFiles();
        File file = null;
        for (File listFile : listFiles) {
            if (listFile.getName().contains("cut_") && listFile.getName().contains(".csv")) {
                file = listFile;
            }
        }
        return file;
    }

    private String[] takeFavElement(@NotNull Context context) {
        Properties prop = new Properties();
        String[] propertiesFile = new String[10];
        try {
            prop.load(context.openFileInput("favourites.properties"));
            for (int i = 0; i < propertiesFile.length; i++) {
                if (!prop.getProperty("busStopCode.Fav." + i).equals("")) {
                    propertiesFile[i] = prop.getProperty("busStopCode.Fav." + i).substring(0, prop.getProperty("busStopCode.Fav." + i).indexOf(";"));
                } else {
                    propertiesFile[i] = "";
                }
            }
        } catch (IOException e) {
            Log.logFile(context, e);
        }
        return propertiesFile;
    }

    private void writeFile(Context context, File file, @NotNull ArrayList<BusClass> busClass) {
        ArrayList<String> stopsTemp = new ArrayList<>();
        String busStopCode;
        for (int i = 0; i < busClass.size(); i++) {
            busStopCode = busClass.get(i).getBusStopCode();
            if (!stopsTemp.contains(busStopCode)) {
                stopsTemp.add(busStopCode);
                try {
                    FileUtils.writeStringToFile(new File(context.getFilesDir(), file.getName()), (busStopCode + ";" + busClass.get(i).getBusStopName() + ";" + busClass.get(i).getBusStopAddress() + ";" + busClass.get(i).getLatitude().replace(",", ".") + ";" + busClass.get(i).getLongitude().replace(",", ".") + "\n"), StandardCharsets.UTF_8, true);
                } catch (IOException e) {
                    Log.logFile(context, e);
                }
            }
        }
        stopsTemp.clear();
    }

    private void extractFromFileCutted(@NotNull Context context, @NotNull File file, String[] propertiesFile, List<SearchItem> stops) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(file.getName()), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer token = new StringTokenizer(line, ";");
                String busStopCode = token.nextToken();
                String busStopName = token.nextToken();
                String busStopAddress = token.nextToken();
                String latitude = token.nextToken();
                latitude = latitude.replace(",", ".");
                arrayLatitude.add(Double.parseDouble(latitude));
                String longitude = token.nextToken();
                longitude = longitude.replace(",", ".");
                boolean isElementAdded = false;
                for (String s : propertiesFile) {
                    if (s.equals(busStopCode)) {
                        isElementAdded = true;
                    }
                }
                if (!isElementAdded) {
                    stops.add(new SearchItem(busStopCode, busStopName, busStopAddress, R.drawable.star_border, latitude, longitude));
                } else {
                    stops.add(new SearchItem(busStopCode, busStopName, busStopAddress, R.drawable.star, latitude, longitude));
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            Log.logFile(context, e);
        }
    }

    public List<SearchItem> stopsViewer(Context context, ArrayList<BusClass> busClass) {
        List<SearchItem> stops = new ArrayList<>();
        File file = takeFileCut(context);
        String[] propertiesFile = takeFavElement(context);
        if (FileUtils.sizeOf(file) == 0) {
            writeFile(context, file, busClass);
            extractFromFileCutted(context, file, propertiesFile, stops);
        } else {
            extractFromFileCutted(context, file, propertiesFile, stops);
        }
        return stops;
    }

    private void stopsCutLatitude(double searchValue) {
        arrayLatitude = cutTheCutFile(searchValue, arrayLatitude);
    }

    private List<Double> cutTheCutFile(double searchValue, @NotNull List<Double> cut) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < cut.size(); i++) {
            if (cut.get(i) < (searchValue + distance) && cut.get(i) > (searchValue - distance)) {
                result.add(cut.get(i));
            }
        }
        return result;
    }

    public List<String> takeTheCorrespondingBusStop(@NotNull ArrayList<BusClass> busClass, double searchValueLatitude, double searchValueLongitude) {
        stopsCutLatitude(searchValueLatitude);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < busClass.size(); i++) {
            for (int j = 0; j < arrayLatitude.size(); j++) {
                if (Double.parseDouble(busClass.get(i).getLatitude()) == arrayLatitude.get(j) && !result.contains(busClass.get(i).getBusStopCode())) {
                    if (Double.parseDouble(busClass.get(i).getLongitude()) < (searchValueLongitude + distance) && Double.parseDouble(busClass.get(i).getLongitude()) > (searchValueLongitude - distance)) {
                        result.add(busClass.get(i).getBusStopCode());
                    }
                }
            }

        }
        return result;
    }
}