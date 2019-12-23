package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.content.Context;

import com.apps.adrcotfas.burpeebuddy.common.timers.PreWorkoutTimer;
import com.apps.adrcotfas.burpeebuddy.common.timers.Timer;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.workout.InProgressWorkout;
import com.apps.adrcotfas.burpeebuddy.workout.State;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

public class WorkoutManager implements RepCounter.Listener{
    private static final String TAG = "WorkoutManager";

    private InProgressWorkout mWorkout;
    private PreWorkoutTimer mPreWorkoutTimer;
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

        int crtReps = mWorkout.crtSetReps.getValue();
        int crtSet = mWorkout.crtSet.getValue();

        if (crtReps < mWorkout.goal.getReps()) {
            Timber.tag(TAG).d("rep finished " + mWorkout.crtSetReps.getValue() + "/" + mWorkout.goal.getReps());
            EventBus.getDefault().post(new Events.RepComplete(crtReps));
        } else if (crtSet < mWorkout.goal.getSets()) {
            mWorkout.crtSetReps.setValue(0);
            mWorkout.crtSet.setValue(crtSet + 1);
            Timber.tag(TAG).d("set finished " + mWorkout.crtSet.getValue() + "/" + mWorkout.goal.getSets());
            mTimer.stop();
            EventBus.getDefault().post(new Events.RepComplete(crtReps, true));
            EventBus.getDefault().post(new Events.SetComplete());
        } else {
            Timber.tag(TAG).d("Workout finished");
            mWorkout.state = State.FINISHED;
            EventBus.getDefault().post(new Events.RepComplete(crtReps, true));
            EventBus.getDefault().post(new Events.FinishedWorkoutEvent());
        }

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

    public void init(ExerciseType type, Goal goal) {
        mWorkout.type = type;
        mWorkout.goal = goal;
    }

    public void start() {
        //TODO: register only if relevant
        getRepCounter().register(this);
        skipFirstRep = true;
        mTimer.start();
    }

    public void reset() {
        mWorkout.reset();
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

    public void startPreWorkoutTimer(long millis) {
        mPreWorkoutTimer = new PreWorkoutTimer(millis);
        mPreWorkoutTimer.start();
    }

    public PreWorkoutTimer getPreWorkoutTimer() {
        return mPreWorkoutTimer;
    }
}
