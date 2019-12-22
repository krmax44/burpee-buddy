package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.content.Context;

import com.apps.adrcotfas.burpeebuddy.common.timers.Timer;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.workout.InProgressWorkout;

public class WorkoutManager implements RepCounter.Listener{

    private InProgressWorkout mWorkout;
    private Timer mTimer;
    private RepCounter mRepCounter;

    /**
     * A workaround to skip the first rep because it's not a real, just the sensor changing state.
     */
    private boolean skipFirstRep;

    public WorkoutManager(Context context) {
        mTimer = new Timer();
        mRepCounter = new RepCounter(context);
        mWorkout = new InProgressWorkout();
    }

    @Override
    public void onRepCompleted() {
        if (skipFirstRep) {
            skipFirstRep = false;
            return;
        }
        mWorkout.crtSetReps.setValue(mWorkout.crtSetReps.getValue() + 1);
        mWorkout.totalReps.setValue(mWorkout.totalReps.getValue() + 1);

        /**
         * REP based:
         *
         *  if (reps < goal.reps)
         *     ++reps
         *  else if (crtSet < goal.sets)
         *     ++crtSet
         *              -> notification, trigger break, update fragment etc
         *  else
         *      show finished dialog
         *
         * TIME based:
         * TODO: move timer here
         * if (duration < goal.duration)
         *  - update duration
         *  else if (crtSet < goal.sets)
         *      ++crtSet -> notification, break, update fragment etc
         *  else
         *      show finished dialog
         *
         *  AMRAP_C:
         *  - like time but with counting of reps
         *
         *  AMRAP:
         *  - like time but with manually entering the reps in a dialog
         */
    }

    public void start(ExerciseType type, Goal goal) {
        getRepCounter().register(this);
        mWorkout.type = type;
        mWorkout.goal = goal;
        skipFirstRep = true;
        mTimer.start();
    }

    public void reset() {
        mWorkout.reset();
        getRepCounter().unregister();
    }

    public InProgressWorkout getWorkout() {
        return mWorkout;
    }

    public Timer getTimer() {
        return mTimer;
    }

    public RepCounter getRepCounter() {
        return mRepCounter;
    }
}
