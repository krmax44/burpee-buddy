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

    public int crtSet = 1;

    public List<Integer> reps = new ArrayList<>();
    public int totalReps = 0;

    public List<Integer> durations = new ArrayList<>();
    public int totalDuration = 0;

    public InProgressWorkout() {
        reset();
    }

    public void reset() {
        this.exercise = new Exercise();
        this.goal = new Goal(GoalType.INVALID, 0, 0, 0, 0);
        crtSet = 1;
        reps = new ArrayList<>();
        totalReps = 0;
        durations = new ArrayList<>();
        totalDuration = 0;
    }

    public void init(Exercise exercise, Goal goal) {
        this.exercise = exercise;
        this.goal = goal;

        //TODO: maybe fix this in the future / index vs crt set
        for (int i = 0; i <= goal.sets; ++i) {
            reps.add(i, 0);
        }

        for (int i = 0; i <= goal.sets; ++i) {
            durations.add(i, 0);
        }
    }
}
