package com.apps.adrcotfas.burpeebuddy.edit_exercises.view;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableView;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;

import java.util.List;

public interface ExercisesView extends ObservableView<ExercisesView.Listener> {

    void bindExercises(List<Exercise> exercises);

    public interface Listener {

        void onExerciseEditClicked(Exercise exercise);

        void onVisibilityToggle(String exercise, boolean visibility);
        void onExercisesRearranged(List<Exercise> exercises);
    }
}
