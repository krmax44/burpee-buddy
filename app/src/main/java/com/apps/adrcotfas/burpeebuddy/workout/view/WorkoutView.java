package com.apps.adrcotfas.burpeebuddy.workout.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;

public interface WorkoutView extends ObservableView<WorkoutView.Listener> {

    public interface Listener {
        void onStopButtonClicked();
        void onFinishSetButtonClicked();
    }

    public void onRepComplete();
    public void onTimerTick(int seconds);

    public void setFinishSetButtonVisibility(int visibility);

    public void onStartBreak();
    public void toggleRowAppearance(boolean isWorkingOut);

    public void onWorkoutFinished();
}
