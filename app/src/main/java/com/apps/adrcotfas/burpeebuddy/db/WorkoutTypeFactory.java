package com.apps.adrcotfas.burpeebuddy.db;

import java.util.Arrays;
import java.util.List;

public class WorkoutTypeFactory {

    public final static String BURPEES = "burpees";
    public final static String PUSHUPS = "push-ups";
    public final static String PLANK = "plank";

    public static WorkoutType getBurpeeWorkout() {
        List<Metrics> metrics = Arrays.asList(Metrics.REPS, Metrics.TIME);
        metrics.add(Metrics.REPS);
        return new WorkoutType(BURPEES, metrics);
    }

    public static WorkoutType getPushUpsWorkout() {
        List<Metrics> metrics = Arrays.asList(Metrics.REPS, Metrics.TIME);
        metrics.add(Metrics.REPS);
        return new WorkoutType(PUSHUPS, metrics);
    }

    public static WorkoutType getPlankUpsWorkout() {
        List<Metrics> metrics = Arrays.asList(Metrics.TIME);
        metrics.add(Metrics.REPS);
        return new WorkoutType(PLANK, metrics);
    }
}
