package com.f3ffo.hellobusbologna;

import com.f3ffo.hellobusbologna.adapter.OutputAdapter;
import com.f3ffo.hellobusbologna.adapter.OutputErrorAdapter;
import com.f3ffo.hellobusbologna.adapter.SearchAdapter;
import com.f3ffo.hellobusbologna.asyncInterface.AsyncResponse;
import com.f3ffo.hellobusbologna.fragment.TimePickerFragment;
import com.f3ffo.hellobusbologna.hellobus.BusReader;
import com.f3ffo.hellobusbologna.hellobus.UrlElaboration;
import com.f3ffo.hellobusbologna.items.OutputCardViewItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.lapism.searchview.Search;
import com.lapism.searchview.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AsyncResponse, TimePickerDialog.OnTimeSetListener, SwipeRefreshLayout.OnRefreshListener {
    private ViewFlipper viewFlipper;
    private AppCompatTextView busCodeText, textViewBusHour;
    private AppCompatSpinner spinnerBusCode;
    private String busStop, busLine, busHour;
    private SearchAdapter adapter;
    protected static BusReader br = new BusReader();
    private ProgressBar progressBar;
    private SearchView searchViewBusStopName;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewFlipper = findViewById(R.id.viewflipper);
        spinnerBusCode = findViewById(R.id.spinnerBusCode);
        textViewBusHour = findViewById(R.id.textViewBusHour);
        busCodeText = findViewById(R.id.busCodeText);
        FloatingActionButton fabBus = findViewById(R.id.fabBus);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchViewBusStopName = findViewById(R.id.searchViewBusStopName);

        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
        buildRecyclerViewSearch();

        adapter.setOnItemClickListener((int position) -> {
            viewFlipper.setDisplayedChild(0);
            searchViewBusStopName.close();
            busStop = br.getStops().get(position).getBusStopCode();
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, br.busViewer(busStop));
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
            spinnerBusCode.setAdapter(spinnerArrayAdapter);
            spinnerBusCode.setVisibility(View.VISIBLE);
            textViewBusHour.setVisibility(View.VISIBLE);
            busCodeText.setVisibility(View.VISIBLE);
            if (br.getBusStopName().length() > 18) {
                String busStopNameSub = br.getBusStopName().substring(0, 18);
                searchViewBusStopName.setText(busStopNameSub + "...");
            } else {
                searchViewBusStopName.setText(br.getBusStopName());
            }
        });
        searchViewBusStopName.setOnOpenCloseListener(new Search.OnOpenCloseListener() {

            @Override
            public void onOpen() {
                busLine = "";
                busHour = "";
                busStop = "";
                spinnerBusCode.setVisibility(View.GONE);
                textViewBusHour.setVisibility(View.GONE);
                busCodeText.setVisibility(View.GONE);
                viewFlipper.setDisplayedChild(1);
                searchViewBusStopName.setOnQueryTextListener(new Search.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(CharSequence query) {
                        return false;
                    }

                    @Override
                    public void onQueryTextChange(CharSequence newText) {
                        adapter.getFilter().filter(newText.toString());
                    }
                });
            }

            @Override
            public void onClose() {
            }
        });
        textViewBusHour.setOnClickListener((View v) -> {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "Orario");
        });
        fabBus.setOnClickListener((View v) -> {
            if (!busStop.isEmpty()) {
                if (spinnerBusCode.getSelectedItem().toString().equals("Tutti gli autobus")) {
                    busLine = "";
                    checkBus(busStop, busLine, busHour);
                } else {
                    busLine = spinnerBusCode.getSelectedItem().toString();
                    checkBus(busStop, busLine, busHour);
                }
            } else {
                Toast.makeText(MainActivity.this, "Fermata mancante", Toast.LENGTH_LONG).show();
            }
        });
        searchViewBusStopName.setOnLogoClickListener(() -> {
            //TODO open menu and show
        });
    }

    public void buildRecyclerViewSearch() {
        RecyclerView recyclerViewBusStation = findViewById(R.id.recyclerViewBusStation);
        recyclerViewBusStation.setHasFixedSize(true);
        recyclerViewBusStation.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new SearchAdapter(MainActivity.this, br.getStops());
        recyclerViewBusStation.setAdapter(adapter);
    }

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
        if (searchViewBusStopName.isOpen() && !busStop.isEmpty()) {
            spinnerBusCode.setVisibility(View.VISIBLE);
            textViewBusHour.setVisibility(View.VISIBLE);
            busCodeText.setVisibility(View.VISIBLE);
            viewFlipper.setDisplayedChild(0);
        } else if (searchViewBusStopName.isOpen() && busStop.isEmpty()) {
            spinnerBusCode.setVisibility(View.GONE);
            textViewBusHour.setVisibility(View.GONE);
            busCodeText.setVisibility(View.GONE);
            searchViewBusStopName.setText("");
            viewFlipper.setDisplayedChild(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_favourites:
                return true;
            case R.id.nav_info:
                return true;
            case R.id.nav_share:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        checkBus(busStop, busLine, busHour);
        Toast.makeText(MainActivity.this, "Aggiornato!", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void processStart() {
        RelativeLayout relativeLayoutProgressBar = findViewById(R.id.relativeLayoutProgressBar);
        progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyle);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayoutProgressBar.addView(progressBar, params);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void processFinish(List<OutputCardViewItem> output) {
        RecyclerView recyclerViewBusOutput = findViewById(R.id.recyclerViewBusOutput);
        recyclerViewBusOutput.setHasFixedSize(true);
        recyclerViewBusOutput.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        List<OutputCardViewItem> outputCardViewItemList = new ArrayList<>();
        if ("".equals(output.get(0).getError())) {
            if ("".equals(busHour)) {
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
                String diffTime;
                for (int i = 1; i < output.size(); i++) {
                    StringTokenizer token = new StringTokenizer(output.get(i).getBusHour(), ":");
                    int diffHour = Integer.parseInt(token.nextToken()) - now.get(Calendar.HOUR_OF_DAY);
                    int diffMin = Integer.parseInt(token.nextToken()) - now.get(Calendar.MINUTE);
                    if (diffHour == 0 || diffHour < 0) {
                        diffTime = diffMin + "min";
                    } else if (diffMin < 0) {
                        diffTime = 60 + diffMin + "min";
                    } else {
                        diffTime = diffHour + "h " + diffMin + "min";
                    }
                    outputCardViewItemList.add(new OutputCardViewItem(output.get(i).getBusNumber(), diffTime, output.get(i).getBusHourComplete(), output.get(i).getSatelliteOrHour(), output.get(i).getHandicap()));
                    OutputAdapter adapter = new OutputAdapter(MainActivity.this, outputCardViewItemList);
                    recyclerViewBusOutput.setAdapter(adapter);
                }
            } else {
                for (int i = 1; i < output.size(); i++) {
                    outputCardViewItemList.add(new OutputCardViewItem(output.get(i).getBusNumber(), output.get(i).getBusHourComplete(), "", output.get(i).getSatelliteOrHour(), output.get(i).getHandicap()));
                    OutputAdapter adapter = new OutputAdapter(MainActivity.this, outputCardViewItemList);
                    recyclerViewBusOutput.setAdapter(adapter);
                }
            }
        } else {
            outputCardViewItemList.add(new OutputCardViewItem(output.get(0).getErrorImage(), output.get(0).getError()));
            OutputErrorAdapter adapter = new OutputErrorAdapter(MainActivity.this, outputCardViewItemList);
            recyclerViewBusOutput.setAdapter(adapter);
        }
        viewFlipper.setDisplayedChild(0);
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void checkBus(String busStop, String busLine, String busHour) {
        try {
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
