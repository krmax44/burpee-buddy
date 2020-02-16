package com.apps.adrcotfas.burpeebuddy.workout.manager;

import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalToString;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class InProgressWorkout {

    private State mState = State.INACTIVE;
    private Exercise mExercise;
    private Goal mGoal;

    private int mCrtSetIdx;
    private int mTotalReps;
    private int mTotalDuration;

    private List<Integer> mReps;
    private List<Integer> mDurations;

    public InProgressWorkout() {
        reset();
    }

    public void reset() {
        mState = State.INACTIVE;
        mExercise = new Exercise();
        mGoal = new Goal(GoalType.INVALID, 0, 0, 0, 0);
        mCrtSetIdx = 0;
        mTotalReps = 0;
        mTotalDuration = 0;
        mDurations = new ArrayList<>();
        mReps = new ArrayList<>();
    }

    public void init(Exercise exercise, Goal goal) {
        mState = State.INACTIVE;
        mExercise = exercise;
        mGoal = goal;
        mCrtSetIdx = 0;
        mReps = new ArrayList<>();
        mTotalReps = 0;
        mDurations = new ArrayList<>();
        mTotalDuration = 0;

        for (int i = 0; i < goal.sets; ++i) {
            mReps.add(i, 0);
        }
        for (int i = 0; i < goal.sets; ++i) {
            mDurations.add(i, 0);
        }
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        this.mState = state;
    }

    public Exercise getExercise() {
        return mExercise;
    }

    public Goal getGoal() {
        return mGoal;
    }

    public GoalType getGoalType() {
        return mGoal.type;
    }

    public ExerciseType getExerciseType() {
        return mExercise.type;
    }

    public String getExerciseName() {
        return mExercise.name;
    }

    public String getGoalName() {
        return GoalToString.goalToString(mGoal);
    }

    public int getCurrentSet() {
        return mCrtSetIdx + 1;
    }

    public int getCurrentSetIdx() {
        return mCrtSetIdx;
    }

    public void incrementCurrentSet() {
        ++mCrtSetIdx;
    }

    public boolean isFinalSet() {
        return mCrtSetIdx + 1 == mGoal.sets;
    }

    public boolean isFirstSet() {
        return mCrtSetIdx == 0;
    }

    public int getCurrentReps() {
        return mReps.get(mCrtSetIdx);
    }

    public int getRepsFromSet(int set) {
        if (set >= mReps.size()) {
            Timber.wtf("Invalid index");
            return 0;
        }
        return mReps.get(set);
    }

    public boolean isSetFinished() {
        return getCurrentReps() == getGoalReps();
    }

    public void incrementCurrentReps() {
        mReps.set(mCrtSetIdx, getCurrentReps() + 1);
        ++mTotalReps;
    }

    public void setCurrentReps(int reps) {
        this.mReps.set(mCrtSetIdx, reps);
        mTotalReps += reps;
    }

    public void updateCurrentReps(int value) {
        // remove current mReps from total
        mTotalReps = mTotalReps - getCurrentReps();
        // update crt set mReps
        setCurrentReps(value);
    }

    public int getCurrentDuration() {
        return mDurations.get(mCrtSetIdx);
    }

    public int getDurationFromSet(int set) {
        if (set >= mDurations.size()) {
            Timber.wtf("Invalid index");
            return 0;
        }
        return mDurations.get(set);
    }

    public void setCurrentDuration(int duration) {
        mDurations.set(mCrtSetIdx, duration);
        mTotalDuration += duration;
    }

    public int getTotalReps() {
        return mTotalReps;
    }

    public int getTotalDuration() {
        return mTotalDuration;
    }

    public double getAvgPaceFromSet(int i) {
        final int reps = getRepsFromSet(i);
        final int duration = getDurationFromSet(i);
        if (duration == 0) {
            return 0;
        }
        return getAvgPace(reps, duration);
    }

    public double getTotalAvgPace() {
        if (getTotalDuration() == 0) {
            return 0;
        }
        return getAvgPace(getTotalReps(), getTotalDuration());
    }

    public static double getAvgPace(int reps, int duration) {
        if (duration == 0) {
            return 0;
        }
        return Math.round(reps * 60.0 * 100.0 / duration) / 100.0;
    }

    public int getGoalSets() {
        return mGoal.sets;
    }

    public int getGoalReps() {
        return mGoal.reps;
    }

    public int getGoalDuration() {
        return mGoal.duration;
    }

    public int getGoalDurationBreak() {
        return mGoal.duration_break;
    }
}
