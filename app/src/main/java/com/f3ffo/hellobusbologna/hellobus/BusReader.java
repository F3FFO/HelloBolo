package com.f3ffo.hellobusbologna.hellobus;

import android.content.Context;
import android.util.Log;

import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.model.SearchListViewItem;
import com.f3ffo.hellobusbologna.model.BusClass;

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
    private List<SearchListViewItem> stops = new ArrayList<>();
    private String busStopName;

    public String getBusStopName() {
        return busStopName;
    }

    public List<SearchListViewItem> getStops() {
        return stops;
    }

    public void extractFromFile(Context context) {
        File[] listFiles = context.getFilesDir().listFiles();
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

    public void stopsViewer(Context context) {
        ArrayList<String> stopsTemp = new ArrayList<>();
        File file = takeFile(context);
        Properties prop = new Properties();
        String[] propertiesFile = null;
        try {
            prop.load(context.openFileInput("favourites.properties"));
            propertiesFile = new String[prop.size()];
            for (int j = 0; j < prop.size(); j++) {
                propertiesFile[j] = prop.getProperty("busStopCode.Fav." + j).substring(0, prop.getProperty("busStopCode.Fav." + j).indexOf(","));
                System.out.println(propertiesFile[j]);
            }
        } catch (IOException e) {

        }
        if (FileUtils.sizeOf(file) == 0) {
            for (int i = 0; i < busClass.size(); i++) {
                String busStopCode = busClass.get(i).getBusStopCode();
                if (!stopsTemp.contains(busStopCode)) {
                    stopsTemp.add(busStopCode);
                    for (int j = 0; j < propertiesFile.length; j++) {
                        if (!propertiesFile[j].equals(busStopCode)) {
                            stops.add(new SearchListViewItem(busStopCode, busClass.get(i).getBusStopName(), busClass.get(i).getBusStopAddress(), R.drawable.round_favourite_border));
                        } else {
                            stops.add(new SearchListViewItem(busStopCode, busClass.get(i).getBusStopName(), busClass.get(i).getBusStopAddress(), R.drawable.ic_star));
                        }
                    }
                    try {
                        FileUtils.writeStringToFile(new File(context.getFilesDir(), file.getName()), (busStopCode + "," + busClass.get(i).getBusStopName() + "," + busClass.get(i).getBusStopAddress() + "\n"), StandardCharsets.UTF_8, true);
                    } catch (IOException e) {
                        Log.e("ERROR stopsViewer", e.getMessage());
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
                    String busStopAddress = StringUtils.lowerCase(token.nextToken());
                    boolean isElementAdded = false;
                    if (propertiesFile.length != 0) {
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
                    } else {
                        stops.add(new SearchListViewItem(busStopCode, busStopName, busStopAddress, R.drawable.round_favourite_border));
                    }
                }
            } catch (IOException e) {
                Log.e("ERROR 2nd stopsViewer", e.getMessage());
            }
        }
    }

    public boolean refreshElement(int position) {
        if (stops.get(position).getImageFavourite() == R.drawable.round_favourite_border) {
            stops.get(position).setImageFavourite(R.drawable.ic_star);
            return true;
        } else {
            stops.get(position).setImageFavourite(R.drawable.round_favourite_border);
            return false;
        }
    }
}
