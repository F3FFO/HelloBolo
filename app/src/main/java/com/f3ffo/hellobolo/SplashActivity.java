package com.f3ffo.hellobolo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.f3ffo.hellobolo.asyncInterface.AsyncResponseVersion;
import com.f3ffo.hellobolo.hellobus.CheckVersion;
import com.f3ffo.hellobolo.hellobus.DownloadCsv;
import com.f3ffo.hellobolo.preference.Preference;

public class SplashActivity extends AppCompatActivity implements AsyncResponseVersion {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new Preference().getPreferenceTheme(SplashActivity.this);
        super.onCreate(savedInstanceState);
        if (isNetworkAvailable()) {
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void processFinisVersion(String version) {
        try {
            new DownloadCsv(SplashActivity.this, version).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}