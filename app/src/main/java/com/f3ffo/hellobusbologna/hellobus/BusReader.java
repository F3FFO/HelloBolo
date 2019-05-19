package com.f3ffo.hellobusbologna.hellobus;

import android.util.Log;

import com.f3ffo.hellobusbologna.items.SearchListViewItem;
import com.f3ffo.hellobusbologna.model.BusClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BusReader {
    public ArrayList<BusClass> busClass = new ArrayList<>();
    public ArrayList<SearchListViewItem> stops = new ArrayList<>();
    private String stopName;

    public String getStopName() {
        return stopName;
    }

    public void extractFromFile(InputStream file) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8), 16384);
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("codice_linea")) {
                    StringTokenizer token = new StringTokenizer(line, ";");
                    String busCode = token.nextToken();
                    String stopCode = token.nextToken();
                    String stopName = token.nextToken();
                    String stopAddress = token.nextToken();
                    busClass.add(new BusClass(busCode, stopCode, stopName, stopAddress));
                }
            }
            br.close();
            file.close();
        } catch (IOException e) {
            Log.e("ERROR extractFromFile: ", e.getMessage());
            busClass.clear();
        }
    }

    public ArrayList<String> busViewer(String stopCodeIn) {
        ArrayList<String> bus = new ArrayList<>();
        bus.add(0, "Tutti gli autobus");
        for (int i = 0; i < busClass.size(); i++) {
            if (busClass.get(i).getStopCode().equals(stopCodeIn)) {
                bus.add(busClass.get(i).getbusCode());
                this.stopName = busClass.get(i).getStopName();
            }
        }
        return bus;
    }

    public void stopsViewer() {
        for (int i = 0; i < busClass.size(); i++) {
            if (!stops.get(i).getBusStopCode().equals(busClass.get(i).getStopCode())) {
                stops.add(new SearchListViewItem(busClass.get(i).getStopCode(), busClass.get(i).getStopName(), busClass.get(i).getStopAddress()));
            }
        }
    }
}
