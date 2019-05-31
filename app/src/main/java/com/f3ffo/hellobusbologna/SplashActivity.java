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

import com.f3ffo.hellobusbologna.hellobus.DownloadCsvAndroidM;
import com.f3ffo.hellobusbologna.hellobus.DownloadCsvAndroidO;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isOnline()) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    new DownloadCsvAndroidO(SplashActivity.this).execute().get();
                } else {
                    new DownloadCsvAndroidM(SplashActivity.this).execute().get();
                }
            } catch (Exception e) {
                Log.e("ERROR SplashActivity: ", e.getMessage());
            }
            MainActivity.br.extractFromFile(SplashActivity.this);
            MainActivity.br.stopsViewer();
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
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}