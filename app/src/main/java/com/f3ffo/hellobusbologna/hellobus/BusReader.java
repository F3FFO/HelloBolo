package com.f3ffo.hellobusbologna.hellobus;

import android.content.Context;
import android.util.Log;

import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.search.SearchListViewItem;

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

    private ArrayList<BusClass> busClass = new ArrayList<>();
    private String busStopName;

    public String getBusStopName() {
        return busStopName;
    }

    public void extractFromFile(Context context) {
        File[] listFiles = context.getFilesDir().listFiles();
        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (!listFile.isDirectory() && !listFile.getName().equals("favourites.properties") && !listFile.getName().contains("cut_")) {
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
                                busClass.add(new BusClass(busCode, stopCode, stopName, StringUtils.capitalize(stopAddress)));
                            }
                        }
                        br.close();
                    } catch (IOException e) {
                        Log.e("ERROR extractFromFile", e.getMessage());
                        busClass.clear();
                    }
                }
            }
        }
    }

    public ArrayList<String> busViewer(String busStopCodeIn) {
        ArrayList<String> bus = new ArrayList<>();
        bus.add(0, "Tutti gli autobus");
        for (int i = 0; i < busClass.size(); i++) {
            if (busClass.get(i).getBusStopCode().equals(busStopCodeIn)) {
                bus.add(busClass.get(i).getbusCode());
                this.busStopName = busClass.get(i).getBusStopName();
            }
        }
        return bus;
    }

    private File takeFile(Context context) {
        File[] listFiles = context.getFilesDir().listFiles();
        File file = null;
        for (File listFile : listFiles) {
            if (listFile.getName().contains("cut_")) {
                file = listFile;
            }
        }
        return file;
    }

    public List<SearchListViewItem> stopsViewer(Context context) {
        List<SearchListViewItem> stops = new ArrayList<>();
        File file = takeFile(context);
        Properties prop = new Properties();
        String[] propertiesFile = new String[10];
        try {
            prop.load(context.openFileInput("favourites.properties"));
            for (int i = 0; i < propertiesFile.length; i++) {
                if (!prop.getProperty("busStopCode.Fav." + i).equals("")) {
                    propertiesFile[i] = prop.getProperty("busStopCode.Fav." + i).substring(0, prop.getProperty("busStopCode.Fav." + i).indexOf(","));
                } else {
                    propertiesFile[i] = "";
                }
            }
        } catch (IOException e) {
            Log.e("ERROR stopsViewer01", e.getMessage());
            e.printStackTrace();
        }
        ArrayList<String> stopsTemp = new ArrayList<>();
        if (FileUtils.sizeOf(file) == 0) {
            for (int i = 0; i < busClass.size(); i++) {
                String busStopCode = busClass.get(i).getBusStopCode();
                if (!stopsTemp.contains(busStopCode)) {
                    stopsTemp.add(busStopCode);
                    for (String s : propertiesFile) {
                        if (!s.equals(busStopCode)) {
                            stops.add(new SearchListViewItem(busStopCode, busClass.get(i).getBusStopName(), busClass.get(i).getBusStopAddress(), R.drawable.round_favourite_border));
                        } else {
                            stops.add(new SearchListViewItem(busStopCode, busClass.get(i).getBusStopName(), busClass.get(i).getBusStopAddress(), R.drawable.ic_star));
                        }
                    }
                    try {
                        FileUtils.writeStringToFile(new File(context.getFilesDir(), file.getName()), (busStopCode + "," + busClass.get(i).getBusStopName() + "," + busClass.get(i).getBusStopAddress() + "\n"), StandardCharsets.UTF_8, true);
                    } catch (IOException e) {
                        Log.e("ERROR stopsViewer02", e.getMessage());
                    }
                }
            }
            stopsTemp.clear();
        } else {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput(file.getName()), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    StringTokenizer token = new StringTokenizer(line, ",");
                    String busStopCode = token.nextToken();
                    String busStopName = token.nextToken();
                    String busStopAddress = token.nextToken();
                    boolean isElementAdded = false;
                    for (String s : propertiesFile) {
                        if (s.equals(busStopCode)) {
                            isElementAdded = true;
                        }
                    }
                    if (!isElementAdded) {
                        stops.add(new SearchListViewItem(busStopCode, busStopName, busStopAddress, R.drawable.round_favourite_border));
                    } else {
                        stops.add(new SearchListViewItem(busStopCode, busStopName, busStopAddress, R.drawable.ic_star));
                    }
                }
            } catch (IOException e) {
                Log.e("ERROR stopsViewer03", e.getMessage());
            }
        }
        return stops;
    }
}
