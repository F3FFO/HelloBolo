package com.f3ffo.hellobusbologna.preference;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.f3ffo.hellobusbologna.R;

public class PreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
    }
}
