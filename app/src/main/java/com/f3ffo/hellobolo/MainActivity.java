package com.f3ffo.hellobolo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.f3ffo.hellobolo.asyncInterface.AsyncResponseUrl;
import com.f3ffo.hellobolo.favourite.Favourites;
import com.f3ffo.hellobolo.favourite.FavouritesAdapter;
import com.f3ffo.hellobolo.favourite.FavouritesItem;
import com.f3ffo.hellobolo.hellobus.BusClass;
import com.f3ffo.hellobolo.hellobus.BusReader;
import com.f3ffo.hellobolo.hellobus.UrlElaboration;
import com.f3ffo.hellobolo.output.OutputAdapter;
import com.f3ffo.hellobolo.output.OutputErrorAdapter;
import com.f3ffo.hellobolo.output.OutputItem;
import com.f3ffo.hellobolo.preference.Preferences;
import com.f3ffo.hellobolo.preference.PreferencesActivity;
import com.f3ffo.hellobolo.rss.ArticleStatePagerAdapter;
import com.f3ffo.hellobolo.search.SearchAdapter;
import com.f3ffo.hellobolo.search.SearchItem;
import com.f3ffo.hellobolo.utility.CheckInternet;
import com.f3ffo.hellobolo.utility.Log;
import com.f3ffo.library.SearchBar;
import com.f3ffo.library.searchInterface.OnSearchActionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AsyncResponseUrl, SwipeRefreshLayout.OnRefreshListener {
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean doubleBackToExitPressedOnce = false;
    private ConstraintLayout constraintLayoutOutput, constraintLayoutSearch, constraintLayoutFavourites;
    private ViewPager viewPagerRss;
    private TabLayout tabsRss;
    private LinearLayoutCompat linearLayoutBusCode, linearLayoutHour, linearLayoutNoFavourites;
    private MaterialTextView materialTextViewBusHour;
    private AppCompatSpinner spinnerBusCode;
    private FloatingActionButton fabBus;
    private ContentLoadingProgressBar progressBarOutput;
    private SwipeRefreshLayout swipeRefreshLayoutOutput;
    private SearchBar searchViewBusStopName;
    private BottomNavigationView bottomNavView;
    private RecyclerView recyclerViewBusStation, recyclerViewBusGps, recyclerViewFavourites;
    private SearchAdapter adapterBusStation, adapterBusGps;
    private FavouritesAdapter adapterFavourites;
    private OutputAdapter adapterOutput;
    private OutputErrorAdapter adapterOutputError;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private List<OutputItem> outputItemList;
    private List<FavouritesItem> fav = new ArrayList<>();
    private List<SearchItem> stops = new ArrayList<>(), stopsGps = new ArrayList<>();
    private ArrayList<BusClass> busClass = new ArrayList<>();
    private String busStopCode = "", busLine = "", busHour = "", currentBusStopCode = "";
    private double latitude, longitude;
    private BusReader busReader = new BusReader();
    private Favourites favourites = new Favourites();
    private Preferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preference = new Preferences(MainActivity.this);
        preference.setPreferenceTheme();
        super.onCreate(savedInstanceState);
        initLanguage();
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_NO:
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    getWindow().getDecorView().setSystemUiVisibility(0);
                }
                break;
        }
        setContentView(R.layout.activity_main);
        init();
        BusReader.setDistance(preference.getPreferenceGps());
        Log.logInfo(MainActivity.this);
        searchViewBusStopName.setRadiusSearchBar(20);
        searchViewBusStopName.setCardViewElevation(0);
        searchViewBusStopName.setExtraIcon(R.drawable.gps_fixed);
        outputItemList = new ArrayList<>();
        setSwipeRefreshLayoutOutput();
        busClass.addAll(busReader.extractFromFile(MainActivity.this));
        stops.addAll(busReader.stopsViewer(MainActivity.this, busClass));
        buildRecyclerViewSearch(stops, false);
        favourites.readFile(MainActivity.this);
        fav.addAll(favourites.getFavouritesList());
        if (fav.isEmpty()) {
            recyclerViewFavourites.setVisibility(View.GONE);
            linearLayoutNoFavourites.setVisibility(View.VISIBLE);
        }
        buildRecyclerViewFavourites();
        viewPagerRss.setAdapter(new ArticleStatePagerAdapter(MainActivity.this, getSupportFragmentManager()));
        tabsRss.setupWithViewPager(viewPagerRss);
        searchViewBusStopName.setPlaceholderTextAppearance(MainActivity.this, R.style.TextAppearance_MaterialComponents_Body1_Custom);
        searchViewBusStopName.setOnExtraButtonClickListener(view -> {
            try {
                if (getLocation()) {
                    Log.logCoordinates(MainActivity.this, latitude, longitude);
                    latitude = ((long) (latitude * 1e6)) / 1e6;
                    longitude = ((long) (longitude * 1e6)) / 1e6;
                    List<String> busPosition = busReader.takeTheCorrespondingBusStop(busClass, latitude, longitude);
                    stopsGps.clear();
                    List<Integer> listPosition = new ArrayList<>();
                    if (busPosition.size() > 1) {
                        for (int i = 0; i < stops.size(); i++) {
                            for (int j = 0; j < busPosition.size(); j++) {
                                if (stops.get(i).getBusStopCode().equals(busPosition.get(j))) {
                                    listPosition.add(i);
                                    stopsGps.add(new SearchItem(stops.get(i).getBusStopCode(), stops.get(i).getBusStopName(), stops.get(i).getBusStopAddress(), stops.get(i).getImageFavourite(), stops.get(i).getLatitude(), stops.get(i).getLongitude()));
                                }
                            }
                        }
                        buildRecyclerViewSearch(stopsGps, true);
                        adapterBusGps.setOnItemClickListener(position -> itemAdapterMethod(stopsGps, position));
                        adapterBusGps.setOnFavouriteButtonClickListener(position -> {
                            Favourites favourites = new Favourites();
                            if (updateStarFavourite(stopsGps, position)) {
                                FavouritesItem item = favourites.addFavourite(MainActivity.this, stopsGps.get(position).getBusStopCode(), stopsGps.get(position).getBusStopName(), stopsGps.get(position).getBusStopAddress());
                                if (item != null) {
                                    fav.add(item);
                                    for (int element : listPosition) {
                                        if (stopsGps.get(position).getBusStopCode().equals(stops.get(element).getBusStopCode())) {
                                            adapterBusStation.notifyItemChanged(element);
                                            updateStarFavourite(stops, element);
                                        }
                                    }
                                    adapterBusGps.notifyItemChanged(position);
                                    adapterFavourites.notifyItemInserted(fav.size());
                                    Toast.makeText(MainActivity.this, R.string.toast_favourite_added, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.toast_favourite_not_added, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (favourites.removeFavourite(MainActivity.this, stopsGps.get(position).getBusStopCode())) {
                                    for (int i = 0; i < fav.size(); i++) {
                                        if (fav.get(i).getBusStopCode().equals(stopsGps.get(position).getBusStopCode())) {
                                            fav.remove(i);
                                            adapterBusGps.notifyItemChanged(position);
                                            adapterFavourites.notifyItemRemoved(i);
                                            break;
                                        }
                                    }
                                    Toast.makeText(MainActivity.this, R.string.toast_favourite_removed, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else if (busPosition.size() == 1) {
                        clearOutput();
                        busStopCode = busPosition.get(0);
                        for (int i = 0; i < stops.size(); i++) {
                            if (stops.get(i).getBusStopCode().equals(busPosition.get(0))) {
                                currentBusStopCode = stops.get(i).getBusStopCode();
                                spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, busViewer(busStopCode));
                                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
                                spinnerBusCode.setAdapter(spinnerArrayAdapter);
                                searchViewBusStopName.setPlaceHolder(stops.get(i).getBusStopName());
                                searchViewBusStopName.disableSearch();
                                setElementAppBar(true);
                                setDisplayChild(1);
                                fabBus.show();
                                break;
                            }
                        }

                    } else {
                        Toast.makeText(MainActivity.this, R.string.toast_no_bus_stop_gps, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.logError(MainActivity.this, e);
            }
        });
        searchViewBusStopName.setOnSearchActionListener(new OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (enabled) {
                    bottomNavView.setVisibility(View.GONE);
                    setVisibilityRecycler(false);
                    setElementAppBar(false);
                    fabBus.hide();
                    setDisplayChild(2);
                } else {
                    if (constraintLayoutOutput.getVisibility() == View.GONE && currentBusStopCode.equals("")) {
                        bottomNavView.setVisibility(View.VISIBLE);
                        bottomNavView.setSelectedItemId(R.id.navigation_home);
                        setDisplayChild(0);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
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
        adapterBusStation.setOnItemClickListener(position -> {
            bottomNavView.setVisibility(View.VISIBLE);
            itemAdapterMethod(stops, position);
        });
        adapterBusStation.setOnFavouriteButtonClickListener(position -> {
            Favourites favourites = new Favourites();
            if (updateStarFavourite(stops, position)) {
                recyclerViewFavourites.setVisibility(View.VISIBLE);
                linearLayoutNoFavourites.setVisibility(View.GONE);
                FavouritesItem item = favourites.addFavourite(MainActivity.this, stops.get(position).getBusStopCode(), stops.get(position).getBusStopName(), stops.get(position).getBusStopAddress());
                if (item != null) {
                    fav.add(item);
                    adapterBusStation.notifyItemChanged(position);
                    adapterFavourites.notifyItemInserted(fav.size());
                    Toast.makeText(MainActivity.this, R.string.toast_favourite_added, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_favourite_not_added, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (favourites.removeFavourite(MainActivity.this, stops.get(position).getBusStopCode())) {
                    boolean isRemoved = false;
                    for (int i = 0; i < fav.size() && !isRemoved; i++) {
                        if (fav.get(i).getBusStopCode().equals(stops.get(position).getBusStopCode())) {
                            fav.remove(i);
                            adapterBusStation.notifyItemChanged(position);
                            adapterFavourites.notifyItemRemoved(i);
                            isRemoved = true;
                        }
                    }
                    if (fav.isEmpty()) {
                        recyclerViewFavourites.setVisibility(View.GONE);
                        linearLayoutNoFavourites.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(MainActivity.this, R.string.toast_favourite_removed, Toast.LENGTH_SHORT).show();
                }
            }
        });
        adapterBusStation.setOnMapsButtonClickListener(position -> {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + stops.get(position).getLatitude() + "," + stops.get(position).getLongitude());
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        });
        materialTextViewBusHour.setOnClickListener(view -> {
            view = LayoutInflater.from(MainActivity.this).inflate(R.layout.time_picker, null);
            TimePicker timePicker = view.findViewById(R.id.timePicker);
            timePicker.setIs24HourView(true);
            boolean[] isNow = {true};
            timePicker.setOnTimeChangedListener((timePicker2, i, i1) -> isNow[0] = false);
            new MaterialAlertDialogBuilder(MainActivity.this, R.style.TimePickerTheme)
                    .setView(view)
                    .setPositiveButton(R.string.time_picker_ok, (dialog, which) -> {
                        setTime(timePicker.getHour(), timePicker.getMinute(), isNow[0]);
                        dialog.dismiss();
                    })
                    .setNeutralButton(R.string.time_picker_cancel, (dialog, which) -> dialog.dismiss())
                    .show();
            MaterialTextView textViewResetHour = view.findViewById(R.id.textViewResetHour);
            textViewResetHour.setOnClickListener(secondView -> {
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone(getString(R.string.time_zone)), Locale.ITALY);
                timePicker.setHour(now.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(now.get(Calendar.MINUTE));
                isNow[0] = true;
            });
        });
        spinnerBusCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!Objects.equals(spinnerArrayAdapter.getItem(position), busStopCode)) {
                    swipeRefreshLayoutOutput.setEnabled(false);
                    outputItemList.clear();
                    fabBus.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        fabBus.setOnClickListener(view -> {
            if (!busStopCode.isEmpty()) {
                if (spinnerBusCode.getSelectedItem().toString().equals(getString(R.string.first_element_spinner))) {
                    busLine = "";
                } else {
                    busLine = spinnerBusCode.getSelectedItem().toString();
                }
                fabBus.hide();
                checkBus(busStopCode, busLine, busHour);
                swipeRefreshLayoutOutput.setEnabled(true);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.toast_no_bus_stop), Toast.LENGTH_SHORT).show();
            }
        });
        adapterFavourites.setOnItemClickListener(position -> {
            clearOutput();
            busStopCode = fav.get(position).getBusStopCode();
            currentBusStopCode = fav.get(position).getBusStopCode();
            spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, busViewer(busStopCode));
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
            spinnerBusCode.setAdapter(spinnerArrayAdapter);
            searchViewBusStopName.setPlaceHolder(fav.get(position).getBusStopName());
            searchViewBusStopName.disableSearch();
            setElementAppBar(true);
            setDisplayChild(1);
            fabBus.show();
        });
        adapterFavourites.setOnFavouriteButtonClickListener(position -> {
            if (favourites.removeFavourite(MainActivity.this, fav.get(position).getBusStopCode())) {
                boolean isRemoved = false;
                for (int i = 0; i < stops.size() && !isRemoved; i++) {
                    if (fav.get(position).getBusStopCode().equals(stops.get(i).getBusStopCode()) && !updateStarFavourite(stops, i)) {
                        adapterFavourites.notifyItemRemoved(position);
                        fav.remove(position);
                        adapterBusStation.notifyItemChanged(i);
                        isRemoved = true;
                    }
                }
                if (fav.isEmpty()) {
                    recyclerViewFavourites.setVisibility(View.GONE);
                    linearLayoutNoFavourites.setVisibility(View.VISIBLE);
                }
                Toast.makeText(MainActivity.this, R.string.toast_favourite_removed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPreference(preference);
        if (constraintLayoutFavourites.getVisibility() == View.VISIBLE) {
            bottomNavView.setSelectedItemId(R.id.navigation_favourites);
        } else if (viewPagerRss.getVisibility() == View.VISIBLE) {
            bottomNavView.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    public void onBackPressed() {
        if (constraintLayoutSearch.getVisibility() == View.VISIBLE && currentBusStopCode.equals("")) {
            setElementAppBar(false);
            bottomNavView.setVisibility(View.VISIBLE);
            setDisplayChild(0);
            fabBus.hide();
            if (searchViewBusStopName.isSearchEnabled()) {
                searchViewBusStopName.disableSearch();
            }
        } else if (constraintLayoutSearch.getVisibility() == View.VISIBLE && !currentBusStopCode.equals("")) {
            setElementAppBar(true);
            bottomNavView.setVisibility(View.VISIBLE);
            setDisplayChild(1);
            fabBus.show();
            if (searchViewBusStopName.isSearchEnabled()) {
                searchViewBusStopName.disableSearch();
            }
        } else if (viewPagerRss.getVisibility() == View.INVISIBLE && currentBusStopCode.equals("")) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            for (int i = permissions.length - 1; i >= 0; --i) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, R.string.toast_permissions, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", getPackageName(), null)));
                }
            }
        }
    }

    private void initLanguage() {
        if (preference.setPreferenceLanguage()) {
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        } else {
            Locale locale = new Locale("it");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }

    private void init() {
        checkPermissions();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        constraintLayoutOutput = findViewById(R.id.constraintLayoutOutput);
        constraintLayoutSearch = findViewById(R.id.constraintLayoutSearch);
        constraintLayoutFavourites = findViewById(R.id.constraintLayoutFavourites);
        linearLayoutHour = findViewById(R.id.linearLayoutHour);
        linearLayoutBusCode = findViewById(R.id.linearLayoutBusCode);
        linearLayoutNoFavourites = findViewById(R.id.linearLayoutNoFavourites);
        bottomNavView = findViewById(R.id.bottomNavView);
        spinnerBusCode = findViewById(R.id.spinnerBusCode);
        materialTextViewBusHour = findViewById(R.id.materialTextViewBusHour);
        fabBus = findViewById(R.id.fabBus);
        swipeRefreshLayoutOutput = findViewById(R.id.swipeRefreshLayoutOutput);
        searchViewBusStopName = findViewById(R.id.searchViewBusStopName);
        progressBarOutput = findViewById(R.id.progressBarOutput);
        bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPagerRss = findViewById(R.id.viewPagerRss);
        tabsRss = findViewById(R.id.tabsRss);
        recyclerViewBusStation = findViewById(R.id.recyclerViewBusStation);
        recyclerViewBusGps = findViewById(R.id.recyclerViewBusGps);
        recyclerViewFavourites = findViewById(R.id.recyclerViewFavourites);
    }

    private void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        for (String permission : REQUIRED_SDK_PERMISSIONS) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            requestPermissions(missingPermissions.toArray(new String[missingPermissions.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS, grantResults);
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

    private void setSwipeRefreshLayoutOutput() {
        swipeRefreshLayoutOutput.setOnRefreshListener(MainActivity.this);
        swipeRefreshLayoutOutput.setEnabled(false);
        swipeRefreshLayoutOutput.setProgressBackgroundColorSchemeResource(R.color.colorPrimary);
        swipeRefreshLayoutOutput.setColorSchemeResources(R.color.colorAccent);
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

    private void setVisibilityRecycler(boolean isGps) {
        if (!isGps) {
            recyclerViewBusStation.setVisibility(View.VISIBLE);
            recyclerViewBusGps.setVisibility(View.GONE);
        } else {
            recyclerViewBusStation.setVisibility(View.GONE);
            recyclerViewBusGps.setVisibility(View.VISIBLE);
        }
    }

    private void initPreference(@NotNull Preferences preference) {
        doubleBackToExitPressedOnce = preference.setPreferenceExit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = (@NonNull MenuItem item) -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                setElementAppBar(false);
                setVisibilityRecycler(false);
                if (searchViewBusStopName.isSearchEnabled()) {
                    searchViewBusStopName.disableSearch();
                }
                setDisplayChild(0);
                fabBus.hide();
                bottomNavView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
                return true;
            case R.id.navigation_favourites:
                setElementAppBar(false);
                setVisibilityRecycler(false);
                if (searchViewBusStopName.isSearchEnabled()) {
                    searchViewBusStopName.disableSearch();
                }
                setDisplayChild(3);
                fabBus.hide();
                bottomNavView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
                return true;
            case R.id.navigation_settings:
                setVisibilityRecycler(false);
                if (searchViewBusStopName.isSearchEnabled()) {
                    searchViewBusStopName.disableSearch();
                }
                bottomNavView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
                startActivity(new Intent(MainActivity.this, PreferencesActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                return true;
        }
        return false;
    };

    private void buildRecyclerViewSearch(List<SearchItem> stops, boolean isGps) {
        setVisibilityRecycler(isGps);
        if (isGps) {
            recyclerViewBusGps.setHasFixedSize(true);
            recyclerViewBusGps.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            adapterBusGps = new SearchAdapter(stops);
            recyclerViewBusGps.setAdapter(adapterBusGps);
            adapterBusGps.notifyDataSetChanged();
        } else {
            recyclerViewBusStation.setHasFixedSize(true);
            recyclerViewBusStation.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            adapterBusStation = new SearchAdapter(stops);
            recyclerViewBusStation.setAdapter(adapterBusStation);
            adapterBusStation.notifyDataSetChanged();
        }
    }

    private void buildRecyclerViewFavourites() {
        recyclerViewFavourites.setHasFixedSize(true);
        recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapterFavourites = new FavouritesAdapter(fav);
        recyclerViewFavourites.setAdapter(adapterFavourites);
        adapterFavourites.notifyDataSetChanged();
    }

    private void clearOutput() {
        if (!outputItemList.isEmpty()) {
            outputItemList.clear();
            if (adapterOutput != null) {
                adapterOutput.notifyDataSetChanged();
            } else if (adapterOutputError != null) {
                adapterOutputError.notifyDataSetChanged();
            } else {
                adapterOutputError.notifyDataSetChanged();
                adapterOutput.notifyDataSetChanged();
            }
        }
    }

    private boolean getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setMessage(R.string.dialog_gps_message)
                    .setPositiveButton(R.string.dialog_gps_yes, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton(R.string.dialog_generic_no, (dialog, which) -> dialog.cancel())
                    .show();
            return false;
        } else {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
            } else {
                Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (locationGps != null) {
                    latitude = locationGps.getLatitude();
                    longitude = locationGps.getLongitude();
                    return true;
                } else if (locationNetwork != null) {
                    latitude = locationNetwork.getLatitude();
                    longitude = locationNetwork.getLongitude();
                    return true;
                } else if (locationPassive != null) {
                    latitude = locationPassive.getLatitude();
                    longitude = locationPassive.getLongitude();
                    return true;
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_gps_error, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return false;
        }
    }

    private ArrayList<String> busViewer(String busStopCodeIn) {
        ArrayList<String> bus = new ArrayList<>();
        bus.add(0, getString(R.string.first_element_spinner));
        for (int i = 0; i < busClass.size(); i++) {
            String busStopCode = busClass.get(i).getBusStopCode();
            if (busStopCode.equals(busStopCodeIn)) {
                bus.add(busClass.get(i).getBusCode());
                currentBusStopCode = busClass.get(i).getBusStopCode();
            }
        }
        return bus;
    }

    private void itemAdapterMethod(@NotNull List<SearchItem> listStops, int position) {
        if (!currentBusStopCode.equals(listStops.get(position).getBusStopName())) {
            clearOutput();
            busStopCode = listStops.get(position).getBusStopCode();
            currentBusStopCode = listStops.get(position).getBusStopCode();
            spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, busViewer(busStopCode));
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
            spinnerBusCode.setAdapter(spinnerArrayAdapter);
            searchViewBusStopName.setPlaceHolder(listStops.get(position).getBusStopName());
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
    }

    private boolean updateStarFavourite(@NotNull List<SearchItem> stopsTemp, int position) {
        if (stopsTemp.get(position).getImageFavourite() == R.drawable.star_border) {
            stopsTemp.get(position).setImageFavourite(R.drawable.star);
            return true;
        } else {
            stopsTemp.get(position).setImageFavourite(R.drawable.star_border);
            return false;
        }
    }

    private void setTime(int hour, int minute, boolean isNow) {
        if (!isNow) {
            if (minute < 10) {
                materialTextViewBusHour.setText(getString(R.string.busHourText, hour, ("0" + minute)));
                busHour = hour + "0" + minute;
            } else {
                materialTextViewBusHour.setText(getString(R.string.busHourText, hour, ("" + minute)));
                busHour = hour + "" + minute;
            }
        } else {
            materialTextViewBusHour.setText(R.string.busHourTextNow);
            busHour = "";
        }
        outputItemList.clear();
        fabBus.show();
    }

    @Override
    public void onRefresh() {
        checkBus(busStopCode, busLine, busHour);
        Toast.makeText(MainActivity.this, R.string.toast_update_output, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processStart() {
        if (CheckInternet.isNetworkAvailable(MainActivity.this)) {
            if (outputItemList.isEmpty()) {
                progressBarOutput.setVisibility(View.VISIBLE);
            } else {
                outputItemList.clear();
            }
        } else {
            Toast.makeText(MainActivity.this, R.string.network, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void processFinish(@NotNull List<OutputItem> output) {
        RecyclerView recyclerViewBusOutput = findViewById(R.id.recyclerViewBusOutput);
        recyclerViewBusOutput.setHasFixedSize(true);
        recyclerViewBusOutput.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        try {
            if ("".equals(output.get(0).getError())) {
                adapterOutput = new OutputAdapter(MainActivity.this, outputItemList);
                if ("".equals(busHour)) {
                    String diffTime;
                    Calendar now = Calendar.getInstance(TimeZone.getTimeZone(getString(R.string.time_zone)), Locale.ITALY);
                    for (int i = 1; i < output.size(); i++) {
                        StringTokenizer token = new StringTokenizer(output.get(i).getBusHour(), ":");
                        int diffHour = Integer.parseInt(token.nextToken()) - now.get(Calendar.HOUR_OF_DAY);
                        int diffMin = Integer.parseInt(token.nextToken()) - now.get(Calendar.MINUTE);
                        if (diffHour == 0 || diffHour < 0) {
                            if (diffMin < 2) {
                                diffTime = getString(R.string.busHourOutputLive);
                            } else {
                                diffTime = diffMin + getString(R.string.busHourOutputMin);
                            }
                        } else if (diffMin < 0) {
                            diffTime = 60 + diffMin + getString(R.string.busHourOutputMin);
                        } else {
                            diffTime = diffHour + getString(R.string.busHourOutputHour) + " " + diffMin + getString(R.string.busHourOutputMin);
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
                adapterOutputError = new OutputErrorAdapter(MainActivity.this, outputItemList);
                recyclerViewBusOutput.setAdapter(adapterOutputError);
                adapterOutputError.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.logError(MainActivity.this, e);
        }
        swipeRefreshLayoutOutput.setRefreshing(false);
        progressBarOutput.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void checkBus(String busStop, String busLine, String busHour) {
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            UrlElaboration ue = new UrlElaboration(busStop, busLine, busHour, MainActivity.this, MainActivity.this);
            ue.execute();
        } catch (Exception e) {
            Log.logError(MainActivity.this, e);
        }
    }
}