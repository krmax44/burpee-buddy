package com.apps.adrcotfas.burpeebuddy.workout;

import androidx.lifecycle.MutableLiveData;

import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;

public class InProgressWorkout {

    public State state = State.INACTIVE;

    public ExerciseType type;
    public Goal goal;

    public MutableLiveData<Integer> crtSet = new MutableLiveData<>(1);

    public MutableLiveData<Integer> crtSetReps = new MutableLiveData<>(0);
    public MutableLiveData<Integer> totalReps = new MutableLiveData<>(0);

    public MutableLiveData<Integer> crtDuration = new MutableLiveData<>(0);
    public MutableLiveData<Integer> totalDuration = new MutableLiveData<>(0);

    public InProgressWorkout() {
        reset();
    }

    public void reset() {
        this.type = ExerciseType.INVALID;
        this.goal = new Goal(GoalType.INVALID, 0, 0, 0, 0);
        crtSet.setValue(1);
        crtSetReps.setValue(0);
        totalReps.setValue(0);
        crtDuration.setValue(0);
        totalDuration.setValue(0);
    }
}
