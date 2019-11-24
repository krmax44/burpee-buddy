package com.apps.adrcotfas.burpeebuddy.workout;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BaseObservableViewMvc;

public class WorkoutViewMvcImpl extends BaseObservableViewMvc<WorkoutViewMvc.Listener>
        implements WorkoutViewMvc{

    public WorkoutViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_workout, parent, false));

        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(v -> onStopButtonClicked());
    }

    private void onStopButtonClicked() {
        for (Listener listener : getListeners()) {
            listener.onStopButtonClicked();
        }
    }
}
