package com.f3ffo.hellobolo.fullScreenDialog;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.f3ffo.hellobolo.R;
import com.f3ffo.hellobolo.utility.Log;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class LogDialog extends DialogFragment {

    private static final String TAG = "dialog_log";

    public static void display(FragmentManager fragmentManager) {
        LogDialog logDialog = new LogDialog();
        logDialog.show(fragmentManager, TAG);
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
        return inflater.inflate(R.layout.dialog_log, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialToolbar materialToolbarDialogFullScreen = view.findViewById(R.id.materialToolbarDialogLog);
        materialToolbarDialogFullScreen.setNavigationOnClickListener(v -> dismiss());
        materialToolbarDialogFullScreen.setTitle(Log.LOG_FILENAME);
        MaterialTextView materialTextViewDialogFullScreen = view.findViewById(R.id.materialTextViewDialogLog);
        materialTextViewDialogFullScreen.setHorizontallyScrolling(true);
        materialTextViewDialogFullScreen.setMovementMethod(new ScrollingMovementMethod());
        List<String> file = Log.getLog(view.getContext());
        for (int i = 0; i < file.size(); i++) {
            materialTextViewDialogFullScreen.append(file.get(i) + "\n");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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