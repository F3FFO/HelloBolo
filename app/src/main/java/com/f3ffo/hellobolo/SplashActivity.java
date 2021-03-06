package com.f3ffo.hellobolo;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.f3ffo.hellobolo.asyncInterface.AsyncResponseVersion;
import com.f3ffo.hellobolo.hellobus.CheckVersion;
import com.f3ffo.hellobolo.hellobus.DownloadCsv;
import com.f3ffo.hellobolo.utility.CheckInternet;

public class SplashActivity extends AppCompatActivity implements AsyncResponseVersion {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        init();
    }

    @Override
    public void processFinisVersion(String version) {
        try {
            new DownloadCsv(SplashActivity.this, version).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (CheckInternet.isNetworkAvailable(SplashActivity.this)) {
            try {
                new CheckVersion(SplashActivity.this, SplashActivity.this).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(SplashActivity.this, R.string.network, Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
    }
}