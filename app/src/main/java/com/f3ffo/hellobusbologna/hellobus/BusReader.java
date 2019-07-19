package com.f3ffo.hellobusbologna.hellobus;

import android.content.Context;

import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.search.SearchItem;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

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

    public ArrayList<BusClass> extractFromFile(Context context) {
        ArrayList<BusClass> busClass = new ArrayList<>();
        File[] listFiles = context.getFilesDir().listFiles();
        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (listFile.getName().contains("lineefermate_") && listFile.getName().contains(".csv")) {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput(listFile.getName()), StandardCharsets.UTF_8));
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (!line.startsWith("codice_linea")) {
                                StringTokenizer token = new StringTokenizer(line, ";");
                                String busCode = token.nextToken();
                                String stopCode = token.nextToken();
                                String stopName = token.nextToken();
                                String stopAddress = StringUtils.lowerCase(token.nextToken());
                                token.nextToken();
                                token.nextToken();
                                token.nextToken();
                                String latitude = token.nextToken();
                                String longitude = token.nextToken();
                                busClass.add(new BusClass(busCode, stopCode, stopName, StringUtils.capitalize(stopAddress), latitude, longitude));
                            }
                        }
                        br.close();
                    } catch (IOException e) {
                        busClass.clear();
                    }
                }
            }
        }
        return busClass;
    }

    private File takeFileCut(Context context) {
        File[] listFiles = context.getFilesDir().listFiles();
        File file = null;
        for (File listFile : listFiles) {
            if (listFile.getName().contains("cut_") && listFile.getName().contains(".csv")) {
                file = listFile;
            }
        }
        return file;
    }

    private String[] takeFavElement(Context context) {
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
            e.printStackTrace();
        }
        return propertiesFile;
    }

    private void writeFile(Context context, File file, ArrayList<BusClass> busClass) {
        ArrayList<String> stopsTemp = new ArrayList<>();
        String busStopCode;
        for (int i = 0; i < busClass.size(); i++) {
            busStopCode = busClass.get(i).getBusStopCode();
            if (!stopsTemp.contains(busStopCode)) {
                stopsTemp.add(busStopCode);
                try {
                    FileUtils.writeStringToFile(new File(context.getFilesDir(), file.getName()), (busStopCode + ";" + busClass.get(i).getBusStopName() + ";" + busClass.get(i).getBusStopAddress() + ";" + busClass.get(i).getLatitude() + ";" + busClass.get(i).getLongitude() + "\n"), StandardCharsets.UTF_8, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        stopsTemp.clear();
    }

    private void extractFromFileCutted(Context context, File file, String[] propertiesFile, List<SearchItem> stops) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(file.getName()), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer token = new StringTokenizer(line, ";");
                String busStopCode = token.nextToken();
                String busStopName = token.nextToken();
                String busStopAddress = token.nextToken();
                String latitude = token.nextToken();
                String longitude = token.nextToken();
                boolean isElementAdded = false;
                for (String s : propertiesFile) {
                    if (s.equals(busStopCode)) {
                        isElementAdded = true;
                    }
                }
                if (!isElementAdded) {
                    stops.add(new SearchItem(busStopCode, busStopName, busStopAddress, R.drawable.round_favourite_border, latitude, longitude));
                } else {
                    stops.add(new SearchItem(busStopCode, busStopName, busStopAddress, R.drawable.ic_star, latitude, longitude));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
}
