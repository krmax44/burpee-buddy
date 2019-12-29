package com.apps.adrcotfas.burpeebuddy.workout.manager;

import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;

public class InProgressWorkout {

    public State state = State.INACTIVE;

    public ExerciseType type;
    public Goal goal;

    public int crtSet = 1;

    public int crtSetReps = 0;
    public int totalReps = 0;

    public int crtDuration = 0;
    public int totalDuration = 0;

    public InProgressWorkout() {
        reset();
    }

    public void reset() {
        this.type = ExerciseType.INVALID;
        this.goal = new Goal(GoalType.INVALID, 0, 0, 0, 0);
        crtSet = 1;
        crtSetReps = 0;
        totalReps = 0;
        crtDuration = 0;
        totalDuration = 0;
    }
}
