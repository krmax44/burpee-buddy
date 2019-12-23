package com.apps.adrcotfas.burpeebuddy.common.bl;

import android.content.Context;

import com.apps.adrcotfas.burpeebuddy.common.timers.CountDownTimer;
import com.apps.adrcotfas.burpeebuddy.common.timers.Timer;
import com.apps.adrcotfas.burpeebuddy.common.timers.TimerType;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.apps.adrcotfas.burpeebuddy.workout.InProgressWorkout;
import com.apps.adrcotfas.burpeebuddy.workout.State;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType.COUNTABLE;
import static com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType.INVALID;
import static com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType.REP_BASED;
import static com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType.TIME_BASED;

public class WorkoutManager implements RepCounter.Listener, CountDownTimer.Listener{
    private static final String TAG = "WorkoutManager";

    private InProgressWorkout mWorkout;
    private Timer mTimer;
    private CountDownTimer mCountDownTimer;
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

        if (getGoalType().equals(GoalType.REP_BASED)) {
            if (crtReps < mWorkout.goal.getReps()) {
                Timber.tag(TAG).d("rep finished " + mWorkout.crtSetReps.getValue() + "/" + mWorkout.goal.getReps());
                EventBus.getDefault().post(new Events.RepComplete(crtReps));
            } else if (crtSet < mWorkout.goal.getSets()) {
                getRepCounter().unregister();
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
        } else if (getGoalType().equals(GoalType.AMRAP)) {
            Timber.tag(TAG).d("rep finished " + mWorkout.crtSetReps.getValue());
            EventBus.getDefault().post(new Events.RepComplete(crtReps));
        }
    }

    public void init(ExerciseType type, Goal goal) {
        if (type.equals(INVALID)) {
            throw new IllegalArgumentException("Invalid exercise type");
        }
        mWorkout.type = type;
        mWorkout.goal = goal;
    }

    public void start() {
        // push-ups and burpees
        if (mWorkout.type.equals(COUNTABLE)) {
            getRepCounter().register(this);
            skipFirstRep = true;
            if (getGoalType().equals(GoalType.REP_BASED)) {
                mTimer.start();
            } else if (getGoalType().equals(GoalType.AMRAP)) {
                mCountDownTimer = new CountDownTimer(
                        TimerType.COUNT_DOWN,
                        TimeUnit.SECONDS.toMillis(mWorkout.goal.getDuration()),
                        this);
                mCountDownTimer.start();
            }
        } else if (mWorkout.type.equals(TIME_BASED) && getGoalType().equals(GoalType.TIME_BASED)) {
            mCountDownTimer = new CountDownTimer(
                    TimerType.COUNT_DOWN,
                    TimeUnit.SECONDS.toMillis(mWorkout.goal.getDuration()),
                    this);
            mCountDownTimer.start();
        } else if (mWorkout.type.equals(REP_BASED) && getGoalType().equals(GoalType.AMRAP)) {
                // countdown for pull-ups with dialog to enter current reps
        }
    }

    private GoalType getGoalType() {
        return mWorkout.goal.getType();
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
        mCountDownTimer = new CountDownTimer(TimerType.PRE_WORKOUT_COUNT_DOWN, millis);
        mCountDownTimer.start();
    }

    public CountDownTimer getCountDownTimer() {
        return mCountDownTimer;
    }

    @Override
    public void onFinishedAmrapSet() {
        getWorkout().crtSet.setValue(getWorkout().crtSet.getValue() + 1);
        if (getWorkout().crtSet.getValue() <= getWorkout().goal.getSets()) {
            BuddyApplication.getWorkoutManager().getRepCounter().unregister();
            EventBus.getDefault().post(new Events.SetComplete());
        } else {
            EventBus.getDefault().post(new Events.FinishedWorkoutEvent());
        }
    }
}
