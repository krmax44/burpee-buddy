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
    }

    /**
     * This is relevant only for exercises which can be measured with the proximity sensor
     * ExerciseType == COUNTABLE
     */
    @Override
    public void onRepCompleted() {
        //TODO: refactoring needed
        getWorkout().reps.set(getWorkout().crtSetIdx, getWorkout().reps.get(getWorkout().crtSetIdx) + 1);
        ++getWorkout().totalReps;

        if (getGoalType() == GoalType.TIME) {

            Timber.tag(TAG).d("rep finished " + getWorkout().reps.get(getWorkout().crtSetIdx));
            EventBus.getDefault().post(new Events.RepComplete(getWorkout().reps.get(getWorkout().crtSetIdx)));

        } else if (getGoalType() == GoalType.REPS) {
            if (getWorkout().reps.get(getWorkout().crtSetIdx) < getWorkout().goal.reps) {
                Timber.tag(TAG).d("rep finished " + getWorkout().reps.get(getWorkout().crtSetIdx) + "/" + getWorkout().goal.reps);
                EventBus.getDefault().post(new Events.RepComplete(getWorkout().reps.get(getWorkout().crtSetIdx)));
            } else if (getWorkout().crtSetIdx + 1 < getWorkout().goal.sets) {
                getRepCounter().unregister();
                getWorkout().durations.set(getWorkout().crtSetIdx, mTimer.elapsedSeconds);
                getWorkout().totalDuration += getWorkout().durations.get(getWorkout().crtSetIdx);
                ++getWorkout().crtSetIdx;

                Timber.tag(TAG).v("set finished " + (getWorkout().crtSetIdx + 1) + "/" + getWorkout().goal.sets);
                mTimer.stop();
                EventBus.getDefault().post(new Events.RepComplete(getWorkout().reps.get(getWorkout().crtSetIdx)));

                if (SettingsHelper.autoStartBreak(getWorkout().exercise.type)) {
                    EventBus.getDefault().post(new Events.StartBreak(getWorkout().goal.duration_break));
                } else {
                    getWorkout().state = State.SET_FINISHED;
                    EventBus.getDefault().post(new Events.SetFinished());
                }

                getWorkout().reps.set(getWorkout().crtSetIdx, 0);
            } else {
                Timber.tag(TAG).d("Workout finished");
                getRepCounter().unregister();
                getWorkout().durations.set(getWorkout().crtSetIdx, mTimer.elapsedSeconds);
                getWorkout().totalDuration += getWorkout().durations.get(getWorkout().crtSetIdx);

                mTimer.stop();
                EventBus.getDefault().post(new Events.RepComplete(getWorkout().reps.get(getWorkout().crtSetIdx)));

                ++getWorkout().crtSetIdx;

                getWorkout().state = State.WORKOUT_FINISHED;
                EventBus.getDefault().post(new Events.FinishedWorkoutEvent());
            }
        }
    }

    public void init(Exercise exercise, Goal goal) {
        if (exercise.type.equals(INVALID)) {
            throw new IllegalArgumentException("Invalid exercise type");
        }
        getWorkout().init(exercise, goal);
    }

    public void start() {
        mWorkout.state = State.ACTIVE;

        if (getExerciseType().equals(COUNTABLE)) {
            getRepCounter().register(this);
        }

        if (getGoalType().equals(GoalType.REPS)) {
            mTimer.start(0);
        } else if (getGoalType().equals(GoalType.TIME)) {
            mCountDownTimer = new CountDownTimer(
                    TimerType.COUNT_DOWN,
                    TimeUnit.SECONDS.toMillis(getWorkout().goal.duration),
                    this);
            mCountDownTimer.start();
        }
    }

    private ExerciseType getExerciseType() {
        return getWorkout().exercise.type;
    }

    private GoalType getGoalType() {
        return getWorkout().goal.type;
    }

    public void stop() {
        getWorkout().state = State.INACTIVE;
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
     * This is called when:
     * - a set of an AMRAP workout is finished (COUNTABLE / UNCOUNTABLE + TIME as goal)
     * - a set of a UNCOUNTABLE + REPS as goal workout was finished (manually triggered by the user)
     */
    @Override
    public void onFinishedSet() {

        Timber.tag(TAG).v("onFinishedSet %s / %s", getWorkout().crtSetIdx + 1, getWorkout().goal.sets);

        if (getWorkout().exercise.type == COUNTABLE) {
            getRepCounter().unregister();
        }

        if (getWorkout().goal.type == GoalType.TIME) {
            // Rep(countable or not) based exercises with TIME as goal. (AMRAP)
            // The countdown was finished
            final int duration = getWorkout().goal.duration;
            getWorkout().durations.set(getWorkout().crtSetIdx, duration);
            getWorkout().totalDuration += duration;
        } else if (getWorkout().goal.type == GoalType.REPS) {
            // Rep based exercises with REPS as goal.
            // The user stated that he achieved the rep goal
            getWorkout().reps.set(getWorkout().crtSetIdx, mWorkout.goal.reps);
            getWorkout().durations.set(getWorkout().crtSetIdx, mTimer.elapsedSeconds);
            getWorkout().totalDuration += getWorkout().durations.get(getWorkout().crtSetIdx);
            mTimer.stop();
        }

        ++getWorkout().crtSetIdx;

        if (getWorkout().isLastSet()) {
            getWorkout().state = State.WORKOUT_FINISHED;
            EventBus.getDefault().post(new Events.FinishedWorkoutEvent());
        } else {
            if (SettingsHelper.autoStartBreak(getWorkout().exercise.type)) {
                getWorkout().state = State.BREAK_ACTIVE;
                EventBus.getDefault().post(new Events.StartBreak(getWorkout().goal.duration_break));
            } else {
                getWorkout().state = State.SET_FINISHED;
                EventBus.getDefault().post(new Events.SetFinished());
            }
        }
    }

    /**
     * Called when the user tries to stop a workout.
     * This will pause and un-pause the workout.
     */
    public void toggle() {
        if (getWorkout().state == State.BREAK_ACTIVE) {
            mElapsed = mCountDownTimer.seconds;
            mCountDownTimer.cancel();
            getWorkout().state = State.BREAK_PAUSED;
            return;
        } else if (getWorkout().state == State.BREAK_PAUSED) {
            getWorkout().state = State.BREAK_ACTIVE;
            EventBus.getDefault().post(new Events.StartBreak(mElapsed));
            return;
        }

        final boolean active = getWorkout().state == State.ACTIVE;

        getWorkout().state = active ? State.PAUSED : State.ACTIVE;

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
                mCountDownTimer = new CountDownTimer(TimerType.COUNT_DOWN, mElapsed);
                mCountDownTimer.start();
            }
        } else if (getGoalType().equals(GoalType.REPS)){
            if (active) {
                mElapsed = mTimer.elapsedSeconds;
                mTimer.stop();
            } else {
                mTimer.start(mElapsed);
            }
        }
    }
}
