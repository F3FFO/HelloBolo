package com.f3ffo.hellobusbologna;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private ViewFlipper viewFlipper;
    private EditText editTextBusStopCode;
    private EditText textViewBusStopName;
    private EditText editTextBusHour;
    private Spinner spinnerBusCode;
    private String busStop;
    private String busLine;
    private String busHour;
    private Context context;
    private ArrayAdapter<String> adapter;
    protected static BusReader br = new BusReader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        spinnerBusCode = (Spinner) findViewById(R.id.spinnerBusCode);

        Button buttonStopViewer = (Button) findViewById(R.id.buttonStopViewer);
        textViewBusStopName = (EditText) findViewById(R.id.editTextBusStopName);
        buttonStopViewer.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(2);
                final ListView listViewBusStation = (ListView) findViewById(R.id.listViewBusStation);

                adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, br.stops);
                listViewBusStation.setAdapter(adapter);
                listViewBusStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String value = listViewBusStation.getItemAtPosition(position).toString();
                        StringTokenizer token = new StringTokenizer(value, " -");
                        editTextBusStopCode.setText(token.nextToken());
                        textViewBusStopName.setText(br.getStopName());
                        viewFlipper.setDisplayedChild(0);
                    }
                });
            }
        });
        EditText editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        Switch switchAdvancedOption = (Switch) findViewById(R.id.switchAdvancedOption);
        switchAdvancedOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout advancedOption = (LinearLayout) findViewById(R.id.advancedOption);
                if (isChecked) {
                    advancedOption.setVisibility(View.VISIBLE);
                } else {
                    editTextBusHour.setText("");
                    advancedOption.setVisibility(View.INVISIBLE);
                }
            }
        });
        editTextBusStopCode = (EditText) findViewById(R.id.editTextBusStopCode);
        editTextBusStopCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    busViewer(editTextBusStopCode.getText().toString());
                } else {
                    textViewBusStopName.setText("");
                    spinnerBusCode.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        FloatingActionButton fabBus = (FloatingActionButton) findViewById(R.id.fabBus);
        editTextBusHour = (EditText) findViewById(R.id.editTextBusHour);
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
        FloatingActionButton fabHome = (FloatingActionButton) findViewById(R.id.fabHome);
        fabHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(0);
            }
        });
        FloatingActionButton fabReload = (FloatingActionButton) findViewById(R.id.fabReload);
        fabReload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBus(busStop, busLine, busHour); //TODO Toast for user
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewFlipper.getDisplayedChild() != 0) {
            viewFlipper.setDisplayedChild(0);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Display the result of UrlElaboration(AsyncTask)
     *
     * @param output ArrayList containing the output of UrlElaboration
     * @see UrlElaboration
     */
    @Override
    public void processFinish(ArrayList<String> output) {
        if (output.get(0).contains("non gestita") || output.get(0).contains("assente") || output.get(0).contains("Mancano")) {
            Toast.makeText(context, output.get(0), Toast.LENGTH_LONG).show();
        } else {
            TextView textView = (TextView) findViewById(R.id.textBus1);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < output.size(); i++) {
                builder.append(output.get(i)).append("\n");
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
     * @see BusReader
     */
    public void busViewer(String stopCode) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, br.busViewer(stopCode));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerBusCode.setAdapter(spinnerArrayAdapter);
        textViewBusStopName.setText(br.getStopName());
    }
}
