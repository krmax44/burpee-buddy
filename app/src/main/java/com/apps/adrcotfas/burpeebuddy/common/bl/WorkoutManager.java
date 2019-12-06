package com.apps.adrcotfas.burpeebuddy.common.bl;

import androidx.lifecycle.MutableLiveData;

public class WorkoutManager implements RepCounter.Listener{

    private final MutableLiveData<Long> mReps = new MutableLiveData<>();

    public WorkoutManager() {
        resetReps();
    }

    public MutableLiveData<Long> getReps() {
        return mReps;
    }

    public void resetReps() {
        mReps.setValue(0L);
    }

    @Override
    public void onRepCompleted() {
        mReps.setValue(mReps.getValue() + 1);
    }
}
