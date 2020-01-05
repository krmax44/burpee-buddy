package com.apps.adrcotfas.burpeebuddy.workout.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;

public interface WorkoutViewMvc extends ObservableViewMvc<WorkoutViewMvc.Listener> {

    public interface Listener {
        void onStopButtonClicked();
        void onFinishSetButtonClicked();
    }

    public void onRepComplete(int reps);
    public void onTimerTick(int seconds);

    public void setFinishSetButtonVisibility(int visibility);

    public void onStartBreak();
    public void onWorkoutFinished();
}
