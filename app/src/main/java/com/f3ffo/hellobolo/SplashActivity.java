package com.f3ffo.hellobolo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.f3ffo.hellobolo.asyncInterface.AsyncResponseVersion;
import com.f3ffo.hellobolo.hellobus.CheckVersion;
import com.f3ffo.hellobolo.hellobus.DownloadCsv;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SplashActivity extends AppCompatActivity implements AsyncResponseVersion {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            new MaterialAlertDialogBuilder(SplashActivity.this)
                    .setTitle(R.string.alertDialog_title)
                    .setMessage(R.string.alertDialog_message)
                    .setPositiveButton(R.string.alertDialog_yes, (DialogInterface dialog, int which) -> {
                        finish();
                        startActivity(new Intent(SplashActivity.this, SplashActivity.class));
                    })
                    .setNegativeButton(R.string.alertDialog_no, (DialogInterface dialog, int which) -> finish())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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