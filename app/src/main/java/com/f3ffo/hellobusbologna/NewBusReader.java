package com.f3ffo.hellobusbologna;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class NewBusReader {
    private ArrayList<BusClass> bus = new ArrayList<>();

    /**
     * Inserts part of the input file into ArrayList bus
     *
     * @param file File to read
     */
    public void fileToArrayList(InputStream file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8), 8192)) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("codice_linea")) {
                    StringTokenizer token = new StringTokenizer(line, ";");
                    if (token.countTokens() == 10) {
                        String busCode = token.nextToken();
                        String stopCode = token.nextToken();
                        String stopName = token.nextToken();
                        /*for (int i = 0; i < 6; i++) {
                            token.nextToken();
                        }
                        String zoneCode = token.nextToken();*/
                        bus.add(new BusClass(busCode, stopCode, stopName));
                    } else if (token.countTokens() == 9) {
                        String busCode = token.nextToken();
                        String stopCode = token.nextToken();
                        String stopName = token.nextToken();
                        /*for (int i = 0; i < 5; i++) {
                            token.nextToken();
                        }
                        String zoneCode = token.nextToken();*/
                        bus.add(new BusClass(busCode, stopCode, stopName));
                    }
                }
            }
            file.close();
        } catch (IOException e) {
            Log.e("ERROR: ", e.getMessage());
            bus.clear();
        }
    }

    /**
     * Compare the String take from parameter with a String inside ArrayList bus
     *
     * @param stopCode Code of the bus stop
     * @return ArrayList of all buses passing on the bus stop
     */
    public ArrayList<String> stopCodeToView(String stopCode) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < bus.size(); i++) {
            if (bus.get(i).getStopCode().equals(stopCode)) {
                result.add(bus.get(i).getLineCode());
            }
        }
        return result;
    }
}
