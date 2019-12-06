package com.apps.adrcotfas.burpeebuddy.db;

import java.util.ArrayList;

public class Workout {

    /**
     * The type of workout which can be rep-based like burpees
     * or push-ups or time based like plank.
     */
    public WorkoutType type;

    /**
     * A multi-purpose field representing the time in milliseconds
     * at the start of the workout which is updated on each rep
     * and updated one last time at the end to signal the time of completion.
     */
    public long timestamp;

    /**
     * The number of seconds spent in this workout.
     */
    public long duration;
    /**
     * A list of integers representing the seconds between reps and the number of reps.
     */
    public ArrayList<Integer> reps = new ArrayList<>();

    //TODO: later think about auto-pause when combining with other exercises
}
