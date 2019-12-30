package com.f3ffo.hellobolo.preference;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.f3ffo.hellobolo.R;

import org.apache.commons.io.FileUtils;

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
        setSupportActionBar(findViewById(R.id.materialToolbarPreference));
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static class PreferencesFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preference);
            ListPreference listPreference = findPreference(getString(R.string.preference_key_theme));
            listPreference.setOnPreferenceChangeListener((androidx.preference.Preference preference, Object newValue) -> {
                //getActivity().recreate();
                //getActivity().startActivity(new Intent(getContext(), PreferencesActivity.class).addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                Toast.makeText(getContext(), getString(R.string.toast_theme), Toast.LENGTH_LONG).show();
                return true;
            });
            androidx.preference.Preference clearMemory = getPreferenceManager().findPreference(getString(R.string.preference_key_clear_memory));
            if (clearMemory != null) {
                clearMemory.setOnPreferenceClickListener((androidx.preference.Preference preference) -> {
                    try {
                        FileUtils.deleteDirectory(getContext().getFilesDir());
                        Toast.makeText(getContext(), getString(R.string.toast_file_deleted), Toast.LENGTH_LONG).show();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), getString(R.string.toast_no_file_deleted), Toast.LENGTH_LONG).show();
                        return false;
                    }
                });
            }
            /*androidx.preference.Preference button = getPreferenceManager().findPreference(getString(R.string.preference_key_gps));
            if (button != null) {
                button.setOnPreferenceClickListener((androidx.preference.Preference preference) -> {
                    LinearLayoutCompat layout = new LinearLayoutCompat(getContext());
                    layout.setOrientation(LinearLayoutCompat.VERTICAL);
                    layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                    Slider slider = new Slider(getContext());
                    LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                    lparams.setMargins(128, 16, 128, 16);
                    slider.setLayoutParams(lparams);
                    slider.setValueFrom(0);
                    slider.setValueTo(2);
                    slider.setStepSize(1);
                    layout.addView(slider);
                    new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme)
                            .setTitle("Range")
                            .setView(layout)
                            .setNegativeButton(R.string.alertDialog_gps_no, (DialogInterface dialog, int which) -> dialog.dismiss())
                            .setPositiveButton(R.string.alertDialog_gps_yes, (DialogInterface dialog, int which) -> new Preference(getContext()).setPreferenceGps(slider.getValue()))
                            .show();
                    return false;
                });
            }*/
        }
    }
}