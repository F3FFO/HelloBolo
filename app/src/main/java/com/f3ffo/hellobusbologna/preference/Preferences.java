package com.f3ffo.hellobusbologna.preference;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import java.util.Map;

/*public class Preferences {

    private static void sync(PreferenceManager preferenceManager, String key) {
        Preference pref = preferenceManager.findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }

    public static String syncTheme(PreferenceManager preferenceManager, String key) {
        Preference pref = preferenceManager.findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
            return listPref.getValue();
        } else {
            return "0";
        }
    }

    public static void sync(PreferenceManager preferenceManager) {
        Map<String, ?> map = preferenceManager.getSharedPreferences().getAll();
        for (String key : map.keySet()) {
            sync(preferenceManager, key);
        }
    }
}*/
