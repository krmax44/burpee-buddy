package com.apps.adrcotfas.burpeebuddy.db;

import java.util.ArrayList;

public class Workout {
    public WorkoutType type;

    /**
     * Time in milliseconds at the end of the workout
     */
    public long timestamp;

    /**
     * A list of integers representing the seconds between reps and the number of reps
     * This is reused for the PLANK workout type, storing a single integer
     */
    public ArrayList<Integer> reps;

    //TODO: later think about auto-pause when combining with other exercises
}
