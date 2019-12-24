package com.f3ffo.hellobolo.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.f3ffo.hellobolo.R;

public class Preference {

    public void getPreferenceTheme(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPrefs.edit();
        if (mPrefs.getString(context.getString(R.string.preference_key_theme), "").equals("true")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (mPrefs.getString(context.getString(R.string.preference_key_theme), "").equals("false")) {
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
}