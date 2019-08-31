package com.f3ffo.hellobusbologna.preference;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.f3ffo.hellobusbologna.R;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_toolbar);
        setSupportActionBar(findViewById(R.id.materialToolbarPreference));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutPreference, new PreferenceFragment())
                .commit();
    }
}
