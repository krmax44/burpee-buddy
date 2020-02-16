package com.apps.adrcotfas.burpeebuddy.statistics.view;

import com.apps.adrcotfas.burpeebuddy.common.ActionModeHelper;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import java.util.List;

public interface StatisticsView extends ObservableViewMvc<StatisticsView.Listener> {

    void bindWorkouts(List<Workout> workouts);
    void destroyActionMode();

    public interface Listener {
        void startActionMode(ActionModeHelper actionModeHelper);
        void onDeleteSelected(List<Integer> selectedEntriesIds);
        void onEditSelected(Workout selectedWorkout);
    }
}
