package com.f3ffo.hellobusbologna;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements AsyncResponse, TimePickerDialog.OnTimeSetListener {
    private ViewFlipper viewFlipper;
    private TextInputEditText editTextBusStopName;
    private TextView busCodeText;
    private TextView textViewBusHour;
    private Spinner spinnerBusCode;
    private String busStop, busLine, busHour;
    private ArrayAdapter<String> adapter;
    protected static BusReader br = new BusReader();
    private ProgressBar progressBar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        RelativeLayout relativeLayoutProgressBar = findViewById(R.id.relativeLayoutProgressBar);
        progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyle);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayoutProgressBar.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        spinnerBusCode = (Spinner) findViewById(R.id.spinnerBusCode);
        editTextBusStopName = (TextInputEditText) findViewById(R.id.editTextBusStopName);
        textViewBusHour = (TextView) findViewById(R.id.textViewBusHour);
        busCodeText = (TextView) findViewById(R.id.busCodeText);
        FloatingActionButton fabBus = (FloatingActionButton) findViewById(R.id.fabBus);

        editTextBusStopName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    spinnerBusCode.setVisibility(View.GONE);
                    textViewBusHour.setVisibility(View.GONE);
                    busCodeText.setVisibility(View.GONE);
                    viewFlipper.setDisplayedChild(2);
                    final ListView listViewBusStation = (ListView) findViewById(R.id.listViewBusStation);
                    adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item, br.stops);
                    listViewBusStation.setAdapter(adapter);
                    listViewBusStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            spinnerBusCode.setVisibility(View.VISIBLE);
                            textViewBusHour.setVisibility(View.VISIBLE);
                            busCodeText.setVisibility(View.VISIBLE);
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

                    editTextBusStopName.addTextChangedListener(new TextWatcher() {

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
                }
                return false;
            }
        });
        textViewBusHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "Orario");
            }
        });


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

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (minute < 10) {
            textViewBusHour.setText("Partenza: " + hourOfDay + ":0" + minute);
            busHour = hourOfDay + "0" + minute;
        } else {
            textViewBusHour.setText("Partenza: " + hourOfDay + ":" + minute);
            busHour = hourOfDay + "" + minute;
        }
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
        if (viewFlipper.getDisplayedChild() == 2) {
            spinnerBusCode.setVisibility(View.VISIBLE);
            textViewBusHour.setVisibility(View.VISIBLE);
            busCodeText.setVisibility(View.VISIBLE);
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
    public void processFinish(List<CardViewItem> output) {
        //if (output.get(0).contains("non gestita") || output.get(0).contains("assente") || output.get(0).contains("Mancano")) {
        //    Toast.makeText(MainActivity.this, output.get(0), Toast.LENGTH_LONG).show();
        RecyclerView recyclerViewBusOutput = (RecyclerView) findViewById(R.id.recyclerViewBusOutput);
        recyclerViewBusOutput.setHasFixedSize(true);
        recyclerViewBusOutput.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        List<CardViewItem> cardViewItemList = new ArrayList<>();
        for (int i = 0; i < output.size(); i++) {
            cardViewItemList.add(new CardViewItem(
                    output.get(i).getBusNumber(),
                    output.get(i).getBusHour(),
                    output.get(i).getImage()));
        }
        OutputAdapter adapter = new OutputAdapter(MainActivity.this, cardViewItemList);
        recyclerViewBusOutput.setAdapter(adapter);
        viewFlipper.setDisplayedChild(1);
        //}
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void checkBus(String busStop, String busLine, String busHour) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            UrlElaboration ue = new UrlElaboration();
            ue.setDelegate(MainActivity.this);
            ue.setBusStop(busStop);
            ue.setBusLine(busLine);
            ue.setBusHour(busHour);
            ue.execute();
        } catch (Exception e) {
            Log.e("ERROR checkBus: ", e.getMessage());
        }
    }
}
