package com.f3ffo.hellobolo.preference;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.f3ffo.hellobolo.MainActivity;
import com.f3ffo.hellobolo.R;
import com.f3ffo.hellobolo.fullScreenDialog.LicensesDialog;
import com.f3ffo.hellobolo.fullScreenDialog.LogDialog;
import com.f3ffo.hellobolo.hellobus.BusReader;
import com.f3ffo.hellobolo.utility.Log;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PreferencesActivity extends AppCompatActivity {

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
        BottomNavigationView bottomNavView = findViewById(R.id.bottomNavViewPreference);
        bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        MaterialCardView materialCardViewInfo = findViewById(R.id.materialCardViewInfo);
        materialCardViewInfo.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).addCategory(Intent.CATEGORY_DEFAULT).setData(Uri.parse("package:" + getPackageName()))));
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutPreference, new PreferencesFragment()).commit();
        }
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
            findPreference(getString(R.string.preference_key_theme)).setOnPreferenceChangeListener((preference, newValue) -> reload());
            findPreference(getString(R.string.preference_key_language)).setOnPreferenceChangeListener((preference, newValue) -> reload());
            findPreference(getString(R.string.preference_key_open_log)).setOnPreferenceClickListener(preference -> {
                LogDialog.display(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                return true;
            });
            findPreference(getString(R.string.preference_key_delete_log)).setOnPreferenceClickListener(preference -> {
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
            findPreference(getString(R.string.preference_key_clear_memory)).setOnPreferenceClickListener(preference -> {
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
                float[] tempDistance = {new Preferences(getContext()).getPreferenceGps()};
                MaterialTextView attention = new MaterialTextView(getContext());
                LinearLayoutCompat.LayoutParams layoutParamsTextView = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                layoutParamsTextView.setMargins(64, 8, 64, 16);
                attention.setVisibility(View.GONE);
                attention.setLayoutParams(layoutParamsTextView);
                attention.setTextAppearance(R.style.TextAppearance_MaterialComponents_Caption);
                attention.setTextIsSelectable(false);
                attention.setText(getString(R.string.preference_slider_gps_text_high));
                setTextTextView(tempDistance[0], attention);
                Slider slider = new Slider(getContext());
                LinearLayoutCompat.LayoutParams layoutParamsSlider = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                layoutParamsSlider.setMargins(128, 16, 128, 8);
                slider.setLayoutParams(layoutParamsSlider);
                slider.setValueFrom(0);
                slider.setValueTo(1000);
                slider.setStepSize(250);
                slider.setValue(tempDistance[0]);
                int[][] states = new int[][]{new int[]{android.R.attr.state_enabled}};
                int[] colors = new int[]{Color.BLUE};
                ColorStateList colorStateList = new ColorStateList(states, colors);
                slider.setTickColor(colorStateList);
                slider.setTrackColor(colorStateList);
                slider.setHaloColor(colorStateList);
                slider.setThumbColor(colorStateList);
                slider.setTrackHeight(5);
                slider.addOnChangeListener((slider1, value, fromUser) -> {
                    tempDistance[0] = value;
                    setTextTextView(value, attention);
                });
                layout.addView(slider);
                layout.addView(attention);
                new MaterialAlertDialogBuilder(getContext(), R.style.DialogTheme)
                        .setTitle(getString(R.string.preference_title_gps))
                        .setView(layout)
                        .setNegativeButton(R.string.dialog_generic_no, (DialogInterface dialog, int which) -> dialog.dismiss())
                        .setPositiveButton(R.string.dialog_preference_gps_yes, (DialogInterface dialog, int which) -> {
                            BusReader.setDistance(tempDistance[0]);
                            new Preferences(getContext()).setPreferenceGps(slider.getValue());
                        })
                        .show();
                return false;
            });
            findPreference(getString(R.string.preference_key_license)).setOnPreferenceClickListener(preference -> {
                LicensesDialog.display(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                return true;
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