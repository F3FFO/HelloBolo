package com.f3ffo.hellobusbologna;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private ViewFlipper viewFlipper;
    private EditText editTextBusStopCode;
    private EditText editTextBusStopName;
    private EditText editTextBusHour;
    private Spinner spinnerBusCode;
    private String busStop;
    private String busLine;
    private String busHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        FloatingActionButton fabBus = (FloatingActionButton) findViewById(R.id.fabBus);
        FloatingActionButton fabHome = (FloatingActionButton) findViewById(R.id.fabHome);
        FloatingActionButton fabReload = (FloatingActionButton) findViewById(R.id.fabReload);

        spinnerBusCode = (Spinner) findViewById(R.id.spinnerBusCode);

        editTextBusStopCode = (EditText) findViewById(R.id.editTextBusStopCode);
        editTextBusStopName = (EditText) findViewById(R.id.editTextBusStopName);
        editTextBusHour = (EditText) findViewById(R.id.editTextBusHour);

        Switch switchAdvancedOption = (Switch) findViewById(R.id.switchAdvancedOption);
        switchAdvancedOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout advancedOption = (LinearLayout) findViewById(R.id.advancedOption);
                if (isChecked) {
                    advancedOption.setVisibility(View.VISIBLE);
                } else {
                    advancedOption.setVisibility(View.INVISIBLE);
                }
            }
        });

        editTextBusStopName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    busViewer(editTextBusStopName.getText().toString(), false);
                } else {
                    editTextBusStopCode.setText("");
                    spinnerBusCode.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*editTextBusStopCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    busViewer(editTextBusStopCode.getText().toString(), true);
                } else {
                    editTextBusStopName.setText("");
                    spinnerBusCode.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/

        fabBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextBusHour.getText().toString().contains(":")) {
                    busHour = editTextBusHour.getText().toString().replace(":", "");
                }
                if (spinnerBusCode.getSelectedItem().toString().equals("Tutti gli autobus")) {
                    busStop = editTextBusStopCode.getText().toString();
                    busLine = "";
                    checkBus(busStop, busLine, busHour);
                } else {
                    busStop = editTextBusStopCode.getText().toString();
                    busLine = spinnerBusCode.getSelectedItem().toString();
                    checkBus(busStop, busLine, busHour);
                }
            }
        });

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(0);
            }
        });

        fabReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBus(busStop, busLine, busHour); //TODO Toast for user
            }
        });
    }

    /***
     * Display the result of UrlElaboration(AsyncTask)
     *
     * @param output ArrayList containing the output of UrlElaboration
     * @see UrlElaboration
     */
    @Override
    public void processFinish(ArrayList<String> output) {
        if (output.get(0).contains("non gestita") || output.get(0).contains("assente") || output.get(0).contains("Mancano")) {
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
     * Display into spinner the array created in BusReader class
     *
     * @param stopCode Code of bus stop
     * @param isCode   true -> the input is the code of the bus; false -> the input is the name of bus station
     * @see BusReader
     */
    public void busViewer(String stopCode, boolean isCode) {
        NewBusReader p = new NewBusReader();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, p.extractFromFile(getResources().openRawResource(R.raw.lineefermate_20190501), stopCode));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerBusCode.setAdapter(spinnerArrayAdapter);
        if (isCode) {
            editTextBusStopName.setText(p.stopNameViewer());
        } else {
            editTextBusStopCode.setText(p.stopCodeViewer());
        }
    }
}
