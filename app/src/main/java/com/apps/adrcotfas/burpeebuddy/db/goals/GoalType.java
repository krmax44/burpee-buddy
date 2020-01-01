package com.apps.adrcotfas.burpeebuddy.db.goals;

public enum GoalType {

    INVALID(-1),
    TIME(0), // 3 x 1 min or 3 x AMRAP 3 min
    REPS(1); // 4 x 15 reps

    public int getValue() {
        return value;
    }

    private final int value;

    GoalType(int value) {
        this.value = value;
    }
}
