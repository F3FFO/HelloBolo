package com.f3ffo.hellobusbologna;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements AsyncResponse, TimePickerDialog.OnTimeSetListener {
    private ViewFlipper viewFlipper;
    private TextInputEditText editTextBusStopName;
    private TextView textViewBusHour;
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

        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        RelativeLayout relativeLayoutProgressBar = findViewById(R.id.relativeLayoutProgressBar);
        progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyle);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayoutProgressBar.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        spinnerBusCode = (Spinner) findViewById(R.id.spinnerBusCode);
        editTextBusStopName = (TextInputEditText) findViewById(R.id.editTextBusStopName);
        editTextBusStopName.setOnClickListener(new View.OnClickListener() {
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
                        busStop = temp.substring(0, temp.length() - 1);
                        editTextBusStopName.setText(br.getStopName());
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, br.busViewer(busStop));
                        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
                        spinnerBusCode.setAdapter(spinnerArrayAdapter);
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
                adapter.getFilter().filter(s.toString()); //TODO create an algorithm to search
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        textViewBusHour = (TextView) findViewById(R.id.textViewBusHour);
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        textViewBusHour.append(hour + ":" + minute);
        textViewBusHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "Ora");
            }
        });

        FloatingActionButton fabBus = (FloatingActionButton) findViewById(R.id.fabBus);
        fabBus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (spinnerBusCode.getSelectedItem().toString().equals("Tutti gli autobus")) {
                    busLine = "";
                    checkBus(busStop, busLine, busHour);
                } else {
                    busLine = spinnerBusCode.getSelectedItem().toString();
                    checkBus(busStop, busLine, busHour);
                }
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (minute < 10) {
            textViewBusHour.setText("Partenza: " + hourOfDay + ":0" + minute);
        }
        busHour = hourOfDay + "0" + minute;
    }

    /*private boolean isOnline() {
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
    }*/

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
