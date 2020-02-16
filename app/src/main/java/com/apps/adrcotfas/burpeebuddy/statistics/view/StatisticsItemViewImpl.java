package com.apps.adrcotfas.burpeebuddy.statistics.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.google.android.material.chip.Chip;

class StatisticsItemViewImpl extends BaseObservableView<StatisticsItemView.Listener>
        implements StatisticsItemView {

    private Workout workout;

    private Chip exerciseName;

    private TextView date;
    private TextView reps;
    private TextView pace;
    private TextView duration;

    private View overlay;

    public StatisticsItemViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.statistics_list_item, parent, false));

        exerciseName = findViewById(R.id.exercise);
        date = findViewById(R.id.date);
        reps = findViewById(R.id.reps);
        pace = findViewById(R.id.pace);
        duration = findViewById(R.id.duration);
        overlay = findViewById(R.id.overlay);
    }

    @Override
    public void bindWorkout(Workout workout, boolean selected) {

        this.workout = workout;
        if (this.workout.type == ExerciseType.TIME_BASED) {
            reps.setText("");
            pace.setText("");
        }
        exerciseName.setText(this.workout.exerciseName);
        date.setText(StringUtils.formatDateAndTime(this.workout.timestamp));
        if (this.workout.reps == 0) {
            reps.setText("");
        } else {
            reps.setText(this.workout.reps == 1 ? this.workout.reps + " rep" : this.workout.reps + " reps");
        }

        if (this.workout.pace == 0) {
            pace.setText("");
        } else {
            pace.setText(this.workout.pace == (long) this.workout.pace
                    ? (long) this.workout.pace + " reps/min"
                    : this.workout.pace + " reps/min");
        }
        if (this.workout.duration == 0) {
            duration.setText("");
        } else {
            duration.setText(StringUtils.secondsToTimerFormatAlt(this.workout.duration));
        }

        overlay.setVisibility(selected ? View.VISIBLE : View.GONE);
    }
}
