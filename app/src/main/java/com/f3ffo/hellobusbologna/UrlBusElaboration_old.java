package com.f3ffo.hellobusbologna;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.StringTokenizer;

public class UrlBusElaboration_old {

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


    private String[] httpExtract() throws IOException {
        String ris[] = null;
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
                    ris = new String[1];
                    ris[0] = "Fermata Non Gestita";

                } else if (line.contains("NESSUNA ALTRA CORSA")) {
                    ris = new String[1];
                    ris[0] = "Linea Assente Ora";

                } else if (line.equals("NULL")) {
                    ris = new String[1];
                    ris[0] = "Mancano Dei Dati";
                    //------------------------------OutPutGestion-----------------------------------
                } else {
                    line = line.substring((line.indexOf(":") + 2));
                    StringTokenizer token = new StringTokenizer(line, ",");
                    ris = new String[token.countTokens()];
                    int i = 0;
                    while (token.hasMoreTokens()) {
                        String util = token.nextToken();
                        if (util.startsWith(" ")) {
                            if (util.contains("CON PEDANA)")) {
                                ris[i] = util.substring(1, util.lastIndexOf("(")) + "CON PEDANA";
                                i++;
                            } else {
                                ris[i] = util.substring(1) + " SENZA PEDANA";
                                i++;
                            }

                        } else {
                            if (util.contains("CON PEDANA)")) {
                                ris[i] = util.substring(0, util.lastIndexOf("(")) + "CON PEDANA";
                                i++;
                            } else {
                                ris[i] = util + " SENZA PEDANA";
                                i++;
                            }
                        }
                    }
                }
            }
        }
        br.close();
        reset();
        return ris;
    }

    public void httpExtractOnlyStop(String stop) throws IOException {
        this.stop = stop;
        httpExtract();
    }

    public void httpExtractStopAndHour(String stop, String hour) throws IOException {
        this.stop = stop;
        this.hour = hour;
        httpExtract();
    }

    public void httpExtractStopAndBusLine(String stop, String busline) throws IOException {
        this.stop = stop;
        this.busline = busline;
        httpExtract();
    }

    private void reset() {
        this.hour = "";
        this.stop = "";
        this.busline = "";

    }

}
