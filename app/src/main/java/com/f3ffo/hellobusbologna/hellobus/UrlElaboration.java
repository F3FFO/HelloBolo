package com.f3ffo.hellobusbologna.hellobus;

import android.os.AsyncTask;
import android.util.Log;

import com.f3ffo.hellobusbologna.asyncInterface.AsyncResponse;
import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.output.OutputItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import okhttp3.*;

public class UrlElaboration extends AsyncTask<Void, Void, List<OutputItem>> {
    private String busStop;
    private String busLine;
    private String busHour;
    private AsyncResponse delegate;

    public UrlElaboration(String busStop, String busLine, String busHour, AsyncResponse delegate) {
        this.busStop = busStop;
        this.busLine = busLine;
        this.busHour = busHour;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.delegate.processStart();
    }

    @Override
    protected List<OutputItem> doInBackground(Void... params) {
        List<OutputItem> outputItemList = new ArrayList<>();
        try {
            Request get = new Request.Builder().url("https://hellobuswsweb.tper.it/web-services/hello-bus.asmx/QueryHellobus?fermata=" + busStop + "&oraHHMM=" + busHour + "&linea=" + busLine).build();
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(new OkHttpClient().newCall(get).execute().body()).byteStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("<?xml")) {
                    line = line.substring(line.lastIndexOf("asmx\">") + 6, line.lastIndexOf("<"));
                    if (line.contains("NESSUNA ALTRA CORSA")) {
                        if (busLine.isEmpty()) {
                            outputItemList.add(new OutputItem("NESSUNA LINEA PRESENTE"));
                        } else {
                            outputItemList.add(new OutputItem("LINEA " + busLine + " ASSENTE"));
                        }
                    } else if (line.contains("LINEA " + busLine + " NON GESTITA")) {
                        outputItemList.add(new OutputItem("LINEA " + busLine + " NON GESTITA"));
                    } else if (line.contains("TEMPORANEAMENTE SOSPESE")) {
                        outputItemList.add(new OutputItem("ERRORE"));
                    } else if (line.equals("NULL")) {
                        outputItemList.add(new OutputItem("ERRORE"));
                    } else if (line.contains("FERMATA " + busStop + " NON GESTITA")) {
                        outputItemList.add(new OutputItem("FERMATA " + busStop + " NON GESTITA"));
                    } else {
                        outputItemList.add(new OutputItem(""));
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
                                int isSatellite = R.drawable.round_time;
                                if (token2.nextToken().equals("DaSatellite")) {
                                    isSatellite = R.drawable.ic_output_satellite;
                                }
                                String busHour = token2.nextToken();
                                outputItemList.add(new OutputItem(busNumber, busHour, busHour, isSatellite, isHandicap));
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
                                int isSatellite = R.drawable.round_time;
                                if (token2.nextToken().equals("DaSatellite")) {
                                    isSatellite = R.drawable.ic_output_satellite;
                                }
                                String busHour = token2.nextToken();
                                outputItemList.add(new OutputItem(busNumber, busHour, busHour, isSatellite, isHandicap));
                            }
                        }
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            Log.e("ERROR urlElaboration: ", e.getMessage());
        }
        return outputItemList;
    }

    @Override
    protected void onPostExecute(List<OutputItem> result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}
