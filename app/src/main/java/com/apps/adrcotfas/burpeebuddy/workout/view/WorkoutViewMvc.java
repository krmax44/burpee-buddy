package com.apps.adrcotfas.burpeebuddy.workout.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;

public interface WorkoutViewMvc extends ObservableViewMvc<WorkoutViewMvc.Listener> {

    public interface Listener {
        void onStopButtonClicked();
    }

    public void updateCounter(int reps);
    public void updateTimer(int seconds);
}
