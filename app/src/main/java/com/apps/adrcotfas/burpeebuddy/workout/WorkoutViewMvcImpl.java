package com.apps.adrcotfas.burpeebuddy.workout;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BaseViewMvc;

import java.util.ArrayList;
import java.util.List;

public class WorkoutViewMvcImpl extends BaseViewMvc implements WorkoutViewMvc{

    private final List<WorkoutViewMvc.Listener> mListeners = new ArrayList<>();

    public WorkoutViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_workout, parent, false));

        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(v -> onStopButtonClicked());
    }

    private void onStopButtonClicked() {
        for (Listener listener : mListeners) {
            listener.onStopButtonClicked();
        }
    }

    @Override
    public void registerListener(Listener listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterListener(Listener listener) {
        mListeners.remove(listener);
    }
}
