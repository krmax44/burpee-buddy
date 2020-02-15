package com.apps.adrcotfas.burpeebuddy.statistics.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import java.util.List;

public interface StatisticsViewMvc extends ObservableViewMvc<StatisticsViewMvc.Listener> {

    void bindWorkouts(List<Workout> workouts);

    void selectAllItems();
    void unselectItems();
    List<Integer> getSelectedEntriesIds();

    public interface Listener {
        void startActionMode();
        void updateTitle(String valueOf);
        void finishAction();

        void toggleEditButtonVisibility(boolean visible);
    }
}
