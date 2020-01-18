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
    private TextView mTime;
    private TextView mReps;
    private TextView mPace;
    private TextView mDuration;

    public StatisticsItemViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.view_statistics_list_item, parent, false));

        mExerciseName = findViewById(R.id.exercise);
        mDate = findViewById(R.id.date);
        mTime = findViewById(R.id.time);
        mReps = findViewById(R.id.reps);
        mPace = findViewById(R.id.pace);
        mDuration = findViewById(R.id.duration);
        //TODO: update views and set onLongPressListener
    }

    @Override
    public void bindWorkout(Workout workout) {
        mWorkout = workout;
        if (mWorkout.type == ExerciseType.TIME_BASED) {
            mReps.setVisibility(View.GONE);
            mPace.setVisibility(View.GONE);
        }
        mExerciseName.setText(mWorkout.exerciseName);
        mDate.setText(StringUtils.formatDateAndTime(mWorkout.timestamp));
        mReps.setText(mWorkout.reps + " reps");
        mPace.setText(mWorkout.pace + " reps/min");
        mDuration.setText(StringUtils.secondsToTimerFormatAlt(mWorkout.duration));
    }
}
