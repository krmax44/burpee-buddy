package com.apps.adrcotfas.burpeebuddy.main.edit_exercises.item;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;

public interface ExercisesItemViewMvc extends ObservableViewMvc<ExercisesItemViewMvc.Listener> {

    public interface Listener {
        void onExerciseEdit(String exercise, Exercise newExercise);
        void onVisibilityToggle(String exercise, boolean visible);
    }

    void bindExercise(Exercise exercise);
}
