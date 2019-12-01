package com.f3ffo.hellobusbologna.preference;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

import com.f3ffo.hellobusbologna.R;

/*public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_toolbar);
        setSupportActionBar(findViewById(R.id.materialToolbarPreference));
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayoutPreference, new PreferencesFragment())
                    .commit();
        }
    }

    public static class PreferencesFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //String theme = Preferences.syncTheme(getPreferenceManager(), "list_preference_theme");
            Preferences.sync(getPreferenceManager());
            /*if (theme.equals("1")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }*/
        /*}

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference, rootKey);
        }
    }
}*/