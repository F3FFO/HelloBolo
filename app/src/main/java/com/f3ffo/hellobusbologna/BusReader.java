package com.f3ffo.hellobusbologna;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BusReader {
    public ArrayList<BusClass> stop = new ArrayList<>();

    public void extractFromFile(InputStream file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8), 16384)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("codice_linea")) {
                    StringTokenizer token = new StringTokenizer(line, ";");
                    String busCode = token.nextToken();
                    String stopCode = token.nextToken();
                    String stopName = token.nextToken();
                    String stopAddress = token.nextToken();
                    stop.add(new BusClass(busCode, stopCode, stopName, stopAddress));
                }
            }
            file.close();
        } catch (IOException e) {
            Log.e("ERROR: ", e.getMessage());
            stop.clear();
        }
    }

    public ArrayList<String> busViewer(String stopCodeIn) {
        ArrayList<String> result = new ArrayList<>();
        result.add(0, "Tutti gli autobus");
        for (int i = 1; i < stop.size(); i++) {
            if (stop.get(i).getbusCode().equals(stopCodeIn)) {
                result.add(stop.get(i).getbusCode());
            }
        }
        return result;
    }

    public ArrayList<String> stopsViewer() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < stop.size(); i++) {
            String element = stop.get(i).getStopCode() + " - " + stop.get(i).getStopName() + "\n" + stop.get(i).getStopAddress();
            if (!result.contains(element)) {
                result.add(element);
            }
        }
        return result;
    }
}
