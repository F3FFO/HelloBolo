package com.f3ffo.hellobusbologna;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
    private TextInputEditText editTextBusStopCode;
    private TextInputEditText editTextBusStopName;
    private TextInputEditText editTextBusHour;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        spinnerBusCode = (Spinner) findViewById(R.id.spinnerBusCode);

        Button buttonStopViewer = (Button) findViewById(R.id.buttonStopViewer);
        editTextBusStopName = (TextInputEditText) findViewById(R.id.editTextBusStopName);
        editTextBusStopName.setEnabled(false);
        buttonStopViewer.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(2);
                final ListView listViewBusStation = (ListView) findViewById(R.id.listViewBusStation);

                adapter = new ArrayAdapter<>(context, R.layout.list_item, br.stops);
                listViewBusStation.setAdapter(adapter);
                listViewBusStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String value = listViewBusStation.getItemAtPosition(position).toString();
                        StringTokenizer token = new StringTokenizer(value, "-");
                        String temp = token.nextToken();
                        editTextBusStopCode.setText(temp.substring(0, temp.length() - 1));
                        editTextBusStopName.setText(br.getStopName());
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
        editTextBusHour = (TextInputEditText) findViewById(R.id.editTextBusHour);
        switchAdvancedOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextInputLayout textInputLayoutBusHour = (TextInputLayout) findViewById(R.id.textInputLayoutBusHour);
                busHour = "";
                if (isChecked) {
                    textInputLayoutBusHour.setVisibility(View.VISIBLE);
                } else {
                    editTextBusHour.setText("");
                    busHour = "";
                    textInputLayoutBusHour.setVisibility(View.INVISIBLE);
                }
            }
        });
        editTextBusStopCode = (TextInputEditText) findViewById(R.id.editTextBusStopCode);
        editTextBusStopCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_layout, br.busViewer(editTextBusStopCode.getText().toString()));
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
                    spinnerBusCode.setAdapter(spinnerArrayAdapter);
                    editTextBusStopName.setText(br.getStopName());
                } else {
                    editTextBusStopName.setText("");
                    spinnerBusCode.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        FloatingActionButton fabBus = (FloatingActionButton) findViewById(R.id.fabBus);
        fabBus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String busHourTemp = editTextBusHour.getText().toString();
                if (busHourTemp.contains(":")) {
                    busHour = busHourTemp.replace(":", "");
                } else if (busHourTemp.isEmpty()) {
                    busHour = "";
                } else if (busHourTemp.length() == 2) {
                    busHour = busHourTemp + "00";
                } else {
                    busHour = busHourTemp;
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
            br.busClass.clear();
            br.stops.clear();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_info) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
            Log.e("ERROR checkBus: ", e.getMessage());
        }
    }
}
