package com.apps.adrcotfas.burpeebuddy.common.bl;

import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;

import org.greenrobot.eventbus.EventBus;

public class WorkoutManager implements RepCounter.Listener{

    private Workout mWorkout = new Workout();

    /**
     * A workaround to skip the first rep because it's not a real, just the sensor changing state.
     */
    private boolean skipFirstRep;

    public Workout getWorkout() {
        return mWorkout;
    }

    public int getNumberOfReps() {
        return mWorkout.reps;
    }

    public void resetWorkout() {
        mWorkout = new Workout();
    }

    @Override
    public void onRepCompleted() {
        //TODO adapt for paused workouts

        if (skipFirstRep) {
            skipFirstRep = false;
            return;
        }

        ++mWorkout.reps;
        mWorkout.timestamp = System.currentTimeMillis();
        EventBus.getDefault().post(new Events.RepCompletedEvent(mWorkout.reps));
    }

    public void setDuration(int value) {
        mWorkout.duration = value;
    }

    public void start() {
        skipFirstRep = true;
        mWorkout.timestamp = System.currentTimeMillis();
    }
}
