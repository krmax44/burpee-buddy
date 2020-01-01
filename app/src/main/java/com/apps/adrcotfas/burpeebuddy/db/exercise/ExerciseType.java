package com.apps.adrcotfas.burpeebuddy.db.exercise;

public enum ExerciseType {

    INVALID(-1),
    TIME_BASED(0), // plank, hold etc (can have TIME goal type)
    REP_BASED_COUNTABLE(1),  // push-ups and burpees (can have REPS and AMRAP goal types)
    REP_BASED(2);  // pull-ups, dips, squats (can have AMRAP goal type)

    public int getValue() {
        return value;
    }

    private int value;
    ExerciseType(int value) {
        this.value = value;
    }

}
