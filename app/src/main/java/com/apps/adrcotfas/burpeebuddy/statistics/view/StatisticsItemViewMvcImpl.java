package com.apps.adrcotfas.burpeebuddy.statistics.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.google.android.material.chip.Chip;

class StatisticsItemViewMvcImpl extends BaseObservableViewMvc<StatisticsItemViewMvc.Listener>
        implements StatisticsItemViewMvc {

    private Workout mWorkout;

    private Chip mExerciseName;
    private TextView mDate;
    private TextView mReps;
    private TextView mPace;
    private TextView mDuration;

    public StatisticsItemViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.view_statistics_list_item, parent, false));

        mExerciseName = findViewById(R.id.exercise);
        mDate = findViewById(R.id.date);
        mReps = findViewById(R.id.reps);
        mPace = findViewById(R.id.pace);
        mDuration = findViewById(R.id.duration);
        findViewById(R.id.parent).setOnClickListener(v -> {
            for (Listener l : getListeners()) {
                l.onWorkoutLongPress(mWorkout.id);
            }
        });
    }

    @Override
    public void bindWorkout(Workout workout) {
        mWorkout = workout;
        if (mWorkout.type == ExerciseType.TIME_BASED) {
            mReps.setText("");
            mPace.setText("");
        }
        mExerciseName.setText(mWorkout.exerciseName);
        mDate.setText(StringUtils.formatDateAndTime(mWorkout.timestamp));
        if (mWorkout.reps == 0) {
            mReps.setText("");
        } else {
            mReps.setText(mWorkout.reps == 1 ? mWorkout.reps + " rep" : mWorkout.reps + " reps");
        }

        if (mWorkout.pace == 0) {
            mPace.setText("");
        } else {
            mPace.setText(mWorkout.pace == (long) mWorkout.pace
                    ? (long) mWorkout.pace + " reps/min"
                    : mWorkout.pace + " reps/min");
        }
        if (mWorkout.duration == 0) {
            mDuration.setText("");
        } else {
            mDuration.setText(StringUtils.secondsToTimerFormatAlt(mWorkout.duration));
        }
    }
}
