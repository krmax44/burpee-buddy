package com.apps.adrcotfas.burpeebuddy.statistics.view;


import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

public interface StatisticsItemView extends ObservableViewMvc<StatisticsItemView.Listener> {

    public interface Listener {
    }

    void bindWorkout(Workout workout, boolean selected);
}
