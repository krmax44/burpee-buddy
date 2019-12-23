package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.content.Context;
import android.util.Log;

import com.apps.adrcotfas.burpeebuddy.common.timers.Timer;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.workout.InProgressWorkout;
import com.apps.adrcotfas.burpeebuddy.workout.State;

import org.greenrobot.eventbus.EventBus;

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

        // REP_based stuff bellow
        int crtReps = mWorkout.crtSetReps.getValue();
        int crtSet = mWorkout.crtSet.getValue();

        if (crtReps < mWorkout.goal.getReps()) {
            mWorkout.crtSetReps.setValue(crtReps + 1);
            mWorkout.totalReps.setValue(mWorkout.totalReps.getValue() + 1);
        } else if (crtSet < mWorkout.goal.getSets()) {
            mWorkout.crtSetReps.setValue(1);
            mWorkout.totalReps.setValue(mWorkout.totalReps.getValue() + 1);
            mWorkout.crtSet.setValue(crtSet + 1);
            //TODO: notification, trigger break(unregister RepCounter), update fragment etc
        }

        if (mWorkout.crtSet.getValue() == mWorkout.goal.getSets()
                && mWorkout.crtSetReps.getValue() == mWorkout.goal.getReps()) {
            mWorkout.state = State.FINISHED;
            EventBus.getDefault().post(new Events.FinishedWorkoutEvent());
        }

        Log.v("WTF", "| set: " + mWorkout.crtSet.getValue() + "| reps: " + mWorkout.crtSetReps.getValue()
                + "| total: " + mWorkout.totalReps.getValue());

        /**
         * TIME based:
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
        //TODO: register only if relevant
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
