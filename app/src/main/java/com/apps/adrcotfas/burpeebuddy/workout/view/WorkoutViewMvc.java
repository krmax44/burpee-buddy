package com.apps.adrcotfas.burpeebuddy.workout.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;

public interface WorkoutViewMvc extends ObservableViewMvc<WorkoutViewMvc.Listener> {

    public interface Listener {
        void onStopButtonClicked();
        void onFinishSetButtonClicked();
    }

    public void updateCounter(int reps);
    public void updateTimer(int seconds);

    public void onStartWorkout();
    public void onStartBreak();
}
