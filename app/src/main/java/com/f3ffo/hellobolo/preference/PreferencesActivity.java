package com.f3ffo.hellobolo.preference;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.f3ffo.hellobolo.FullScreenDialog;
import com.f3ffo.hellobolo.MainActivity;
import com.f3ffo.hellobolo.R;
import com.f3ffo.hellobolo.hellobus.BusReader;
import com.f3ffo.hellobolo.utility.Log;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PreferencesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.preference_main);
        MaterialToolbar materialToolbar = findViewById(R.id.materialToolbarPreference);
        setSupportActionBar(materialToolbar);
        materialToolbar.setNavigationOnClickListener((View v) -> super.onBackPressed());
        bottomNavView = findViewById(R.id.bottomNavViewPreference);
        bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        AppCompatImageView imageViewAppIconPreferenceSettings = findViewById(R.id.imageViewAppIconPreferenceSettings);
        imageViewAppIconPreferenceSettings.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)));
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutPreference, new PreferencesFragment()).commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = (@NonNull MenuItem item) -> {
        Uri uri;
        switch (item.getItemId()) {
            case R.id.navigation_preference_paypal:
                uri = Uri.parse("https://www.paypal.me/f3ff0");
                PreferencesActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            case R.id.navigation_preference_github:
                uri = Uri.parse("https://github.com/F3FFO/HelloBolo");
                PreferencesActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
        }
        return false;
    };

    public static class PreferencesFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preference);
            findPreference(getString(R.string.preference_key_theme)).setOnPreferenceChangeListener((androidx.preference.Preference preference, Object newValue) -> reload());
            findPreference(getString(R.string.preference_key_language)).setOnPreferenceChangeListener((androidx.preference.Preference preference, Object newValue) -> reload());
            findPreference(getString(R.string.preference_key_open_log)).setOnPreferenceClickListener((androidx.preference.Preference preference) -> {
                FullScreenDialog.display(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                return true;
            });
            findPreference(getString(R.string.preference_key_delete_log)).setOnPreferenceClickListener((androidx.preference.Preference preference) -> {
                new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()), R.style.DialogTheme)
                        .setTitle(R.string.dialog_delete_log_title)
                        .setNegativeButton(R.string.dialog_generic_no, (DialogInterface dialog, int which) -> dialog.dismiss())
                        .setPositiveButton(R.string.dialog_delete_log_yes, (DialogInterface dialog, int which) -> {
                            FileUtils.deleteQuietly(new File(getContext().getFilesDir(), Log.LOG_FILENAME));
                            Log.logInfo(getContext());
                            Toast.makeText(getContext(), getString(R.string.toast_log_file_deleted), Toast.LENGTH_LONG).show();
                        })
                        .show();
                return true;
            });
            findPreference(getString(R.string.preference_key_clear_memory)).setOnPreferenceClickListener((androidx.preference.Preference preference) -> {
                new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()), R.style.DialogTheme)
                        .setTitle(R.string.dialog_clear_mem_title)
                        .setMessage(R.string.dialog_clear_mem_warning)
                        .setNegativeButton(R.string.dialog_generic_no, (DialogInterface dialog, int which) -> dialog.dismiss())
                        .setPositiveButton(R.string.dialog_clear_mem_yes, (DialogInterface dialog, int which) -> {
                            try {
                                FileUtils.deleteDirectory(getContext().getFilesDir());
                                Objects.requireNonNull(getActivity()).finish();
                            } catch (IOException e) {
                                Log.logError(getContext(), e);
                                Toast.makeText(getContext(), getString(R.string.toast_no_files_deleted), Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
                return true;
            });
            findPreference(getString(R.string.preference_key_gps)).setOnPreferenceClickListener(preference -> {
                LinearLayoutCompat layout = new LinearLayoutCompat(Objects.requireNonNull(getContext()));
                layout.setOrientation(LinearLayoutCompat.VERTICAL);
                layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                Slider slider = new Slider(getContext());
                LinearLayoutCompat.LayoutParams layoutParamsSlider = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                layoutParamsSlider.setMargins(128, 16, 128, 8);
                slider.setLayoutParams(layoutParamsSlider);
                slider.setValueFrom(0);
                slider.setValueTo(1000);
                slider.setStepSize(250);
                float[] tempDistance = {new Preference(getContext()).getPreferenceGps()};
                slider.setValue(tempDistance[0]);
                MaterialTextView attention = new MaterialTextView(getContext());
                LinearLayoutCompat.LayoutParams layoutParamsTextView = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                layoutParamsTextView.setMargins(64, 8, 64, 16);
                attention.setVisibility(View.GONE);
                attention.setLayoutParams(layoutParamsTextView);
                attention.setTextAppearance(R.style.TextAppearance_MaterialComponents_Caption);
                attention.setTextIsSelectable(false);
                attention.setText(getString(R.string.preference_slider_gps_text_high));
                setTextTextView(tempDistance[0], attention);
                slider.setOnChangeListener((slider1, value) -> {
                    tempDistance[0] = value;
                    setTextTextView(tempDistance[0], attention);
                });
                layout.addView(slider);
                layout.addView(attention);
                new MaterialAlertDialogBuilder(getContext(), R.style.DialogTheme)
                        .setTitle(getString(R.string.preference_title_gps))
                        .setView(layout)
                        .setNegativeButton(R.string.dialog_generic_no, (DialogInterface dialog, int which) -> dialog.dismiss())
                        .setPositiveButton(R.string.dialog_gps_yes, (DialogInterface dialog, int which) -> {
                            double defaultDistance = BusReader.distance;
                            if (BusReader.distance == defaultDistance && tempDistance[0] == 250) {
                                BusReader.distance += 0.0025;
                            } else if (BusReader.distance == defaultDistance && tempDistance[0] == -250) {
                                BusReader.distance -= 0.0025;
                            } else if (BusReader.distance == defaultDistance && tempDistance[0] == 500) {
                                BusReader.distance += 0.005;
                            } else if (BusReader.distance == defaultDistance && tempDistance[0] == -500) {
                                BusReader.distance -= 0.005;
                            } else {
                                BusReader.distance = 0.001;
                            }
                            new Preference(getContext()).setPreferenceGps(slider.getValue());
                        })
                        .show();
                return false;
            });
        }

        private boolean reload() {
            startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            Objects.requireNonNull(getActivity()).finish();
            return true;
        }

        private void setTextTextView(float distance, MaterialTextView materialTextView) {
            if (distance > 500) {
                materialTextView.setVisibility(View.VISIBLE);
            } else {
                materialTextView.setVisibility(View.GONE);
            }
        }
    }
}