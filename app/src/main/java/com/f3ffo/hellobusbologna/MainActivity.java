package com.f3ffo.hellobusbologna;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_main);
        try {
            UrlElaboration ue = new UrlElaboration();
            /*ue.setStop("6036");
            ue.setHour("");
            ue.setBusline("");*/
            new UrlElaboration().execute();
        } catch (Exception e) {
            Log.e("ERROR into Main: ", e.getMessage());
        }
    }


    public void checkBus() throws IOException {
        BusReader p = new BusReader();

        p.fileToArrayList("../../../../res/raw/lineefermate_20190401.csv"); //TODO change with R.raw.lineefermate_20190401
        p.busCodeToPrint("");
        p.stopCodeToPrint("");
        p.stopNameToPrint("");
    }

    protected class UrlElaboration extends AsyncTask<Void, Void, ArrayList<String>> {
        public String hour = "";
        public String stop = "6036";
        public String busline = "";
/*
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
*/
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
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
                ISR.close();
                huc.disconnect();
            } catch (IOException e) {
                Log.e("ERROR: ", e.getMessage());
            }
            return array;
        }

        @Override
        protected void onPostExecute(ArrayList<String> ris) {
            TextView textView = (TextView) findViewById(R.id.textBus1);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < ris.size(); i++) {
                builder.append(ris.get(i) + "\n");
            }
            textView.setText(builder.toString());
        }
    }

}
