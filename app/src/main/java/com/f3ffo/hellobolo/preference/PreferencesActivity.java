package com.f3ffo.hellobolo.preference;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.preference.PreferenceFragmentCompat;

import com.f3ffo.hellobolo.R;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        new Preference().getPreferenceTheme(PreferencesActivity.this);
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

    public static class PreferencesFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference, rootKey);
        }
    }
}