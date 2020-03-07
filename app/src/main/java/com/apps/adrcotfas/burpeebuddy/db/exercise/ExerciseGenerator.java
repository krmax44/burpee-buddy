package com.apps.adrcotfas.burpeebuddy.db.exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseGenerator {

    public static List<Exercise> getDefaultWorkouts() {
        List<Exercise> workouts = new ArrayList<>();
        workouts.add(new Exercise("burpees", ExerciseType.COUNTABLE, false));
        workouts.add(new Exercise("push-ups", ExerciseType.COUNTABLE, false));
        workouts.add(new Exercise("plank", ExerciseType.TIME_BASED, false));
        workouts.add(new Exercise("sit-ups", ExerciseType.UNCOUNTABLE, false));
        workouts.add(new Exercise("pull-ups", ExerciseType.UNCOUNTABLE, false));
        workouts.add(new Exercise("dips", ExerciseType.UNCOUNTABLE, false));
        workouts.add(new Exercise("squats", ExerciseType.UNCOUNTABLE, false));
        workouts.add(new Exercise("kettlebell swings", ExerciseType.UNCOUNTABLE, false));
        workouts.add(new Exercise("dumbbell curls", ExerciseType.UNCOUNTABLE, false));
        workouts.add(new Exercise("double-unders", ExerciseType.UNCOUNTABLE, false));

        return workouts;
    }
}
