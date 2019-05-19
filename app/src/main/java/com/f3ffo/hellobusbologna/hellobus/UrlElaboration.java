package com.f3ffo.hellobusbologna.hellobus;

import android.os.AsyncTask;
import android.util.Log;

import com.f3ffo.hellobusbologna.AsyncResponse;
import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.items.OutputCardViewItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class UrlElaboration extends AsyncTask<Void, Integer, List<OutputCardViewItem>> {
    private String busStop = "";
    private String busLine = "";
    private String busHour = "";
    private AsyncResponse delegate = null;

    public void setBusStop(String busStop) {
        this.busStop = busStop;
    }

    public void setBusLine(String busLine) {
        this.busLine = busLine;
    }

    public void setBusHour(String busHour) {
        this.busHour = busHour;
    }

    public void setDelegate(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        delegate.processStart();
    }

    @Override
    protected List<OutputCardViewItem> doInBackground(Void... params) {
        List<OutputCardViewItem> outputCardViewItemList = new ArrayList<>();
        try {
            HttpURLConnection huc = (HttpURLConnection) new URL("https://hellobuswsweb.tper.it/web-services/hello-bus.asmx/QueryHellobus?fermata=" + busStop + "&oraHHMM=" + busHour + "&linea=" + busLine).openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("<?xml")) {
                    line = line.substring(line.lastIndexOf("asmx\">") + 6, line.lastIndexOf("<"));
                    //------------------------------Manage error-----------------------------------
                    if (line.startsWith("HellobusHelp")) {
                        //array.add("Fermata non gestita");
                    } else if (line.contains("NESSUNA ALTRA CORSA")) {
                        //array.add("Linea assente");
                    } else if (line.equals("NULL")) {
                        //array.add("Mancano dei dati");
                        //------------------------------Manage output-----------------------------------
                    } else {
                        line = line.substring(line.indexOf(":") + 2);
                        if (line.startsWith("(")) {
                            line = line.substring(line.indexOf("(") + 9);
                            StringTokenizer token = new StringTokenizer(line, ",");
                            while (token.hasMoreTokens()) {
                                String util = token.nextToken();
                                StringTokenizer token2 = new StringTokenizer(util, " ");
                                String busNumber = token2.nextToken();
                                int isSatellite = R.drawable.ic_output_time;
                                if (token2.nextToken().equals("DaSatellite")) {
                                    isSatellite = R.drawable.ic_output_satellite;
                                }
                                String busHour = token2.nextToken();
                                outputCardViewItemList.add(new OutputCardViewItem(busNumber, busHour, busHour, isSatellite));
                            }
                        } else {
                            StringTokenizer token = new StringTokenizer(line, ",");
                            while (token.hasMoreTokens()) {
                                String util = token.nextToken();
                                StringTokenizer token2 = new StringTokenizer(util, " ");
                                String busNumber = token2.nextToken();
                                int isSatellite = R.drawable.ic_output_time;
                                if (token2.nextToken().equals("DaSatellite")) {
                                    isSatellite = R.drawable.ic_output_satellite;
                                }
                                String busHour = token2.nextToken();
                                outputCardViewItemList.add(new OutputCardViewItem(busNumber, busHour, busHour, isSatellite));
                            }
                        }
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            Log.e("ERROR urlElaboration: ", e.getMessage());
        }
        return outputCardViewItemList;
    }

    @Override
    protected void onPostExecute(List<OutputCardViewItem> result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}
