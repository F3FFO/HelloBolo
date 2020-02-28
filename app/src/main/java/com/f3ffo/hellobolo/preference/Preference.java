package com.f3ffo.hellobolo.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.f3ffo.hellobolo.R;

public class Preference {

    private SharedPreferences sharedPreference;
    private Context context;

    public Preference(Context context) {
        this.context = context;
        this.sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setPreferenceTheme() {
        SharedPreferences.Editor editor = sharedPreference.edit();
        if (sharedPreference.getString(context.getString(R.string.preference_key_theme), "").equals("true")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (sharedPreference.getString(context.getString(R.string.preference_key_theme), "").equals("false")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                editor.putString(context.getString(R.string.preference_key_theme), "auto");
                editor.apply();
                editor.commit();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else {
                editor.putString(context.getString(R.string.preference_key_theme), "false");
                editor.apply();
                editor.commit();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    void setPreferenceGps(float value) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putFloat(context.getString(R.string.preference_key_gps), value);
        editor.apply();
        editor.commit();
    }

    public float getPreferenceGps() {
        return sharedPreference.getFloat(context.getString(R.string.preference_key_gps), 0);
    }

    public boolean setPreferenceLanguage() {
        return sharedPreference.getString(context.getString(R.string.preference_key_language), "it").equals("en");
    }

    public boolean setPreferenceExit() {
        return !sharedPreference.getBoolean(context.getString(R.string.preference_key_exit), true);
    }
}