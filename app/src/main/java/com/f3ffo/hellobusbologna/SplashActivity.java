package com.f3ffo.hellobusbologna;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.f3ffo.hellobusbologna.hellobus.DownloadCsvAndroidM;
import com.f3ffo.hellobusbologna.hellobus.DownloadCsvAndroidO;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            new DownloadCsvAndroidO(SplashActivity.this).execute();
        } else {
            new DownloadCsvAndroidM(SplashActivity.this).execute();
        }
        //TODO if for wait the result of AsyncTask
        MainActivity.br.extractFromFile(SplashActivity.this);
        MainActivity.br.stopsViewer();
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}