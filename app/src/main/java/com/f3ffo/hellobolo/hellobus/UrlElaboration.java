package com.f3ffo.hellobolo.hellobus;

import android.content.Context;
import android.os.AsyncTask;

import com.f3ffo.hellobolo.R;
import com.f3ffo.hellobolo.asyncInterface.AsyncResponseUrl;
import com.f3ffo.hellobolo.output.OutputItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UrlElaboration extends AsyncTask<Void, Void, List<OutputItem>> {
    private String busStop;
    private String busLine;
    private String busHour;
    private AsyncResponseUrl delegate;
    private Context context;

    public UrlElaboration(String busStop, String busLine, String busHour, AsyncResponseUrl delegate, Context context) {
        this.busStop = busStop;
        this.busLine = busLine;
        this.busHour = busHour;
        this.delegate = delegate;
        this.context = context;
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(new OkHttpClient().newCall(get).execute().body()).byteStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("<?xml")) {
                    line = line.substring(line.lastIndexOf("asmx\">") + 6, line.lastIndexOf("<"));
                    if (line.contains("NESSUNA ALTRA CORSA")) {
                        if (busLine.isEmpty()) {
                            outputItemList.add(new OutputItem(context.getString(R.string.output_no_lines)));
                        } else {
                            outputItemList.add(new OutputItem(context.getString(R.string.output_line, busLine)));
                        }
                    } else if (line.contains("LINEA " + busLine + " NON GESTITA")) {
                        outputItemList.add(new OutputItem(context.getString(R.string.output_no_line_managed, busLine)));
                    } else if (line.contains("TEMPORANEAMENTE SOSPESE")) {
                        outputItemList.add(new OutputItem(context.getString(R.string.output_error)));
                    } else if (line.equals("NULL")) {
                        outputItemList.add(new OutputItem(context.getString(R.string.output_error)));
                    } else if (line.contains("FERMATA " + busStop + " NON GESTITA")) {
                        outputItemList.add(new OutputItem(context.getString(R.string.output_no_bus_stop_managed, busStop)));
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
                                    isHandicap = R.drawable.output_handicap_green;
                                } else if (util.contains("SENZA PEDANA")) {
                                    isHandicap = R.drawable.output_handicap_red;
                                }
                                StringTokenizer token2 = new StringTokenizer(util, " ");
                                String busNumber = token2.nextToken();
                                int isSatellite = R.drawable.output_time_bus;
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
                                    isHandicap = R.drawable.output_handicap_green;
                                } else if (util.contains("SENZA PEDANA")) {
                                    isHandicap = R.drawable.output_handicap_red;
                                }
                                StringTokenizer token2 = new StringTokenizer(util, " ");
                                String busNumber = token2.nextToken();
                                int isSatellite = R.drawable.output_time_bus;
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
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputItemList;
    }

    @Override
    protected void onPostExecute(List<OutputItem> result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}