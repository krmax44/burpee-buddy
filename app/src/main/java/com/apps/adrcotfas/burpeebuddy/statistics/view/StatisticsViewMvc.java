package com.apps.adrcotfas.burpeebuddy.statistics.view;

import android.view.ActionMode;

import com.apps.adrcotfas.burpeebuddy.common.ActionModeCallback;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import java.util.List;

public interface StatisticsViewMvc extends ObservableViewMvc<StatisticsViewMvc.Listener> {

    void bindWorkouts(List<Workout> workouts);
    void destroyActionMode();

    public interface Listener {
        ActionMode startActionMode(ActionModeCallback actionModeCallback);
        void onDeleteSelected(List<Integer> selectedEntriesIds);
        void onEditSelected(Workout selectedWorkout);
    }
}
