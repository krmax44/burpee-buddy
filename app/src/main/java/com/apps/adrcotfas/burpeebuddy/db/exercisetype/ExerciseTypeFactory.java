package com.apps.adrcotfas.burpeebuddy.db.exercisetype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseTypeFactory {

    public final static String PUSHUPS = "push-ups";
    public final static String BURPEES = "burpees";
    public final static String PLANK   = "plank";
    public final static String PULLUPS = "pull-ups";
    public final static String DIPS    = "dips";
    public final static String SQUATS  = "squats";

    public static List<ExerciseType> getDefaultWorkouts() {
        List<ExerciseType> workouts = new ArrayList<>();
        workouts.add(new ExerciseType(PUSHUPS,
                Arrays.asList(Metric.REPS, Metric.TIME, Metric.PROXIMITY_SENSOR_RELEVANT)));

        workouts.add(new ExerciseType(BURPEES,
                Arrays.asList(Metric.REPS, Metric.TIME, Metric.PROXIMITY_SENSOR_RELEVANT)));

        workouts.add(new ExerciseType(PLANK,
                Arrays.asList(Metric.TIME)));

        workouts.add(new ExerciseType(PULLUPS,
                Arrays.asList(Metric.REPS)));

        workouts.add(new ExerciseType(DIPS,
                Arrays.asList(Metric.REPS)));

        workouts.add(new ExerciseType(SQUATS,
                Arrays.asList(Metric.REPS)));

        return workouts;
    }
}
