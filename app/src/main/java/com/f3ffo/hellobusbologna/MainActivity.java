package com.f3ffo.hellobusbologna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("INFO----------------------------------------------->"+R.raw.lineefermate_20190401);
    }

    public void checkBus() throws IOException {
        BusReader p = new BusReader();
        UrlBusElaboration n = new UrlBusElaboration();

        p.fileToArrayList("../../../../res/raw/lineefermate_20190401.csv"); //TODO change with R.raw.lineefermate_20190401
        p.busCodeToPrint("");
        p.stopCodeToPrint("");
        p.stopNameToPrint("");

        n.httpExtractOnlyStop("8");
        n.httpExtractStopAndHour("8", "12");
        n.httpExtractStopAndBusLine("8", "36");
    }
}
