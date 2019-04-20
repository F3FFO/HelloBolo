package com.f3ffo.hellobusbologna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_main);
        try {
            UrlBusElaboration n = new UrlBusElaboration();
            n.setStop("6036");
            new UrlBusElaboration().execute();
            String[] array = n.getRis();
            TextView TV = (TextView) findViewById(R.id.textBus1);
            for (int i = 0; i < array.length; i++) {
                TV.append(array[i]);
            }
        } catch (Exception e) {
            Log.e("ERROR into Main: ", e.getMessage());
        }
    }


    public void checkBus() throws IOException {
        BusReader p = new BusReader();
        UrlBusElaboration n = new UrlBusElaboration();

        p.fileToArrayList("../../../../res/raw/lineefermate_20190401.csv"); //TODO change with R.raw.lineefermate_20190401
        p.busCodeToPrint("");
        p.stopCodeToPrint("");
        p.stopNameToPrint("");

        /*n.httpExtractOnlyStop("8");
        n.httpExtractStopAndHour("8", "12");
        n.httpExtractStopAndBusLine("8", "36");*/
    }
}
