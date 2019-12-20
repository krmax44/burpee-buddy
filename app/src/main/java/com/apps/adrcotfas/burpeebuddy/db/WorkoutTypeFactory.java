package com.apps.adrcotfas.burpeebuddy.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkoutTypeFactory {

    public final static String PUSHUPS = "push-ups";
    public final static String BURPEES = "burpees";
    public final static String PLANK   = "plank";
    public final static String PULLUPS = "pull-ups";
    public final static String DIPS    = "dips";
    public final static String SQUATS  = "squats";

    public static List<WorkoutType> getDefaultWorkouts() {
        List<WorkoutType> workouts = new ArrayList<>();
        workouts.add(new WorkoutType(PUSHUPS,
                Arrays.asList(Metrics.REPS, Metrics.TIME, Metrics.PROXIMITY_SENSOR_RELEVANT)));

        workouts.add(new WorkoutType(BURPEES,
                Arrays.asList(Metrics.REPS, Metrics.TIME, Metrics.PROXIMITY_SENSOR_RELEVANT)));

        workouts.add(new WorkoutType(PLANK,
                Arrays.asList(Metrics.TIME)));

        workouts.add(new WorkoutType(PULLUPS,
                Arrays.asList(Metrics.REPS)));

        workouts.add(new WorkoutType(DIPS,
                Arrays.asList(Metrics.REPS)));

        workouts.add(new WorkoutType(SQUATS,
                Arrays.asList(Metrics.REPS)));

        return workouts;
    }
}
