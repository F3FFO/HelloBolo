package com.f3ffo.hellobusbologna.hellobus;

import android.content.Context;
import android.util.Log;

import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.items.SearchListViewItem;
import com.f3ffo.hellobusbologna.model.BusClass;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
            if (!listFile.isDirectory() && !listFile.getName().equals("favourites.properties")) {
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
                    Log.e("ERROR extractFromFile: ", e.getMessage());
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

    public void stopsViewer() {
        ArrayList<String> stopsTemp = new ArrayList<>();
        for (int i = 0; i < busClass.size(); i++) {
            String element = busClass.get(i).getBusStopCode();
            if (!stopsTemp.contains(element)) {
                stopsTemp.add(element);
                stops.add(new SearchListViewItem(R.drawable.round_search, element, busClass.get(i).getBusStopName(), busClass.get(i).getBusStopAddress(), R.drawable.round_favourite_border));
            }
        }
        stopsTemp.clear();
    }
}
