package com.apps.adrcotfas.burpeebuddy.workout;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;

public interface WorkoutViewMvc extends ObservableViewMvc<WorkoutViewMvc.Listener> {

    public interface Listener {
        void onStopButtonClicked();
    }

    public void updateCounter(long value);
    public void updateTimer(long value);
    void toggleTimerVisibility();
}
