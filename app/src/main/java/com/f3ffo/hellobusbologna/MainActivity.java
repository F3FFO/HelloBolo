package com.f3ffo.hellobusbologna;

import com.f3ffo.hellobusbologna.adapter.FavouritesAdapter;
import com.f3ffo.hellobusbologna.adapter.OutputAdapter;
import com.f3ffo.hellobusbologna.adapter.OutputErrorAdapter;
import com.f3ffo.hellobusbologna.adapter.SearchAdapter;
import com.f3ffo.hellobusbologna.asyncInterface.AsyncResponse;
import com.f3ffo.hellobusbologna.fragment.TimePickerFragment;
import com.f3ffo.hellobusbologna.hellobus.BusReader;
import com.f3ffo.hellobusbologna.hellobus.Favourites;
import com.f3ffo.hellobusbologna.hellobus.UrlElaboration;
import com.f3ffo.hellobusbologna.model.OutputCardViewItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.navigation.NavigationView;
import com.lapism.searchview.Search;
import com.lapism.searchview.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AsyncResponse, TimePickerDialog.OnTimeSetListener, SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    private ConstraintLayout constraintLayoutOutput, constraintLayoutSearch, constraintLayoutFavourites;
    private AppCompatTextView busCodeText, textViewBusHour;
    private AppCompatSpinner spinnerBusCode;
    private String busStop = "", busLine = "", busHour = "";
    private SearchAdapter adapterBusStation;
    private FavouritesAdapter adapterFavourites;
    private FloatingActionButton fabBus;
    protected static BusReader br = new BusReader();
    private Favourites fv = new Favourites();
    private ProgressBar progressBar;
    private SearchView searchViewBusStopName;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout drawer;
    private List<OutputCardViewItem> outputCardViewItemList;
    private BottomNavigationView bottomNavView;
    private ArrayAdapter<String> spinnerArrayAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = (@NonNull MenuItem item) -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                constraintLayoutOutput.setVisibility(View.VISIBLE);
                constraintLayoutSearch.setVisibility(View.GONE);
                constraintLayoutFavourites.setVisibility(View.GONE);
                return true;
            case R.id.navigation_favourites:
                constraintLayoutOutput.setVisibility(View.GONE);
                constraintLayoutSearch.setVisibility(View.GONE);
                constraintLayoutFavourites.setVisibility(View.VISIBLE);
                return true;
            case R.id.navigation_notifications:
                //TODO something
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayoutOutput = findViewById(R.id.constraintLayoutOutput);
        constraintLayoutSearch = findViewById(R.id.constraintLayoutSearch);
        constraintLayoutFavourites = findViewById(R.id.constraintLayoutFavourites);
        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        spinnerBusCode = findViewById(R.id.spinnerBusCode);
        textViewBusHour = findViewById(R.id.textViewBusHour);
        busCodeText = findViewById(R.id.busCodeText);
        fabBus = findViewById(R.id.fabBus);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchViewBusStopName = findViewById(R.id.searchViewBusStopName);
        outputCardViewItemList = new ArrayList<>();
        drawer = findViewById(R.id.drawer_layout);
        NavigationView lateralNavView = findViewById(R.id.lateralNavView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        lateralNavView.setNavigationItemSelectedListener(MainActivity.this);
        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
        swipeRefreshLayout.setEnabled(false);
        buildRecyclerViewSearch();
        buildRecyclerViewFavourites(true);
        searchViewBusStopName.setOnOpenCloseListener(new Search.OnOpenCloseListener() {

            @Override
            public void onOpen() {
                spinnerBusCode.setVisibility(View.GONE);
                textViewBusHour.setVisibility(View.GONE);
                busCodeText.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.GONE);
                fabBus.hide();
                constraintLayoutOutput.setVisibility(View.GONE);
                constraintLayoutSearch.setVisibility(View.VISIBLE);
                constraintLayoutFavourites.setVisibility(View.GONE);
                searchViewBusStopName.setOnQueryTextListener(new Search.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(CharSequence query) {
                        return false;
                    }

                    @Override
                    public void onQueryTextChange(CharSequence newText) {
                        adapterBusStation.getFilter().filter(newText.toString());
                    }
                });
            }

            @Override
            public void onClose() {
                if ("".equals(busStop)) {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
        adapterBusStation.setOnItemClickListener((int position) -> {
            busStop = br.getStops().get(position).getBusStopCode();
            spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, br.busViewer(busStop));
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
            spinnerBusCode.setAdapter(spinnerArrayAdapter);
            searchViewBusStopName.setText(br.getBusStopName());
            searchViewBusStopName.close();
            spinnerBusCode.setVisibility(View.VISIBLE);
            textViewBusHour.setVisibility(View.VISIBLE);
            busCodeText.setVisibility(View.VISIBLE);
            constraintLayoutSearch.setVisibility(View.GONE);
            constraintLayoutOutput.setVisibility(View.VISIBLE);
            bottomNavView.setVisibility(View.VISIBLE);
            fabBus.show();
        });
        adapterBusStation.setOnFavouriteButtonClickListener((int position) -> {
            Favourites favourites = new Favourites();
            if (favourites.addFavourite(MainActivity.this, br.getStops().get(position).getBusStopCode(), br.getStops().get(position).getBusStopName(), br.getStops().get(position).getBusStopAddress())) {
                buildRecyclerViewFavourites(false);
                br.refreshElement(position);
                adapterBusStation.notifyItemChanged(position);
                Toast.makeText(MainActivity.this, R.string.favourite_added, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, R.string.favourite_not_added, Toast.LENGTH_LONG).show();
            }
        });
        textViewBusHour.setOnClickListener((View v) -> {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "Ora");
        });
        spinnerBusCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerArrayAdapter != null) {
                    if (!spinnerArrayAdapter.getItem(position).equals(busStop)) {
                        swipeRefreshLayout.setEnabled(false);
                        outputCardViewItemList.clear();
                        fabBus.show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fabBus.setOnClickListener((View v) -> {
            if (!busStop.isEmpty()) {
                if (spinnerBusCode.getSelectedItem().toString().equals("Tutti gli autobus")) {
                    busLine = "";
                } else {
                    busLine = spinnerBusCode.getSelectedItem().toString();
                }
                fabBus.hide();
                checkBus(busStop, busLine, busHour);
                swipeRefreshLayout.setEnabled(true);
            } else {
                Toast.makeText(MainActivity.this, "Fermata mancante", Toast.LENGTH_LONG).show();
            }
        });
        searchViewBusStopName.setOnLogoClickListener(() -> drawer.openDrawer(GravityCompat.START));
        adapterFavourites.setOnFavouriteButtonClickListener((int position) -> {
            busStop = fv.getFavouritesList().get(position).getBusStopCode();
            searchViewBusStopName.setText(fv.getFavouritesList().get(position).getBusStopName());
        });
    }

    public void buildRecyclerViewFavourites(boolean isFirstTime) {
        RecyclerView recyclerViewFavourites = findViewById(R.id.recyclerViewFavourites);
        recyclerViewFavourites.setHasFixedSize(true);
        recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        fv.readFile(MainActivity.this, isFirstTime);
        adapterFavourites = new FavouritesAdapter(fv.getFavouritesList());
        recyclerViewFavourites.setAdapter(adapterFavourites);
    }

    public void buildRecyclerViewSearch() {
        RecyclerView recyclerViewBusStation = findViewById(R.id.recyclerViewBusStation);
        recyclerViewBusStation.setHasFixedSize(true);
        recyclerViewBusStation.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapterBusStation = new SearchAdapter(br.getStops());
        recyclerViewBusStation.setAdapter(adapterBusStation);
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
        outputCardViewItemList.clear();
        fabBus.show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchViewBusStopName.isOpen() && !busStop.isEmpty()) {
            if (searchViewBusStopName.getText().toString().isEmpty()) {
                searchViewBusStopName.setText(br.getBusStopName());
            }
            spinnerBusCode.setVisibility(View.VISIBLE);
            textViewBusHour.setVisibility(View.VISIBLE);
            busCodeText.setVisibility(View.VISIBLE);
            constraintLayoutSearch.setVisibility(View.GONE);
            constraintLayoutOutput.setVisibility(View.VISIBLE);
            bottomNavView.setVisibility(View.VISIBLE);
        } else if (searchViewBusStopName.isOpen() && busStop.isEmpty()) {
            spinnerBusCode.setVisibility(View.GONE);
            textViewBusHour.setVisibility(View.GONE);
            busCodeText.setVisibility(View.GONE);
            searchViewBusStopName.setText("");
            constraintLayoutSearch.setVisibility(View.GONE);
            constraintLayoutOutput.setVisibility(View.VISIBLE);
            bottomNavView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
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
    }

    @Override
    public void processStart() {
        if (outputCardViewItemList.isEmpty()) {
            RelativeLayout relativeLayoutProgressBar = findViewById(R.id.relativeLayoutProgressBar);
            progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyle);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            relativeLayoutProgressBar.addView(progressBar, params);
        } else {
            outputCardViewItemList.clear();
        }
    }

    @Override
    public void processFinish(List<OutputCardViewItem> output) {
        RecyclerView recyclerViewBusOutput = findViewById(R.id.recyclerViewBusOutput);
        recyclerViewBusOutput.setHasFixedSize(true);
        recyclerViewBusOutput.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        if ("".equals(output.get(0).getError())) {
            if ("".equals(busHour)) {
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
                String diffTime;
                for (int i = 1; i < output.size(); i++) {
                    StringTokenizer token = new StringTokenizer(output.get(i).getBusHour(), ":");
                    int diffHour = Integer.parseInt(token.nextToken()) - now.get(Calendar.HOUR_OF_DAY);
                    int diffMin = Integer.parseInt(token.nextToken()) - now.get(Calendar.MINUTE);
                    if (diffHour == 0 || diffHour < 0) {
                        if (diffMin < 3) {
                            diffTime = "In arrivo";
                        } else {
                            diffTime = diffMin + "min";
                        }
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
        swipeRefreshLayout.setRefreshing(false);
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
