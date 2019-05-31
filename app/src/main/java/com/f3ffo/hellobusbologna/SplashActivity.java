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
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
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