package com.f3ffo.hellobolo;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;

public class FullScreenDialog extends DialogFragment {

    private static final String TAG = "dialog_full_screen";
    private MaterialToolbar materialToolbarDialogFullScreen;

    public static FullScreenDialog display(FragmentManager fragmentManager) {
        FullScreenDialog fullScreenDialog = new FullScreenDialog();
        fullScreenDialog.show(fragmentManager, TAG);
        return fullScreenDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFullScreenTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_full_screen, container, false);
        materialToolbarDialogFullScreen = view.findViewById(R.id.materialToolbarDialogFullScreen);
        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        materialToolbarDialogFullScreen.setNavigationOnClickListener(v -> dismiss());
        materialToolbarDialogFullScreen.setTitle(Log.LOG_FILENAME);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_NO:
                    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        dialog.getWindow().getDecorView().setSystemUiVisibility(0);
                    }
                    break;
            }
        }
    }
}