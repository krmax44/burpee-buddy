package com.apps.adrcotfas.burpeebuddy.db.challenge;

import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;

public class Challenge {

    public Challenge(String s) {
        exerciseName = s;
    }

    public int id;
    public ExerciseType type;
    public String exerciseName;

    int repetitions; // per day
    int duration;    // per day

    long start; // the first day
    int days;   // number of days
}
