package com.apps.adrcotfas.burpeebuddy.common.bl;

import com.apps.adrcotfas.burpeebuddy.common.timers.Timer;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.workout.InProgressWorkout;

public class WorkoutManager implements RepCounter.Listener{

    private InProgressWorkout mWorkout = new InProgressWorkout();
    private Timer mTimer;

    /**
     * A workaround to skip the first rep because it's not a real, just the sensor changing state.
     */
    private boolean skipFirstRep;

    public WorkoutManager() {
        mTimer = new Timer();
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
        mWorkout.type = type;
        mWorkout.goal = goal;
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
}
