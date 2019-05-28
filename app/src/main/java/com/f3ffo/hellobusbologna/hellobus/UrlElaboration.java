package com.f3ffo.hellobusbologna.hellobus;

import android.os.AsyncTask;
import android.util.Log;

import com.f3ffo.hellobusbologna.asyncInterface.AsyncResponse;
import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.items.OutputCardViewItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import okhttp3.*;

public class UrlElaboration extends AsyncTask<Void, Void, List<OutputCardViewItem>> {
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
        this.delegate.processStart();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        this.delegate.progressUpdate();
    }

    @Override
    protected List<OutputCardViewItem> doInBackground(Void... params) {
        List<OutputCardViewItem> outputCardViewItemList = new ArrayList<>();
        try {
            Request get = new Request.Builder().url("https://hellobuswsweb.tper.it/web-services/hello-bus.asmx/QueryHellobus?fermata=" + busStop + "&oraHHMM=" + busHour + "&linea=" + busLine).build();
            //HttpURLConnection huc = (HttpURLConnection) new URL("https://hellobuswsweb.tper.it/web-services/hello-bus.asmx/QueryHellobus?fermata=" + busStop + "&oraHHMM=" + busHour + "&linea=" + busLine).openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(new OkHttpClient().newCall(get).execute().body()).byteStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("<?xml")) {
                    line = line.substring(line.lastIndexOf("asmx\">") + 6, line.lastIndexOf("<"));
                    //------------------------------Manage error-----------------------------------
                    if (line.contains("FERMATA " + busStop + " NON GESTITA")) {
                        outputCardViewItemList.add(new OutputCardViewItem(R.drawable.ic_error, "FERMATA " + busStop + " NON GESTITA"));
                    } else if (line.contains("LINEA " + busLine + " NON GESTITA")) {
                        outputCardViewItemList.add(new OutputCardViewItem(R.drawable.ic_error, "LINEA " + busLine + " NON GESTITA"));
                    } else if (line.contains("NESSUNA ALTRA CORSA")) {
                        outputCardViewItemList.add(new OutputCardViewItem(R.drawable.ic_error, "LINEA " + busLine + " ASSENTE"));
                    } else if (line.equals("NULL")) {
                        outputCardViewItemList.add(new OutputCardViewItem(R.drawable.ic_error, "ERRORE"));
                    } else if (line.contains("TEMPORANEAMENTE SOSPESE")) {
                        outputCardViewItemList.add(new OutputCardViewItem(R.drawable.ic_error, "ERRORE"));
                        //------------------------------Manage output-----------------------------------
                    } else {
                        outputCardViewItemList.add(new OutputCardViewItem(0, ""));
                        line = line.substring(line.indexOf(":") + 2);
                        if (line.startsWith("(")) {
                            line = line.substring(line.indexOf("(") + 9);
                            StringTokenizer token = new StringTokenizer(line, ",");
                            while (token.hasMoreTokens()) {
                                String util = token.nextToken();
                                int isHandicap = 0;
                                if (util.contains("CON PEDANA")) {
                                    isHandicap = R.drawable.ic_handicap_green;
                                } else if (util.contains("SENZA PEDANA")) {
                                    isHandicap = R.drawable.ic_handicap_red;
                                }
                                StringTokenizer token2 = new StringTokenizer(util, " ");
                                String busNumber = token2.nextToken();
                                int isSatellite = R.drawable.ic_output_time;
                                if (token2.nextToken().equals("DaSatellite")) {
                                    isSatellite = R.drawable.ic_output_satellite;
                                }
                                String busHour = token2.nextToken();
                                outputCardViewItemList.add(new OutputCardViewItem(busNumber, busHour, busHour, isSatellite, isHandicap));
                            }
                        } else {
                            StringTokenizer token = new StringTokenizer(line, ",");
                            while (token.hasMoreTokens()) {
                                String util = token.nextToken();
                                int isHandicap = 0;
                                if (util.contains("CON PEDANA")) {
                                    isHandicap = R.drawable.ic_handicap_green;
                                } else if (util.contains("SENZA PEDANA")) {
                                    isHandicap = R.drawable.ic_handicap_red;
                                }
                                StringTokenizer token2 = new StringTokenizer(util, " ");
                                String busNumber = token2.nextToken();
                                int isSatellite = R.drawable.ic_output_time;
                                if (token2.nextToken().equals("DaSatellite")) {
                                    isSatellite = R.drawable.ic_output_satellite;
                                }
                                String busHour = token2.nextToken();
                                outputCardViewItemList.add(new OutputCardViewItem(busNumber, busHour, busHour, isSatellite, isHandicap));
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
