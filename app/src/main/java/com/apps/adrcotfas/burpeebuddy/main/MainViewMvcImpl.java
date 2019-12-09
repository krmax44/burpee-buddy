package com.apps.adrcotfas.burpeebuddy.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.google.android.material.chip.Chip;

public class MainViewMvcImpl extends BaseObservableViewMvc<MainViewMvc.Listener>
        implements MainViewMvc {

    public MainViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_main, parent, false));

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> onStartButtonClicked());
        Chip plank = findViewById(R.id.plank);
        plank.setOnClickListener(v -> onDisabledChipClicked());
        Chip reps = findViewById(R.id.reps);
        reps.setOnClickListener(v -> onDisabledChipClicked());
        Chip time = findViewById(R.id.time);
        time.setOnClickListener(v -> onDisabledChipClicked());
    }

    public void onStartButtonClicked() {
        for (Listener listener : getListeners()) {
            listener.onStartButtonClicked();
        }
    }

    public void onDisabledChipClicked() {
        for (Listener listener : getListeners()) {
            listener.onDisabledChipClicked();
        }
    }
}
