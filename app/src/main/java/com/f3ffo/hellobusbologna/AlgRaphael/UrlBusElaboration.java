package com.f3ffo.hellobusbologna.AlgRaphael;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.StringTokenizer;

public class UrlBusElaboration {

    private String hour = "";
    private String stop = "";
    private String busline = "";

    private void httpExtract() throws MalformedURLException, IOException {
        String s = "https://hellobuswsweb.tper.it/web-services/hello-bus.asmx/QueryHellobus?fermata=" + stop + "&oraHHMM=" + hour + "&linea=" + busline;

        URL u = new URL(s);
        HttpURLConnection huc = (HttpURLConnection) u.openConnection();
        Scanner br = new Scanner(new InputStreamReader(huc.getInputStream()));
        String line;
        while (br.hasNext()) {
            line = br.nextLine();
            if (!line.startsWith("<?xml")) {
                line = line.substring(line.lastIndexOf("asmx\">") + 6, line.lastIndexOf("<"));
                //------------------------------ErrorGestion-----------------------------------
                if (line.startsWith("HellobusHelp")) {
                    System.out.println("Fermata Non Gestita");

                } else if (line.contains("NESSUNA ALTRA CORSA")) {
                    System.out.println("Linea Assente Ora");

                } else if (line.equals("NULL")) {
                    System.out.println("Mancano Dei Dati");
                    //------------------------------OutPutGestion-----------------------------------                
                } else {
                    line = line.substring((line.indexOf(":") + 2));
                    StringTokenizer token = new StringTokenizer(line, ",");
                    while (token.hasMoreTokens()) {
                        String util = token.nextToken();
                        if (util.startsWith(" ")) {
                            if (util.contains("CON PEDANA)")) {
                                System.out.println((util.substring(1, util.lastIndexOf("(")) + "CON PEDANA"));
                            } else {
                                System.out.println(util.substring(1) + " SENZA PEDANA");
                            }

                        } else {
                            if (util.contains("CON PEDANA)")) {
                                System.out.println((util.substring(0, util.lastIndexOf("(")) + "CON PEDANA"));
                            } else {
                                System.out.println(util + " SENZA PEDANA");
                            }
                        }
                    }
                }
            }
        }
    }

    public void httpExtractOnlyStop(String stop) throws IOException {
        this.stop = stop;
        httpExtract();
        reset();
    }

    public void httpExtractStopAndHour(String stop, String hour) throws IOException {
        this.stop = stop;
        this.hour = hour;
        httpExtract();
        reset();
    }

    public void httpExtractStopAndBusLine(String stop, String busline) throws IOException {
        this.stop = stop;
        this.busline = busline;
        httpExtract();
        reset();
    }

    private void reset() {
        this.hour = "";
        this.stop = "";
        this.busline = "";

    }
}
