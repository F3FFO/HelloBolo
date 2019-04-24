package com.f3ffo.hellobusbologna;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private ViewFlipper viewFlipper;
    private EditText editTextBusStop;
    private EditText editTextBusHour;
    private Spinner spinnerBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        FloatingActionButton fabOk = (FloatingActionButton) findViewById(R.id.fabOk);
        FloatingActionButton fabHome = (FloatingActionButton) findViewById(R.id.fabHome);

        spinnerBus = (Spinner) findViewById(R.id.spinnerBus);

        editTextBusStop = (EditText) findViewById(R.id.editTextBusStop);
        editTextBusHour = (EditText) findViewById(R.id.editTextBusHour);


        editTextBusStop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    busForStop(editTextBusStop.getText().toString());
                } else {
                    spinnerBus.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBus(editTextBusStop.getText().toString(), spinnerBus.getSelectedItem().toString(), editTextBusHour.getText().toString());
            }
        });

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /***
     * Display the result of UrlElaboration(AsyncTask)
     *
     * @param output ArrayList containing the output of UrlElaboration
     * @see UrlElaboration
     */
    @Override
    public void processFinish(ArrayList<String> output) {
        if (output.get(0).contains("non gestiti") || output.get(0).contains("assente") || output.get(0).contains("Mancano")) {
            Toast.makeText(this, output.get(0), Toast.LENGTH_LONG).show();
        } else {
            TextView textView = (TextView) findViewById(R.id.textBus1);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < output.size(); i++) {
                builder.append(output.get(i) + "\n");
            }
            textView.setText(builder.toString());
            viewFlipper.setDisplayedChild(1);
        }
    }

    /**
     * Send to UrlElaboration the param take from user and run UrlElaboration(AsyncTask)
     *
     * @param busStop Code of the bus stop
     * @param busLine Code of the line bus
     * @param busHour Specific hour of the day
     * @see UrlElaboration
     */
    protected void checkBus(String busStop, String busLine, String busHour) {
        try {
            UrlElaboration ue = new UrlElaboration();
            ue.setDelegate(this);
            ue.setBusStop(busStop);
            ue.setBusLine(busLine);
            ue.setBusHour(busHour);
            ue.execute();
        } catch (Exception e) {
            Log.e("ERROR: ", e.getMessage());
        }
    }

    /**
     * Display into spinner the array created in NewBusReader class
     *
     * @param stopCode Code of bus stop
     * @see NewBusReader
     */
    public void busForStop(String stopCode) {
        NewBusReader p = new NewBusReader();
        p.fileToArrayList(getResources().openRawResource(R.raw.lineefermate_20190401));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, p.stopCodeToView(stopCode));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBus.setAdapter(spinnerArrayAdapter);
    }
}
