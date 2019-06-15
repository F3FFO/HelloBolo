package com.f3ffo.hellobusbologna;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.f3ffo.hellobusbologna.asyncInterface.AsyncResponseVersion;
import com.f3ffo.hellobusbologna.hellobus.CheckVersion;
import com.f3ffo.hellobusbologna.hellobus.DownloadCsv;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity implements AsyncResponseVersion {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isOnline()) {
            try {
                new CheckVersion(SplashActivity.this, SplashActivity.this).execute().get();
            } catch (Exception e) {
                Log.e("ERROR SplashActivity: ", e.getMessage());
                e.printStackTrace();
            }
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            new AlertDialog.Builder(SplashActivity.this)
                    .setTitle(R.string.alertDialog_title)
                    .setMessage(R.string.alertDialog_message)
                    .setPositiveButton(R.string.alertDialog_yes, (DialogInterface dialog, int which) -> {
                        finish();
                        startActivity(new Intent(SplashActivity.this, SplashActivity.class));
                    })
                    .setNegativeButton(R.string.alertDialog_no, (DialogInterface dialog, int which) -> finish())
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    private boolean isOnline() {
        //Check if connection exist
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && isConnected();
    }

    private boolean isConnected() {
        try {
            return Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8").waitFor() < 2;
        } catch (InterruptedException e) {
            Log.e("ERROR SplashActivity: ", e.getMessage());
            return false;
        } catch (IOException e) {
            Log.e("ERROR SplashActivity: ", e.getMessage());
            return false;
        }
    }

    @Override
    public void processFinisVersion(String version) {
        try {
            new DownloadCsv(SplashActivity.this, version).execute().get();
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
        }
        MainActivity.br.extractFromFile(SplashActivity.this);
        MainActivity.br.stopsViewer(SplashActivity.this);
    }
}