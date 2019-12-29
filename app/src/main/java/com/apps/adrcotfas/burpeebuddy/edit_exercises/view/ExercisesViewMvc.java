package com.apps.adrcotfas.burpeebuddy.edit_exercises.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;

import java.util.List;

public interface ExercisesViewMvc extends ObservableViewMvc<ExercisesViewMvc.Listener> {

    void bindExercises(List<Exercise> exercises);

    public interface Listener {

        void onExerciseAddClicked();

        void onExerciseEditClicked(Exercise exercise);

        void onVisibilityToggle(String exercise, boolean visibility);
        void onExercisesRearranged(List<Exercise> exercises);
    }
}
