package com.apps.adrcotfas.burpeebuddy.db;

public class Workout {

    public WorkoutType type;

    /**
     * The time of completion.
     */
    public long timestamp;

    /**
     * the number of reps.
     */
    public int reps;

    /**
     * The number of seconds spent in this workout.
     */
    public int duration;

    /**
     * the weight in kg
     */
    public float weight;

    /**
     * the distance in meters
     */
    public float distance;

    //TODO: later think about auto-pause when combining with other exercises
}
