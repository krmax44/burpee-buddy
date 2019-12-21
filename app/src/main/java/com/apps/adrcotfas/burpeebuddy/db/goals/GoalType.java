package com.apps.adrcotfas.burpeebuddy.db.goals;

public enum GoalType {

    INVALID(-1),
    TIME_BASED(0), // plank
    REP_BASED(1),  // only for countable exercises
    AMRAP(2);      // a variation of time based, can be used for REP_BASED and COUNTABLE exercises

    public int getValue() {
        return value;
    }

    private final int value;

    GoalType(int value) {
        this.value = value;
    }
}
