package com.apps.adrcotfas.burpeebuddy.workout.manager;

import android.content.Context;

import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.common.timers.CountDownTimer;
import com.apps.adrcotfas.burpeebuddy.common.timers.Timer;
import com.apps.adrcotfas.burpeebuddy.common.timers.TimerType;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.COUNTABLE;
import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.INVALID;

public class WorkoutManager implements RepCounter.Listener, CountDownTimer.Listener{
    private static final String TAG = "WorkoutManager";

    private InProgressWorkout mWorkout;
    private Timer mTimer;
    private CountDownTimer mCountDownTimer;
    // used for pausing and resuming the CountDownTimer
    private int mElapsed;
    private RepCounter mRepCounter;

    public WorkoutManager(Context context) {
        mTimer = new Timer();
        mRepCounter = new RepCounter(context);
        mWorkout = new InProgressWorkout();
        mCountDownTimer = new CountDownTimer(TimerType.COUNT_DOWN, 0);
    }

    public void init(Exercise exercise, Goal goal) {
        if (exercise.type.equals(INVALID)) {
            throw new IllegalArgumentException("Invalid exercise type");
        }
        getWorkout().init(exercise, goal);
    }

    public void start() {
        mWorkout.setState(State.ACTIVE);

        if (getExerciseType().equals(COUNTABLE)) {
            getRepCounter().register(this);
        }

        if (getGoalType().equals(GoalType.REPS)) {
            mTimer.start(0);
        } else if (getGoalType().equals(GoalType.TIME)) {
            mCountDownTimer = new CountDownTimer(
                    TimerType.COUNT_DOWN,
                    TimeUnit.SECONDS.toMillis(getWorkout().getGoalDuration()),
                    this);
            mCountDownTimer.start();
        }
    }

    private ExerciseType getExerciseType() {
        return getWorkout().getExerciseType();
    }

    private GoalType getGoalType() {
        return getWorkout().getGoalType();
    }

    public void stop() {
        getWorkout().setState(State.INACTIVE);
        getRepCounter().unregister();
        getCountDownTimer().cancel();
        getTimer().stop();
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

    /**
     * This is relevant only for exercises which can be measured with the proximity sensor
     * ExerciseType == COUNTABLE
     */
    @Override
    public void onFinishedRep() {
        getWorkout().incrementCurrentReps();

        Timber.tag(TAG).d("Rep (%s / %s) | Set (%s / %s)",
                getWorkout().getCurrentReps(), getWorkout().getGoalReps(),
                getWorkout().getCurrentSet() + 1, getWorkout().getGoalSets());

        if (getGoalType() == GoalType.TIME) {
            EventBus.getDefault().post(new Events.RepComplete());
            return;
        }

        if (getWorkout().isSetFinished()) {
            EventBus.getDefault().post(new Events.RepComplete(true));

            getRepCounter().unregister();
            getWorkout().setCurrentDuration(mTimer.elapsedSeconds);

            mTimer.stop();

            if (getWorkout().isFinalSet()) {
                Timber.tag(TAG).v("Workout finished");
                getWorkout().setState(State.WORKOUT_FINISHED);
                EventBus.getDefault().post(new Events.FinishedWorkoutEvent());
            } else {
                if (SettingsHelper.autoStartBreak(getExerciseType())) {
                    getWorkout().incrementCurrentSet();
                    EventBus.getDefault().post(new Events.SetFinished(true));
                    EventBus.getDefault().post(new Events.StartBreak(getWorkout().getGoalDurationBreak()));
                } else {
                    getWorkout().setState(State.SET_FINISHED);
                    EventBus.getDefault().post(new Events.SetFinished());
                }
            }
        } else {
            EventBus.getDefault().post(new Events.RepComplete());
        }
    }

    /**
     * This is called when:
     * - a set of an AMRAP workout is finished (COUNTABLE / UNCOUNTABLE + TIME as goal)
     * - a set of a UNCOUNTABLE + REPS as goal workout was finished (manually triggered by the user)
     */
    @Override
    public void onFinishedSet() {
        Timber.tag(TAG).v("onFinishedSet %s / %s, hashcode: %s",
                getWorkout().getCurrentSet(), getWorkout().getGoalSets(), this.hashCode());

        if (getExerciseType() == COUNTABLE) {
            getRepCounter().unregister();
        }

        if (getGoalType() == GoalType.TIME) {
            // Rep(countable or not) based exercises with TIME as goal (AMRAP)
            // The countdown was finished
            getWorkout().setCurrentDuration(getWorkout().getGoalDuration());
        } else if (getGoalType() == GoalType.REPS) {
            // Rep based exercises with REPS as goal
            // The user stated that he achieved the rep goal
            getWorkout().setCurrentReps(getWorkout().getGoalReps());
            getWorkout().setCurrentDuration(mTimer.elapsedSeconds);
            mTimer.stop();
        }

        if (getWorkout().isFinalSet()) {
            getWorkout().setState(State.WORKOUT_FINISHED);
            EventBus.getDefault().post(new Events.FinishedWorkoutEvent());
        } else {
            if (SettingsHelper.autoStartBreak(getExerciseType())) {
                getWorkout().incrementCurrentSet();
                getWorkout().setState(State.BREAK_ACTIVE);
                EventBus.getDefault().post(new Events.SetFinished(true));
                EventBus.getDefault().post(new Events.StartBreak(getWorkout().getGoalDurationBreak()));
            } else {
                getWorkout().setState(State.SET_FINISHED);
                EventBus.getDefault().post(new Events.SetFinished());
            }
        }
    }

    /**
     * Called when the user tries to stop a workout.
     * This will pause and un-pause the workout.
     */
    public void toggle() {
        if (getWorkout().getState() == State.BREAK_ACTIVE) {
            mElapsed = mCountDownTimer.seconds;
            mCountDownTimer.cancel();
            getWorkout().setState(State.BREAK_PAUSED);
            return;
        } else if (getWorkout().getState() == State.BREAK_PAUSED) {
            getWorkout().setState(State.BREAK_ACTIVE);
            EventBus.getDefault().post(new Events.StartBreak(mElapsed));
            return;
        }

        final boolean active = getWorkout().getState() == State.ACTIVE;

        getWorkout().setState(active ? State.PAUSED : State.ACTIVE);

        if (getExerciseType().equals(COUNTABLE)) {
            if (active) {
                getRepCounter().unregister();
            } else {
                getRepCounter().register(this);
            }
        }

        if (getGoalType().equals(GoalType.TIME)) {
            if (active) {
                mElapsed = mCountDownTimer.seconds;
                mCountDownTimer.cancel();
            } else {
                mCountDownTimer = new CountDownTimer(TimerType.COUNT_DOWN, TimeUnit.SECONDS.toMillis(mElapsed), this);
                mCountDownTimer.start();
            }
        } else if (getGoalType().equals(GoalType.REPS)){
            if (active) {
                mElapsed = mTimer.elapsedSeconds;
                mTimer.toggle();
            } else {
                mTimer.start(mElapsed);
            }
        }
    }

    public int getElapsedTime() {
        if (getWorkout().getState() == State.BREAK_PAUSED
                || getWorkout().getState() == State.BREAK_ACTIVE) {
            return getCountDownTimer().seconds;
        }
        if (getWorkout().getGoalType() == GoalType.REPS) {
            return getTimer().elapsedSeconds;
        } else { // TIME
            return getWorkout().getGoal().duration - getCountDownTimer().seconds;
        }
    }
}
