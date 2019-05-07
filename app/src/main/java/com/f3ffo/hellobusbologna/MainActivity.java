package com.f3ffo.hellobusbologna;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private ArrayAdapter<String> adapter;
    protected static BusReader br = new BusReader();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        if (isOnline()) {
            RelativeLayout relativeLayoutProgressBar = findViewById(R.id.relativeLayoutProgressBar);
            progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyle);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            relativeLayoutProgressBar.addView(progressBar, params);
            progressBar.setVisibility(View.GONE);

            spinnerBusCode = (Spinner) findViewById(R.id.spinnerBusCode);

            Button buttonStopViewer = (Button) findViewById(R.id.buttonStopViewer);
            editTextBusStopName = (TextInputEditText) findViewById(R.id.editTextBusStopName);
            editTextBusStopName.setEnabled(false);
            buttonStopViewer.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    viewFlipper.setDisplayedChild(2);
                    final ListView listViewBusStation = (ListView) findViewById(R.id.listViewBusStation);

                    adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item, br.stops);
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
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, br.busViewer(editTextBusStopCode.getText().toString()));
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
                    RelativeLayout relativeLayoutProgressBarReload = findViewById(R.id.relativeLayoutProgressBarReload);
                    progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyle);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    relativeLayoutProgressBarReload.addView(progressBar, params);
                    progressBar.setVisibility(View.GONE);
                    checkBus(busStop, busLine, busHour);
                }
            });
        } else {
            RelativeLayout relativeLayoutProgressBar = findViewById(R.id.relativeLayoutProgressBar);
            progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyle);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            relativeLayoutProgressBar.addView(progressBar, params);
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMan = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL("https://hellobuswsweb.tper.it/web-services/hello-bus.asmx/QueryHellobus?fermata=%s&oraHHMM=%s&linea=%s");
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                if (urlConn.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
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
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (output.get(0).contains("non gestita") || output.get(0).contains("assente") || output.get(0).contains("Mancano")) {
            Toast.makeText(MainActivity.this, output.get(0), Toast.LENGTH_LONG).show();
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

    private void checkBus(String busStop, String busLine, String busHour) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
