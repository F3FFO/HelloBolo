package com.f3ffo.hellobusbologna;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import static com.f3ffo.hellobusbologna.MainActivity.br;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        br.extractFromFile(getResources().openRawResource(R.raw.lineefermate_20190501));
        br.stopsViewer();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}