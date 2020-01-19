package com.apps.adrcotfas.burpeebuddy.statistics.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import java.util.List;

public interface StatisticsViewMvc extends ObservableViewMvc<StatisticsViewMvc.Listener> {

    void bindWorkouts(List<Workout> workouts);
    public interface Listener {
        void onWorkoutLongPress(int id);
    }
}
