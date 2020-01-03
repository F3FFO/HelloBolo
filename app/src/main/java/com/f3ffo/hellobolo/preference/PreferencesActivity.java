package com.f3ffo.hellobolo.preference;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.preference.PreferenceFragmentCompat;

import com.f3ffo.hellobolo.FullScreenDialog;
import com.f3ffo.hellobolo.Log;
import com.f3ffo.hellobolo.MainActivity;
import com.f3ffo.hellobolo.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            getString(R.string.preference_summary_theme_29, new Preference(PreferencesActivity.this).setPreferenceTheme());
        } else {
            getString(R.string.preference_summary_theme, new Preference(PreferencesActivity.this).setPreferenceTheme());
        }
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
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutPreference, new PreferencesFragment()).commit();
        }
        AppCompatImageButton imageButtonPreferenceDonation = findViewById(R.id.imageButtonPreferenceDonation);
        AppCompatImageButton imageButtonPreferenceContribute = findViewById(R.id.imageButtonPreferenceContribute);
        imageButtonPreferenceDonation.setOnClickListener((View view) -> {
            Uri uri = Uri.parse("https://www.paypal.me/f3ff0");
            PreferencesActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        });
        imageButtonPreferenceContribute.setOnClickListener((View view) -> {
            Uri uri = Uri.parse("https://github.com/F3FFO/HelloBolo");
            PreferencesActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public static class PreferencesFragment extends PreferenceFragmentCompat {

        private boolean reload() {
            startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            getActivity().finish();
            return true;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preference);
            findPreference(getString(R.string.preference_key_theme)).setOnPreferenceChangeListener((androidx.preference.Preference preference, Object newValue) -> reload());
            findPreference(getString(R.string.preference_key_language)).setOnPreferenceChangeListener((androidx.preference.Preference preference, Object newValue) -> reload());
            findPreference(getString(R.string.preference_key_open_log)).setOnPreferenceClickListener((androidx.preference.Preference preference) -> {
                FullScreenDialog.display(getActivity().getSupportFragmentManager());
                return true;
            });
            findPreference(getString(R.string.preference_key_delete_log)).setOnPreferenceClickListener((androidx.preference.Preference preference) -> {
                new MaterialAlertDialogBuilder(getContext(), R.style.DialogTheme)
                        .setTitle(R.string.dialog_delete_log_title)
                        .setNegativeButton(R.string.dialog_generic_no, (DialogInterface dialog, int which) -> dialog.dismiss())
                        .setPositiveButton(R.string.dialog_delete_log_yes, (DialogInterface dialog, int which) -> {
                            FileUtils.deleteQuietly(new File(getContext().getFilesDir(), Log.LOG_FILENAME));
                            Toast.makeText(getContext(), getString(R.string.toast_log_file_deleted), Toast.LENGTH_LONG).show();
                        })
                        .show();
                return true;
            });
            findPreference(getString(R.string.preference_key_clear_memory)).setOnPreferenceClickListener((androidx.preference.Preference preference) -> {
                new MaterialAlertDialogBuilder(getContext(), R.style.DialogTheme)
                        .setTitle(R.string.dialog_clear_mem_title)
                        .setMessage(R.string.dialog_clear_mem_warning)
                        .setNegativeButton(R.string.dialog_generic_no, (DialogInterface dialog, int which) -> dialog.dismiss())
                        .setPositiveButton(R.string.dialog_clear_mem_yes, (DialogInterface dialog, int which) -> {
                            try {
                                FileUtils.deleteDirectory(getContext().getFilesDir());
                                getActivity().finish();
                            } catch (IOException e) {
                                Log.logFile(getContext(), e);
                                Toast.makeText(getContext(), getString(R.string.toast_no_files_deleted), Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
                return true;
            });
            /*findPreference(getString(R.string.preference_key_gps)).setOnPreferenceClickListener(preference -> {
                LinearLayoutCompat layout = new LinearLayoutCompat(getContext());
                layout.setOrientation(LinearLayoutCompat.VERTICAL);
                layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                Slider slider = new Slider(getContext());
                LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(128, 16, 128, 16);
                slider.setLayoutParams(layoutParams);
                slider.setBackgroundColor(getContext().getColor(R.color.colorAccent));
                slider.setValueFrom(-500);
                slider.setValueTo(500);
                slider.setStepSize(250);
                slider.setValue(0);
                layout.addView(slider);
                new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme)
                        .setTitle("Range")
                        .setView(layout)
                        .setNegativeButton(R.string.alertDialog_gps_no, (DialogInterface dialog, int which) -> dialog.dismiss())
                        .setPositiveButton(R.string.alertDialog_gps_yes, (DialogInterface dialog, int which) -> new Preference(getContext()).setPreferenceGps(slider.getValue()))
                        .show();
                return false;
            });*/
        }
    }
}