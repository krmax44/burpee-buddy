package com.apps.adrcotfas.burpeebuddy.workout.view;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.utilities.TimerFormat;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableViewMvc;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.workout.manager.InProgressWorkout;
import com.apps.adrcotfas.burpeebuddy.workout.manager.State;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalToString.formatSecondsAlt;

public class WorkoutViewMvcImpl extends BaseObservableViewMvc<WorkoutViewMvc.Listener>
        implements WorkoutViewMvc {

    private static final String TAG = "WorkoutViewMvcImpl";
    private static final int STATS_TOTAL_ROW_INDEX = -1;

    private class SetViewRow {
        SetViewRow (ConstraintLayout parent) {
            this.parent = parent;
            this.header = this.parent.findViewById(R.id.header);
            this.reps = this.parent.findViewById(R.id.reps);
            this.duration = this.parent.findViewById(R.id.duration);
            this.avgPace = this.parent.findViewById(R.id.avg_pace);
        }
        ConstraintLayout parent;
        TextView header;
        TextView reps;
        TextView duration;
        TextView avgPace;
    }

    private final LayoutInflater mInflater;
    private TextView mTimer;
    private final ExtendedFloatingActionButton mFinishSetButton;

    private LinearLayout mStatsContainer;
    private List<SetViewRow> mStatRowViews;

    private boolean mIsTimeBased;

    public WorkoutViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        mInflater = inflater;
        setRootView(mInflater.inflate(R.layout.fragment_workout, parent, false));

        final Chip exerciseChip = findViewById(R.id.exercise_chip);
        final Chip goalChip = findViewById(R.id.goal_chip);
        exerciseChip.setText(getWorkout().getExerciseName());
        goalChip.setText(getWorkout().getGoalName());

        mIsTimeBased = getWorkout().getExerciseType() == ExerciseType.TIME_BASED;
        setupStats();

        ExtendedFloatingActionButton stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onStopButtonClicked();
            }
        });

        mFinishSetButton = findViewById(R.id.finish_set_button);
        mFinishSetButton.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onFinishSetButtonClicked();
            }
        });

        mTimer = findViewById(R.id.timer);
    }

    private void setupStats() {
        ConstraintLayout statsHeader = findViewById(R.id.stats_header);
        if (mIsTimeBased) {
            statsHeader.findViewById(R.id.reps).setVisibility(View.GONE);
            statsHeader.findViewById(R.id.avg_pace).setVisibility(View.GONE);
        }

        mStatsContainer = findViewById(R.id.stats_container);
        mStatRowViews = new ArrayList<>();
        for (int i = 0; i < getWorkout().getCurrentSet(); ++i) {
            SetViewRow row = createNewStatsRow(i);
            mStatRowViews.add(row);
            mStatsContainer.addView(row.parent);
        }
        if (getWorkout().getState() == State.WORKOUT_FINISHED_IDLE) {
            SetViewRow total = createNewStatsRow(STATS_TOTAL_ROW_INDEX);
            mStatsContainer.addView(total.parent);
        }
    }

    @Override
    public void onStartBreak() {
        Timber.tag(TAG).v("onStartBreak, %s", this.hashCode());

        // refresh previous row
        refreshStatsRow(getWorkout().getCurrentSetIdx() - 1);
        // new row for the new set
        SetViewRow row = createNewStatsRow(getWorkout().getCurrentSetIdx());
        mStatRowViews.add(row);
        mStatsContainer.addView(row.parent);
    }

    @Override
    public void onWorkoutFinished() {
        Timber.tag(TAG).v("onWorkoutFinished, %s", this.hashCode());

        // refresh last row
        refreshStatsRow(getWorkout().getCurrentSetIdx());
        // new row for total
        SetViewRow total = createNewStatsRow(STATS_TOTAL_ROW_INDEX);

        // workaround for uncountable exercises with rep based goals
        if (getWorkout().getState() == State.WORKOUT_FINISHED_IDLE) {
            // refresh the total row after the user entered the number of reps for the last set
            mStatRowViews.set(mStatRowViews.size() - 1, total);
            mStatsContainer.removeViewAt(mStatsContainer.getChildCount() - 1);
            mStatsContainer.addView(total.parent);
            return;
        }

        mStatRowViews.add(total);
        mStatsContainer.addView(total.parent);
    }

    private SetViewRow createNewStatsRow(int index) {
        SetViewRow row = new SetViewRow(
                (ConstraintLayout) mInflater.inflate(
                        R.layout.view_workout_set_row, null, false));

        if (index == STATS_TOTAL_ROW_INDEX) {
            row.header.setText("Total");
            final int duration = getWorkout().getTotalDuration();
            row.duration.setText(duration > 0 ? formatSecondsAlt(duration): "-");
        } else {
            row.header.setText(index < 9 ? "0" + (index + 1) : (index + 1) + "");
            final int duration = getWorkout().getDurationFromSet(index);
            row.duration.setText(duration > 0 ? formatSecondsAlt(duration): "-");
        }

        if (!mIsTimeBased) {
            if (index == STATS_TOTAL_ROW_INDEX) {
                final int reps = getWorkout().getTotalReps();
                row.reps.setText(reps > 0 ? reps + "" : "-");
                final double avgPace = getWorkout().getTotalAvgPace();
                row.avgPace.setText(avgPace > 0 ? avgPace + "" : "-");
            } else {
                final int reps = getWorkout().getRepsFromSet(index);
                row.reps.setText(reps > 0 ? reps + "" : "-");
                final double avgPace = getWorkout().getAvgPaceFromSet(index);
                row.avgPace.setText(avgPace > 0 ? avgPace + "" : "-");
            }
        } else {
            row.reps.setVisibility(View.GONE);
            row.avgPace.setVisibility(View.GONE);
        }

        row.parent.setBackgroundTintList(ColorStateList.valueOf(
                getContext().getResources().getColor(
                        getWorkout().getCurrentSetIdx() == index
                                || index == STATS_TOTAL_ROW_INDEX
                                ? R.color.colorAccent
                                : R.color.transparent)));
        return row;
    }

    private void refreshStatsRow(int index) {
        if (!mIsTimeBased) {
            getSetViewRowAt(index).reps.setText(
                    String.valueOf(getWorkout().getRepsFromSet(index)));
            getSetViewRowAt(index).avgPace.setText(
                    String.valueOf(getWorkout().getAvgPaceFromSet(index)));
        }
        getSetViewRowAt(index).header.setText(index < 9 ? "0" + (index + 1) : (index + 1) + "");
        getSetViewRowAt(index).duration.setText(
                formatSecondsAlt(getWorkout().getDurationFromSet(index)));
        getSetViewRowAt(index).parent.setBackgroundTintList(ColorStateList.valueOf(
                getContext().getResources().getColor(
                        getWorkout().getCurrentSetIdx() == index
                                && getWorkout().getState() != State.WORKOUT_FINISHED
                                && getWorkout().getState() != State.WORKOUT_FINISHED_IDLE
                                ? R.color.colorAccent
                                : R.color.transparent)));
    }

    private SetViewRow getCurrentSetViewRow() {
        return mStatRowViews.get(getWorkout().getCurrentSetIdx());
    }

    private SetViewRow getSetViewRowAt(int index) {
        return mStatRowViews.get(index);
    }

    @Override
    public void onRepComplete(int reps) {
        getCurrentSetViewRow().reps.setText(reps + "");

        final int duration = getElapsedTime();
        if (duration > 0) {
            getCurrentSetViewRow().avgPace.setText(String.valueOf(getWorkout().getAvgPace(reps, duration)));
        }
    }

    @Override
    public void onTimerTick(int seconds) {
        mTimer.setText(TimerFormat.secondsToTimerFormat(seconds));
        if (getWorkout().getState() == State.ACTIVE) {
            getCurrentSetViewRow().duration.setText(formatSecondsAlt(getElapsedTime()));
        }
    }

    @Override
    public void setFinishSetButtonVisibility(int visibility) {
        mFinishSetButton.setVisibility(visibility);
    }

    public InProgressWorkout getWorkout() {
        return BuddyApplication.getWorkoutManager().getWorkout();
    }

    public int getElapsedTime() {
        return BuddyApplication.getWorkoutManager().getElapsedTime();
    }
}
