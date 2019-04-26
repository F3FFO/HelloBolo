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
    private ArrayList<String> bus = new ArrayList<>();
    private String stopName;
    private String stopCode;

    /**
     * Inserts part of the input file into ArrayList bus
     *
     * @param file       File to read
     * @param stopCodeIn Code of the bus stop taken from the input
     */
    public ArrayList<String> extractFromFileFromCode(InputStream file, String stopCodeIn) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8), 16384)) {
            String line;
            bus.add(0, "Tutti gli autobus");
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("codice_linea")) {
                    StringTokenizer token = new StringTokenizer(line, ";");
                    if (token.countTokens() == 10) {
                        String busCode = token.nextToken();
                        String stopCode = token.nextToken();
                        String stopName = token.nextToken();
                        /*for (int i = 0; i < 7; i++) {
                            token.nextToken();
                        }
                        String zoneCode = token.nextToken();*/
                        if (stopCode.equals(stopCodeIn)) {
                            bus.add(busCode);
                            this.stopName = stopName;
                        }
                    } else if (token.countTokens() == 9) {
                        String busCode = token.nextToken();
                        String stopCode = token.nextToken();
                        String stopName = token.nextToken();
                        /*for (int i = 0; i < 6; i++) {
                            token.nextToken();
                        }
                        String zoneCode = token.nextToken();*/
                        if (stopCode.equals(stopCodeIn)) {
                            bus.add(busCode);
                            this.stopName = stopName;
                        } else if (stopName.equals(stopCodeIn)) {
                            bus.add(busCode);
                            this.stopCode = stopCode;
                        }
                    }
                }
            }
            file.close();
        } catch (IOException e) {
            Log.e("ERROR: ", e.getMessage());
            bus.clear();
        }
        return bus;
    }

    public ArrayList<String> extractFromFileFromName(InputStream file, String stopNameIn) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8), 16384)) {
            String line;
            bus.add(0, "Tutti gli autobus");
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("codice_linea")) {
                    StringTokenizer token = new StringTokenizer(line, ";");
                    if (token.countTokens() == 10) {
                        String busCode = token.nextToken();
                        String stopCode = token.nextToken();
                        String stopName = token.nextToken();
                        /*for (int i = 0; i < 7; i++) {
                            token.nextToken();
                        }
                        String zoneCode = token.nextToken();*/
                        if (stopName.equals(stopNameIn)) {
                            bus.add(busCode);
                            this.stopCode = stopCode;
                        }
                    } else if (token.countTokens() == 9) {
                        String busCode = token.nextToken();
                        String stopCode = token.nextToken();
                        String stopName = token.nextToken();
                        /*for (int i = 0; i < 6; i++) {
                            token.nextToken();
                        }
                        String zoneCode = token.nextToken();*/
                        if (stopName.equals(stopNameIn)) {
                            bus.add(busCode);
                            this.stopCode = stopCode;
                        }
                    }
                }
            }
            file.close();
        } catch (IOException e) {
            Log.e("ERROR: ", e.getMessage());
            bus.clear();
        }
        return bus;
    }

    public String stopNameViewer() {
        return this.stopName;
    }

    public String stopCodeViewer() {
        return this.stopCode;
    }
}
