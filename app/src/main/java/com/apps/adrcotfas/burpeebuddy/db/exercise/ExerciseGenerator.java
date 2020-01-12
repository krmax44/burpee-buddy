package com.apps.adrcotfas.burpeebuddy.db.exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseGenerator {

    public static List<Exercise> getDefaultWorkouts() {
        List<Exercise> workouts = new ArrayList<>();
        workouts.add(new Exercise("burpees", ExerciseType.COUNTABLE));
        workouts.add(new Exercise("push-ups", ExerciseType.COUNTABLE));
        workouts.add(new Exercise("plank", ExerciseType.TIME_BASED));
        workouts.add(new Exercise("pull-ups", ExerciseType.UNCOUNTABLE));
        workouts.add(new Exercise("dips", ExerciseType.UNCOUNTABLE));
        workouts.add(new Exercise("squats", ExerciseType.UNCOUNTABLE));
        workouts.add(new Exercise("kettlebell swings", ExerciseType.UNCOUNTABLE));
        workouts.add(new Exercise("dumbbell curl", ExerciseType.UNCOUNTABLE));
        workouts.add(new Exercise("single-unders", ExerciseType.UNCOUNTABLE));
        workouts.add(new Exercise("double-unders", ExerciseType.UNCOUNTABLE));

        return workouts;
    }
}
