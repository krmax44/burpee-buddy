package com.apps.adrcotfas.burpeebuddy.common.bl;

import com.apps.adrcotfas.burpeebuddy.db.Workout;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

public class WorkoutManager implements RepCounter.Listener{

    private Workout mWorkout = new Workout();

    /**
     * A workaround to skip the first rep because it's not a real, just the sensor changing state.
     */
    private boolean skipFirstRep;

    public Workout getWorkout() {
        return mWorkout;
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

        mWorkout.reps.add((int)TimeUnit.MILLISECONDS.toSeconds(
                System.currentTimeMillis() - mWorkout.timestamp));
        mWorkout.timestamp = System.currentTimeMillis();
        EventBus.getDefault().post(new Events.RepCompletedEvent(mWorkout.reps.size()));
    }

    public void setDuration(long value) {
        mWorkout.duration = value;
    }

    public void start() {
        skipFirstRep = true;
        mWorkout.timestamp = System.currentTimeMillis();
    }
}
