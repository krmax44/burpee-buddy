package com.apps.adrcotfas.burpeebuddy.db.exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseFactory {

    public final static String PUSHUPS = "push-ups";
    public final static String BURPEES = "burpees";
    public final static String PLANK   = "plank";
    public final static String PULLUPS = "pull-ups";
    public final static String DIPS    = "dips";
    public final static String SQUATS  = "squats";

    public static List<Exercise> getDefaultWorkouts() {
        List<Exercise> workouts = new ArrayList<>();
        workouts.add(new Exercise(PUSHUPS, ExerciseType.COUNTABLE));
        workouts.add(new Exercise(BURPEES, ExerciseType.COUNTABLE));
        workouts.add(new Exercise(PLANK, ExerciseType.TIME_BASED));
        workouts.add(new Exercise(PULLUPS, ExerciseType.UNCOUNTABLE));
        workouts.add(new Exercise(DIPS, ExerciseType.UNCOUNTABLE));
        workouts.add(new Exercise(SQUATS, ExerciseType.UNCOUNTABLE));

        return workouts;
    }
}
