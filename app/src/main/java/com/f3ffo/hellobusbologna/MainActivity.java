package com.f3ffo.hellobusbologna;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.f3ffo.hellobusbologna.rss.ArticleAdapter;
import com.f3ffo.hellobusbologna.rss.ArticleItem;
import com.f3ffo.hellobusbologna.search.SearchAdapter;
import com.f3ffo.hellobusbologna.search.SearchItem;
import com.f3ffo.hellobusbologna.timePicker.TimePickerFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.lapism.searchview.Search;
import com.lapism.searchview.widget.SearchView;
import com.prof.rssparser.Article;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AsyncResponseUrl, TimePickerDialog.OnTimeSetListener, SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    private ConstraintLayout constraintLayoutRss, constraintLayoutOutput, constraintLayoutSearch, constraintLayoutFavourites;
    private AppCompatTextView busCodeText, textViewHourDefault, textViewBusHour;
    private AppCompatSpinner spinnerBusCode;
    private String busStop = "", busLine = "", busHour = "", currentBusStopName = "";
    private SearchAdapter adapterBusStation;
    private FavouritesAdapter adapterFavourites;
    private FloatingActionButton fabBus;
    private BusReader br = new BusReader();
    private Favourites fv = new Favourites();
    private ProgressBar progressBarOutput;
    private SearchView searchViewBusStopName;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout drawer;
    private List<OutputItem> outputItemList;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private List<FavouritesItem> fav = new ArrayList<>();
    private List<SearchItem> stops = new ArrayList<>();
    private ArrayList<BusClass> busClass = new ArrayList<>();

    private RecyclerView recyclerViewRss;
    private ArticleAdapter articleAdapter;
    private SwipeRefreshLayout swipeRefreshLayoutRss;
    private ProgressBar progressBarRss;
    private ArticleItem viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        constraintLayoutRss = findViewById(R.id.constraintLayoutRss);
        constraintLayoutOutput = findViewById(R.id.constraintLayoutOutput);
        constraintLayoutSearch = findViewById(R.id.constraintLayoutSearch);
        constraintLayoutFavourites = findViewById(R.id.constraintLayoutFavourites);
        spinnerBusCode = findViewById(R.id.spinnerBusCode);
        textViewBusHour = findViewById(R.id.textViewBusHour);
        textViewHourDefault = findViewById(R.id.textViewHourDefault);
        busCodeText = findViewById(R.id.busCodeText);
        fabBus = findViewById(R.id.fabBus);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchViewBusStopName = findViewById(R.id.searchViewBusStopName);
        progressBarOutput = findViewById(R.id.progressBarOutput);
        progressBarRss = findViewById(R.id.progressBarRss);
        recyclerViewRss = findViewById(R.id.recyclerViewRss);
        swipeRefreshLayoutRss = findViewById(R.id.swipeRefreshLayoutRss);
        outputItemList = new ArrayList<>();
        drawer = findViewById(R.id.drawer_layout);
        NavigationView lateralNavView = findViewById(R.id.lateralNavView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        lateralNavView.setNavigationItemSelectedListener(MainActivity.this);
        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark);
        busClass.addAll(br.extractFromFile(MainActivity.this));
        stops.addAll(br.stopsViewer(MainActivity.this, busClass));
        buildRecyclerViewSearch();
        fv.readFile(MainActivity.this);
        fav.addAll(fv.getFavouritesList());
        buildRecyclerViewFavourites();
        viewModel = ViewModelProviders.of(MainActivity.this).get(ArticleItem.class);
        viewModel.fetchFeed();
        recyclerViewRss.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerViewRss.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRss.setHasFixedSize(true);
        viewModel.getArticleList().observe(MainActivity.this, (List<Article> articles) -> {
            if (articles != null) {
                articleAdapter = new ArticleAdapter(articles, MainActivity.this);
                recyclerViewRss.setAdapter(articleAdapter);
                articleAdapter.notifyDataSetChanged();
                progressBarRss.setVisibility(View.GONE);
                swipeRefreshLayoutRss.setRefreshing(false);
            }
        });
        viewModel.getSnackBar().observe(MainActivity.this, (String s) -> {
            if (s != null) {
                Snackbar.make(constraintLayoutRss, s, Snackbar.LENGTH_LONG).show();
                viewModel.onSnackBarShowed();
            }
        });
        swipeRefreshLayoutRss.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayoutRss.canChildScrollUp();
        swipeRefreshLayoutRss.setOnRefreshListener(() -> {
            articleAdapter.getArticleList().clear();
            articleAdapter.notifyDataSetChanged();
            swipeRefreshLayoutRss.setRefreshing(true);
            viewModel.fetchFeed();
        });


        searchViewBusStopName.setOnOpenCloseListener(new Search.OnOpenCloseListener() {

            @Override
            public void onOpen() {
                setElementAppBar(false);
                fabBus.hide();
                setDisplayChild(2);
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
            busStop = stops.get(position).getBusStopCode();
            spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, busViewer(busStop));
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
            spinnerBusCode.setAdapter(spinnerArrayAdapter);
            searchViewBusStopName.setText(stops.get(position).getBusStopName());
            searchViewBusStopName.close();
            setElementAppBar(true);
            setDisplayChild(1);
            fabBus.show();
        });
        adapterBusStation.setOnFavouriteButtonClickListener((int position) -> {
            Favourites favourites = new Favourites();
            if (refreshElement(position)) {
                FavouritesItem item = favourites.addFavourite(MainActivity.this, stops.get(position).getBusStopCode(), stops.get(position).getBusStopName(), stops.get(position).getBusStopAddress());
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
        adapterFavourites.setOnItemClickListener((int position) -> {
            busStop = fav.get(position).getBusStopCode();
            spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_layout, busViewer(busStop));
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_element);
            spinnerBusCode.setAdapter(spinnerArrayAdapter);
            searchViewBusStopName.close();
            setElementAppBar(true);
            setDisplayChild(1);
            fabBus.show();
            searchViewBusStopName.setText(fav.get(position).getBusStopName());
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_search:
                searchViewBusStopName.open(item);
                setElementAppBar(false);
                setDisplayChild(2);
                return true;
            case R.id.navigation_favourites:
                if (searchViewBusStopName.isOpen()) {
                    searchViewBusStopName.close();
                }
                setElementAppBar(false);
                setDisplayChild(3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
            spinnerBusCode.setVisibility(View.VISIBLE);
            textViewHourDefault.setVisibility(View.VISIBLE);
            textViewBusHour.setVisibility(View.VISIBLE);
            busCodeText.setVisibility(View.VISIBLE);
        } else {
            spinnerBusCode.setVisibility(View.GONE);
            textViewHourDefault.setVisibility(View.GONE);
            textViewBusHour.setVisibility(View.GONE);
            busCodeText.setVisibility(View.GONE);
        }
    }

    private void setDisplayChild(int displayChild) {
        if (displayChild == 0) {
            constraintLayoutRss.setVisibility(View.VISIBLE);
            constraintLayoutOutput.setVisibility(View.GONE);
            constraintLayoutSearch.setVisibility(View.GONE);
            constraintLayoutFavourites.setVisibility(View.GONE);
        } else if (displayChild == 1) {
            constraintLayoutRss.setVisibility(View.GONE);
            constraintLayoutOutput.setVisibility(View.VISIBLE);
            constraintLayoutSearch.setVisibility(View.GONE);
            constraintLayoutFavourites.setVisibility(View.GONE);
        } else if (displayChild == 2) {
            constraintLayoutRss.setVisibility(View.GONE);
            constraintLayoutOutput.setVisibility(View.GONE);
            constraintLayoutSearch.setVisibility(View.VISIBLE);
            constraintLayoutFavourites.setVisibility(View.GONE);
        } else if (displayChild == 3) {
            constraintLayoutRss.setVisibility(View.GONE);
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
    }

    private void buildRecyclerViewSearch() {
        RecyclerView recyclerViewBusStation = findViewById(R.id.recyclerViewBusStation);
        recyclerViewBusStation.setHasFixedSize(true);
        recyclerViewBusStation.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapterBusStation = new SearchAdapter(stops);
        recyclerViewBusStation.setAdapter(adapterBusStation);
    }

    private boolean refreshElement(int position) {
        if (stops.get(position).getImageFavourite() == R.drawable.round_favourite_border) {
            stops.get(position).setImageFavourite(R.drawable.ic_star);
            return true;
        } else {
            stops.get(position).setImageFavourite(R.drawable.round_favourite_border);
            return false;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (minute < 10) {
            textViewBusHour.setText(hourOfDay + ":0" + minute);
            busHour = hourOfDay + "0" + minute;
        } else {
            textViewBusHour.setText(hourOfDay + ":" + minute);
            busHour = hourOfDay + "" + minute;
        }
        outputItemList.clear();
        fabBus.show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchViewBusStopName.isOpen() && !busStop.isEmpty()) {
            if (searchViewBusStopName.getText().toString().isEmpty()) {
                searchViewBusStopName.setText(this.currentBusStopName);
            }
            setElementAppBar(true);
            setDisplayChild(1);
        } else if (searchViewBusStopName.isOpen() && busStop.isEmpty()) {
            setElementAppBar(false);
            searchViewBusStopName.setText("");
            setDisplayChild(1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        checkBus(busStop, busLine, busHour);
        Toast.makeText(MainActivity.this, "Aggiornato!", Toast.LENGTH_SHORT).show();
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
            if ("".equals(busHour)) {
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
                String diffTime;
                for (int i = 1; i < output.size(); i++) {
                    StringTokenizer token = new StringTokenizer(output.get(i).getBusHour(), ":");
                    int diffHour = Integer.parseInt(token.nextToken()) - now.get(Calendar.HOUR_OF_DAY);
                    int diffMin = Integer.parseInt(token.nextToken()) - now.get(Calendar.MINUTE);
                    if (diffHour == 0 || diffHour < 0) {
                        if (diffMin < 4) {
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
                    OutputAdapter adapter = new OutputAdapter(MainActivity.this, outputItemList);
                    recyclerViewBusOutput.setAdapter(adapter);
                }
            } else {
                for (int i = 1; i < output.size(); i++) {
                    outputItemList.add(new OutputItem(output.get(i).getBusNumber(), output.get(i).getBusHourComplete(), "", output.get(i).getSatelliteOrHour(), output.get(i).getHandicap()));
                    OutputAdapter adapter = new OutputAdapter(MainActivity.this, outputItemList);
                    recyclerViewBusOutput.setAdapter(adapter);
                }
            }
        } else {
            outputItemList.add(new OutputItem(output.get(0).getError()));
            OutputErrorAdapter adapter = new OutputErrorAdapter(MainActivity.this, outputItemList);
            recyclerViewBusOutput.setAdapter(adapter);
        }
        swipeRefreshLayout.setRefreshing(false);
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
