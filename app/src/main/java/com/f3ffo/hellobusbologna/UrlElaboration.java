package com.f3ffo.hellobusbologna;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class UrlElaboration extends AsyncTask<Void, Void, ArrayList<String>> {

    private String hour = "";
    private String stop = "";
    private String busline = "";

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getBusline() {
        return busline;
    }

    public void setBusline(String busline) {
        this.busline = busline;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        ArrayList<String> array = new ArrayList<>();
        try {
            URL url = new URL("https://hellobuswsweb.tper.it/web-services/hello-bus.asmx/QueryHellobus?fermata=" + stop + "&oraHHMM=" + hour + "&linea=" + busline);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            InputStreamReader ISR = (InputStreamReader) new InputStreamReader(huc.getInputStream(), StandardCharsets.UTF_8);
            Scanner br = new Scanner(ISR);
            String line;
            while (br.hasNext()) {
                line = br.nextLine();
                if (!line.startsWith("<?xml")) {
                    line = line.substring(line.lastIndexOf("asmx\">") + 6, line.lastIndexOf("<"));
                    //------------------------------ErrorGestion-----------------------------------
                    if (line.startsWith("HellobusHelp")) {
                        array.add("Fermata Non Gestita");

                    } else if (line.contains("NESSUNA ALTRA CORSA")) {
                        array.add("Linea Assente Ora");

                    } else if (line.equals("NULL")) {
                        array.add("Mancano Dei Dati");
                        //------------------------------OutPutGestion-----------------------------------
                    } else {
                        line = line.substring((line.indexOf(":") + 2));
                        StringTokenizer token = new StringTokenizer(line, ",");
                        //int i = 0;
                        while (token.hasMoreTokens()) {
                            String util = token.nextToken();
                            if (util.startsWith(" ")) {
                                if (util.contains("CON PEDANA)")) {
                                    array.add(util.substring(1, util.lastIndexOf("(")) + "CON PEDANA");
                                    //i++;
                                } else {
                                    array.add(util.substring(1) + " SENZA PEDANA");
                                    //i++;
                                }

                            } else {
                                if (util.contains("CON PEDANA)")) {
                                    array.add(util.substring(0, util.lastIndexOf("(")) + "CON PEDANA");
                                    //i++;
                                } else {
                                    array.add(util + " SENZA PEDANA");
                                    //i++;
                                }
                            }
                        }
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            Log.e("ERROR: ", e.getMessage());
        }
        return array;
    }
}
