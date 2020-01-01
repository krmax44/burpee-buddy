package com.apps.adrcotfas.burpeebuddy.workout.manager;

import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;

import java.util.ArrayList;
import java.util.List;

public class InProgressWorkout {

    public State state = State.INACTIVE;
    public Exercise exercise;
    public Goal goal;

    public int crtSetIdx;
    public int totalReps;
    public int totalDuration;

    public List<Integer> reps;
    public List<Integer> durations;

    public InProgressWorkout() {
        reset();
    }

    public void reset() {
        state = State.INACTIVE;
        exercise = new Exercise();
        goal = new Goal(GoalType.INVALID, 0, 0, 0, 0);
        crtSetIdx = 0;
        totalReps = 0;
        totalDuration = 0;
        durations = new ArrayList<>();
        reps = new ArrayList<>();
    }

    public void init(Exercise exercise, Goal goal) {
        this.state = State.PRE_WORKOUT;
        this.exercise = exercise;
        this.goal = goal;
        crtSetIdx = 0;
        reps = new ArrayList<>();
        totalReps = 0;
        durations = new ArrayList<>();
        totalDuration = 0;

        for (int i = 0; i < goal.sets; ++i) {
            reps.add(i, 0);
        }

        for (int i = 0; i < goal.sets; ++i) {
            durations.add(i, 0);
        }
    }
}
