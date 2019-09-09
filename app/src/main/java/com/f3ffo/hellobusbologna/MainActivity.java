package com.f3ffo.hellobusbologna;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.f3ffo.hellobusbologna.asyncInterface.AsyncResponseUrl;
import com.f3ffo.hellobusbologna.favourite.Favourites;
import com.f3ffo.hellobusbologna.favourite.FavouritesAdapter;
import com.f3ffo.hellobusbologna.favourite.FavouritesItem;
import com.f3ffo.hellobusbologna.hellobus.BusClass;
import com.f3ffo.hellobusbologna.hellobus.BusReader;
import com.f3ffo.hellobusbologna.hellobus.UrlElaboration;
import com.f3ffo.hellobusbologna.output.OutputAdapter;
import com.f3ffo.hellobusbologna.output.OutputErrorAdapter;
import com.f3ffo.hellobusbologna.output.OutputItem;
import com.f3ffo.hellobusbologna.preference.PreferenceActivity;
import com.f3ffo.hellobusbologna.rss.ArticleStatePagerAdapter;
import com.f3ffo.hellobusbologna.search.SearchAdapter;
import com.f3ffo.hellobusbologna.search.SearchItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AsyncResponseUrl, SwipeRefreshLayout.OnRefreshListener {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean doubleBackToExitPressedOnce = false;
    private ConstraintLayout constraintLayoutOutput, constraintLayoutSearch, constraintLayoutFavourites;
    private ViewPager viewPagerRss;
    private TabLayout tabsRss;
    private LinearLayoutCompat linearLayoutBusCode, linearLayoutHour;
    private MaterialTextView materialTextViewBusHour, materialTextViewAppName;
    private AppCompatSpinner spinnerBusCode;
    private FloatingActionButton fabBus;
    private ProgressBar progressBarOutput;
    private SwipeRefreshLayout swipeRefreshLayoutOutput;
    private MaterialSearchBar searchViewBusStopName;
    private BottomNavigationView bottomNavView;
    private SearchAdapter adapterBusStation;
    private FavouritesAdapter adapterFavourites;
    private OutputAdapter adapterOutput;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private List<OutputItem> outputItemList;
    private List<FavouritesItem> fav = new ArrayList<>();
    private List<SearchItem> stops = new ArrayList<>();
    private ArrayList<BusClass> busClass = new ArrayList<>();
    private String busStopCode = "", busLine = "", busHour = "", currentBusStopName = "";
    private Favourites fv = new Favourites();
    //private Maps maps = new Maps();
    //private SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.materialToolbar));
        checkPermissions();
        //supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        constraintLayoutOutput = findViewById(R.id.constraintLayoutOutput);
        constraintLayoutSearch = findViewById(R.id.constraintLayoutSearch);
        constraintLayoutFavourites = findViewById(R.id.constraintLayoutFavourites);
        linearLayoutHour = findViewById(R.id.linearLayoutHour);
        linearLayoutBusCode = findViewById(R.id.linearLayoutBusCode);
        bottomNavView = findViewById(R.id.bottomNavView);
        spinnerBusCode = findViewById(R.id.spinnerBusCode);
        materialTextViewBusHour = findViewById(R.id.materialTextViewBusHour);
        materialTextViewAppName = findViewById(R.id.materialTextViewAppName);
        fabBus = findViewById(R.id.fabBus);
        swipeRefreshLayoutOutput = findViewById(R.id.swipeRefreshLayoutOutput);
        searchViewBusStopName = findViewById(R.id.searchViewBusStopName);
        progressBarOutput = findViewById(R.id.progressBarOutput);
        bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPagerRss = findViewById(R.id.viewPagerRss);
        tabsRss = findViewById(R.id.tabsRss);
        bottomNavView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
        outputItemList = new ArrayList<>();
        swipeRefreshLayoutOutput.setOnRefreshListener(MainActivity.this);
        swipeRefreshLayoutOutput.setEnabled(false);
        swipeRefreshLayoutOutput.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark);
        BusReader br = new BusReader();
        busClass.addAll(br.extractFromFile(MainActivity.this));
        stops.addAll(br.stopsViewer(MainActivity.this, busClass));
        buildRecyclerViewSearch();
        fv.readFile(MainActivity.this);
        fav.addAll(fv.getFavouritesList());
        buildRecyclerViewFavourites();
        ArticleStatePagerAdapter sectionsPagerAdapter = new ArticleStatePagerAdapter(this, getSupportFragmentManager());
        viewPagerRss.setAdapter(sectionsPagerAdapter);
        tabsRss.setupWithViewPager(viewPagerRss);
        MaterialTextView placeHolder = findViewById(R.id.mt_placeholder);
        placeHolder.setTextAppearance(MainActivity.this, R.style.TextAppearance_MaterialComponents_Body1_Custom);
        searchViewBusStopName.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (enabled) {
                    materialTextViewAppName.setVisibility(View.GONE);
                    setElementAppBar(false);
                    fabBus.hide();
                    setDisplayChild(2);
                } else {
                    if (constraintLayoutOutput.getVisibility() == View.GONE && currentBusStopName.equals("")) {
                        materialTextViewAppName.setVisibility(View.VISIBLE);
                        setDisplayChild(0);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });
        searchViewBusStopName.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapterBusStation.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        adapterBusStation.setOnItemClickListener((int position) -> {
            if (!currentBusStopName.equals(stops.get(position).getBusStopName())) {
                if (adapterOutput != null && !outputItemList.isEmpty()) {
                    outputItemList.clear();
                    adapterOutput.notifyDataSetChanged();
                }
                busStopCode = stops.get(position).getBusStopCode();
                currentBusStopName = stops.get(position).getBusStopName();
                //maps.loadMap(MainActivity.this, supportMapFragment, Double.parseDouble(stops.get(position).getLatitude().replace(",", ".")), Double.parseDouble(stops.get(position).getLongitude().replace(",", ".")));
                spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, busViewer(busStopCode));
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
                spinnerBusCode.setAdapter(spinnerArrayAdapter);
                searchViewBusStopName.setPlaceHolder(currentBusStopName);
                searchViewBusStopName.disableSearch();
                setElementAppBar(true);
                setDisplayChild(1);
                fabBus.show();
            } else {
                searchViewBusStopName.disableSearch();
                setElementAppBar(true);
                setDisplayChild(1);
                fabBus.show();
            }
        });
        adapterBusStation.setOnFavouriteButtonClickListener((int position) -> {
            Favourites favourites = new Favourites();
            if (refreshElement(position)) {
                FavouritesItem item = favourites.addFavourite(MainActivity.this, stops.get(position).getBusStopCode(), stops.get(position).getBusStopName(), stops.get(position).getBusStopAddress(), stops.get(position).getLatitude(), stops.get(position).getLongitude());
                if (item != null) {
                    fav.add(item);
                    adapterBusStation.notifyItemChanged(position);
                    adapterFavourites.notifyItemInserted(fav.size());
                    Toast.makeText(MainActivity.this, R.string.favourite_added, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.favourite_not_added, Toast.LENGTH_LONG).show();
                }
            } else {
                if (fv.removeFavourite(MainActivity.this, stops.get(position).getBusStopCode())) {
                    boolean isRemoved = false;
                    for (int i = 0; i < fav.size() && !isRemoved; i++) {
                        if (fav.get(i).getBusStopCode().equals(stops.get(position).getBusStopCode())) {
                            fav.remove(i);
                            adapterBusStation.notifyItemChanged(position);
                            adapterFavourites.notifyItemRemoved(i);
                            isRemoved = true;
                        }
                    }
                    Toast.makeText(MainActivity.this, R.string.favourite_removed, Toast.LENGTH_LONG).show();
                }
            }
        });
        materialTextViewBusHour.setOnClickListener((View v) -> {
            v = LayoutInflater.from(MainActivity.this).inflate(R.layout.time_picker, null);
            TimePicker timePicker = v.findViewById(R.id.timePicker);
            timePicker.setIs24HourView(true);
            boolean[] isNow = {true};
            timePicker.setOnTimeChangedListener((TimePicker timePicker2, int i, int i1) -> isNow[0] = false);
            new MaterialAlertDialogBuilder(MainActivity.this, R.style.TimePickerTheme)
                    .setView(v)
                    .setPositiveButton(R.string.time_picker_ok, (DialogInterface dialog, int which) -> {
                        setTime(timePicker.getHour(), timePicker.getMinute(), isNow[0]);
                        dialog.dismiss();
                    })
                    .setNeutralButton(R.string.time_picker_cancel, (DialogInterface dialog, int which) -> dialog.dismiss())
                    .show();
            MaterialTextView textViewResetHour = v.findViewById(R.id.textViewResetHour);
            textViewResetHour.setOnClickListener((View view) -> {
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
                isNow[0] = true;
            });
        });
        spinnerBusCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerArrayAdapter != null) {
                    if (!spinnerArrayAdapter.getItem(position).equals(busStopCode)) {
                        swipeRefreshLayoutOutput.setEnabled(false);
                        outputItemList.clear();
                        fabBus.show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        fabBus.setOnClickListener((View v) -> {
            if (!busStopCode.isEmpty()) {
                if (spinnerBusCode.getSelectedItem().toString().equals("Tutti gli autobus")) {
                    busLine = "";
                } else {
                    busLine = spinnerBusCode.getSelectedItem().toString();
                }
                fabBus.hide();
                checkBus(busStopCode, busLine, busHour);
                swipeRefreshLayoutOutput.setEnabled(true);
            } else {
                Toast.makeText(MainActivity.this, "Fermata mancante", Toast.LENGTH_LONG).show();
            }
        });
        adapterFavourites.setOnItemClickListener((int position) -> {
            busStopCode = fav.get(position).getBusStopCode();
            spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, busViewer(busStopCode));
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
            spinnerBusCode.setAdapter(spinnerArrayAdapter);
            setElementAppBar(true);
            setDisplayChild(1);
            fabBus.show();
            searchViewBusStopName.setPlaceHolder(fav.get(position).getBusStopName());
            searchViewBusStopName.disableSearch();
            //maps.loadMap(MainActivity.this, supportMapFragment, Double.parseDouble(fav.get(position).getLatitude().replace(",", ".")), Double.parseDouble(fav.get(position).getLongitude().replace(",", ".")));
        });
        adapterFavourites.setOnFavouriteButtonClickListener((int position) -> {
            if (fv.removeFavourite(MainActivity.this, fav.get(position).getBusStopCode())) {
                boolean isRemoved = false;
                for (int i = 0; i < stops.size() && !isRemoved; i++) {
                    if (fav.get(position).getBusStopCode().equals(stops.get(i).getBusStopCode()) && !refreshElement(i)) {
                        adapterFavourites.notifyItemRemoved(position);
                        fav.remove(position);
                        adapterBusStation.notifyItemChanged(i);
                        isRemoved = true;
                    }
                }
                Toast.makeText(MainActivity.this, R.string.favourite_removed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            String[] permissions = missingPermissions.toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS, grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            for (int index = permissions.length - 1; index >= 0; --index) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.permissions, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = (@NonNull MenuItem item) -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                setElementAppBar(false);
                materialTextViewAppName.setVisibility(View.VISIBLE);
                setDisplayChild(0);
                fabBus.hide();
                bottomNavView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
                return true;
            case R.id.navigation_favourites:
                setElementAppBar(false);
                materialTextViewAppName.setVisibility(View.VISIBLE);
                setDisplayChild(3);
                fabBus.hide();
                bottomNavView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
                return true;
            case R.id.navigation_settings:
                //setElementAppBar(false);
                //materialTextViewAppName.setVisibility(View.VISIBLE);
                //fabBus.hide();
                bottomNavView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
                Intent intentPreference = new Intent(MainActivity.this, PreferenceActivity.class);
                startActivity(intentPreference);
                return true;
        }
        return false;
    };

    private ArrayList<String> busViewer(String busStopCodeIn) {
        ArrayList<String> bus = new ArrayList<>();
        bus.add(0, "Tutti gli autobus");
        for (int i = 0; i < busClass.size(); i++) {
            String busStopCode = busClass.get(i).getBusStopCode();
            if (busStopCode.equals(busStopCodeIn)) {
                bus.add(busClass.get(i).getBusCode());
                this.currentBusStopName = busClass.get(i).getBusStopName();
            }
        }
        return bus;
    }

    private void setElementAppBar(boolean isVisible) {
        if (isVisible) {
            linearLayoutHour.setVisibility(View.VISIBLE);
            linearLayoutBusCode.setVisibility(View.VISIBLE);
        } else {
            linearLayoutHour.setVisibility(View.GONE);
            linearLayoutBusCode.setVisibility(View.GONE);
        }
    }

    private void setDisplayChild(int displayChild) {
        if (displayChild == 0) {
            searchViewBusStopName.setPlaceHolder("");
            viewPagerRss.setVisibility(View.VISIBLE);
            tabsRss.setVisibility(View.VISIBLE);
            constraintLayoutOutput.setVisibility(View.GONE);
            constraintLayoutSearch.setVisibility(View.GONE);
            constraintLayoutFavourites.setVisibility(View.GONE);
        } else if (displayChild == 1) {
            viewPagerRss.setVisibility(View.GONE);
            tabsRss.setVisibility(View.GONE);
            constraintLayoutOutput.setVisibility(View.VISIBLE);
            constraintLayoutSearch.setVisibility(View.GONE);
            constraintLayoutFavourites.setVisibility(View.GONE);
        } else if (displayChild == 2) {
            viewPagerRss.setVisibility(View.GONE);
            tabsRss.setVisibility(View.GONE);
            constraintLayoutOutput.setVisibility(View.GONE);
            constraintLayoutSearch.setVisibility(View.VISIBLE);
            constraintLayoutFavourites.setVisibility(View.GONE);
        } else if (displayChild == 3) {
            searchViewBusStopName.setPlaceHolder("");
            viewPagerRss.setVisibility(View.GONE);
            tabsRss.setVisibility(View.GONE);
            constraintLayoutOutput.setVisibility(View.GONE);
            constraintLayoutSearch.setVisibility(View.GONE);
            constraintLayoutFavourites.setVisibility(View.VISIBLE);
        }
    }

    private void buildRecyclerViewFavourites() {
        RecyclerView recyclerViewFavourites = findViewById(R.id.recyclerViewFavourites);
        recyclerViewFavourites.setHasFixedSize(true);
        recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapterFavourites = new FavouritesAdapter(fav);
        recyclerViewFavourites.setAdapter(adapterFavourites);
        adapterFavourites.notifyDataSetChanged();
    }

    private void buildRecyclerViewSearch() {
        RecyclerView recyclerViewBusStation = findViewById(R.id.recyclerViewBusStation);
        recyclerViewBusStation.setHasFixedSize(true);
        recyclerViewBusStation.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapterBusStation = new SearchAdapter(stops);
        recyclerViewBusStation.setAdapter(adapterBusStation);
        adapterBusStation.notifyDataSetChanged();
    }

    private boolean refreshElement(int position) {
        if (stops.get(position).getImageFavourite() == R.drawable.star_border) {
            stops.get(position).setImageFavourite(R.drawable.star);
            return true;
        } else {
            stops.get(position).setImageFavourite(R.drawable.star_border);
            return false;
        }
    }

    private void setTime(int hour, int minute, boolean isNow) {
        if (!isNow) {
            if (minute < 10) {
                materialTextViewBusHour.setText(getString(R.string.busHourText2, hour, ("0" + minute)));
                busHour = hour + "0" + minute;
            } else {
                materialTextViewBusHour.setText(getString(R.string.busHourText2, hour, ("" + minute)));
                busHour = hour + "" + minute;
            }
        } else {
            materialTextViewBusHour.setText(R.string.busHourText);
            busHour = "";
        }
        outputItemList.clear();
        fabBus.show();
    }

    @Override
    public void onBackPressed() {
        if (constraintLayoutSearch.getVisibility() == View.VISIBLE && currentBusStopName.equals("")) {
            setElementAppBar(false);
            setDisplayChild(0);
            fabBus.hide();
        } else if (constraintLayoutSearch.getVisibility() == View.VISIBLE && !currentBusStopName.equals("")) {
            setElementAppBar(true);
            setDisplayChild(1);
            fabBus.show();
        } else if (viewPagerRss.getVisibility() == View.INVISIBLE && currentBusStopName.equals("")) {
            searchViewBusStopName.setPlaceHolder("");
        } else if (viewPagerRss.getVisibility() == View.VISIBLE && tabsRss.getVisibility() == View.VISIBLE) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        checkBus(busStopCode, busLine, busHour);
        Toast.makeText(MainActivity.this, R.string.update_output, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processStart() {
        if (outputItemList.isEmpty()) {
            progressBarOutput.setVisibility(View.VISIBLE);
        } else {
            outputItemList.clear();
        }
    }

    @Override
    public void processFinish(List<OutputItem> output) {
        RecyclerView recyclerViewBusOutput = findViewById(R.id.recyclerViewBusOutput);
        recyclerViewBusOutput.setHasFixedSize(true);
        recyclerViewBusOutput.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        if ("".equals(output.get(0).getError())) {
            adapterOutput = new OutputAdapter(MainActivity.this, outputItemList);
            if ("".equals(busHour)) {
                String diffTime;
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
                for (int i = 1; i < output.size(); i++) {
                    StringTokenizer token = new StringTokenizer(output.get(i).getBusHour(), ":");
                    int diffHour = Integer.parseInt(token.nextToken()) - now.get(Calendar.HOUR_OF_DAY);
                    int diffMin = Integer.parseInt(token.nextToken()) - now.get(Calendar.MINUTE);
                    if (diffHour == 0 || diffHour < 0) {
                        if (diffMin < 2) {
                            diffTime = "In arrivo";
                        } else {
                            diffTime = diffMin + "min";
                        }
                    } else if (diffMin < 0) {
                        diffTime = 60 + diffMin + "min";
                    } else {
                        diffTime = diffHour + "h " + diffMin + "min";
                    }
                    outputItemList.add(new OutputItem(output.get(i).getBusNumber(), diffTime, output.get(i).getBusHourComplete(), output.get(i).getSatelliteOrHour(), output.get(i).getHandicap()));
                    recyclerViewBusOutput.setAdapter(adapterOutput);
                }
            } else {
                for (int i = 1; i < output.size(); i++) {
                    outputItemList.add(new OutputItem(output.get(i).getBusNumber(), output.get(i).getBusHourComplete(), "", output.get(i).getSatelliteOrHour(), output.get(i).getHandicap()));
                    recyclerViewBusOutput.setAdapter(adapterOutput);
                }
            }
            adapterOutput.notifyDataSetChanged();
        } else {
            outputItemList.add(new OutputItem(output.get(0).getError()));
            OutputErrorAdapter adapterOutputError = new OutputErrorAdapter(MainActivity.this, outputItemList);
            recyclerViewBusOutput.setAdapter(adapterOutputError);
            adapterOutputError.notifyDataSetChanged();
        }
        swipeRefreshLayoutOutput.setRefreshing(false);
        progressBarOutput.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void checkBus(String busStop, String busLine, String busHour) {
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            UrlElaboration ue = new UrlElaboration(busStop, busLine, busHour, MainActivity.this);
            ue.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
