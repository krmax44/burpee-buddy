package com.apps.adrcotfas.burpeebuddy.workout;

import com.apps.adrcotfas.burpeebuddy.common.ObservableViewMvc;

interface WorkoutViewMvc extends ObservableViewMvc<WorkoutViewMvc.Listener> {

    public interface Listener {
        void onStopButtonClicked();
    }
}