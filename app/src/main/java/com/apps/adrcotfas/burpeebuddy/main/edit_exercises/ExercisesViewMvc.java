package com.apps.adrcotfas.burpeebuddy.main.edit_exercises;

import com.apps.adrcotfas.burpeebuddy.common.viewmvc.ObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;

import java.util.List;

public interface ExercisesViewMvc extends ObservableViewMvc<ExercisesViewMvc.Listener> {

    void bindExercises(List<Exercise> exercises);

    public interface Listener {
        void onAddExercise(Exercise exercise);
        void onExerciseEdit(String exercise, Exercise newExercise);
        void onVisibilityToggle(String exercise, boolean visibility);
        void onExercisesRearranged(List<Exercise> exercises);
    }
}
