package com.apps.adrcotfas.burpeebuddy.workout.view;

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

    private ConstraintLayout mStatsHeader;
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
        mStatsHeader = findViewById(R.id.stats_header);
        mStatsContainer = findViewById(R.id.stats_container);
        mStatRowViews = new ArrayList<>(getWorkout().getGoalSets() + 1);

        if (mIsTimeBased) {
            mStatsHeader.findViewById(R.id.reps).setVisibility(View.GONE);
            mStatsHeader.findViewById(R.id.avg_pace).setVisibility(View.GONE);
        }

        for (int i = 0; i <= getWorkout().getGoalSets(); ++i) {
            SetViewRow row = new SetViewRow(
                    (ConstraintLayout) mInflater.inflate(
                            R.layout.view_workout_set_row, null, false));
            mStatRowViews.add(row);
            if (i < getWorkout().getGoalSets()) {
                // Set row:
                if (getWorkout().getCurrentSetIdx() == i) {
                    row.parent.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
                }
                row.header.setText(i < 9 ? "0" + (i + 1) : (i + 1) + "");
                final int duration = getWorkout().getDurationFromSet(i);
                row.duration.setText(duration > 0 ? formatSecondsAlt(duration): "-");

                if (!mIsTimeBased) {
                    final int reps = getWorkout().getRepsFromSet(i);
                    row.reps.setText(reps > 0 ? reps + "" : "-");
                    final double avgPace = getWorkout().getAvgPaceFromSet(i);
                    row.avgPace.setText(avgPace > 0 ? avgPace + "" : "-");
                } else {
                    row.reps.setVisibility(View.GONE);
                    row.avgPace.setVisibility(View.GONE);
                }
            } else {
                row.header.setText("Total");
                final int duration = getWorkout().getTotalDuration();
                row.duration.setText(duration > 0 ? formatSecondsAlt(duration) : "-");

                if (getWorkout().getState() == State.WORKOUT_FINISHED_IDLE) {
                    row.parent.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
                }

                if (!mIsTimeBased) {
                    final int reps = getWorkout().getTotalReps();
                    row.reps.setText(reps > 0 ? reps + "" : "-");
                    final double avgPace = getWorkout().getTotalAvgPace();
                    row.avgPace.setText(avgPace > 0 ? avgPace + "" : "-");
                } else {
                    row.reps.setVisibility(View.GONE);
                    row.avgPace.setVisibility(View.GONE);
                }
            }
            mStatsContainer.addView(row.parent);
        }

    }

    private SetViewRow getCurrentSetViewRow() {
        return mStatRowViews.get(getWorkout().getCurrentSetIdx());
    }

    private SetViewRow getSetViewRowAt(int index) {
        return mStatRowViews.get(index);
    }

    private SetViewRow getTotalRow() {
        return mStatRowViews.get(getWorkout().getGoalSets());
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

    @Override
    public void onStartBreak() {
        Timber.tag(TAG).v("onStartBreak, %s", this.hashCode());

        if (!mIsTimeBased) {
            getSetViewRowAt(getWorkout().getCurrentSetIdx() - 1).reps.setText(
                    String.valueOf(getWorkout().getRepsFromSet(getWorkout().getCurrentSetIdx() - 1)));
            getSetViewRowAt(getWorkout().getCurrentSetIdx() - 1).avgPace.setText(
                    String.valueOf(getWorkout().getAvgPaceFromSet(getWorkout().getCurrentSetIdx() - 1)));

            getTotalRow().reps.setText(String.valueOf(getWorkout().getTotalReps()));
            getTotalRow().avgPace.setText(String.valueOf(getWorkout().getTotalAvgPace()));
        }

        getSetViewRowAt(getWorkout().getCurrentSetIdx() - 1).duration.setText(
                formatSecondsAlt(getWorkout().getDurationFromSet(getWorkout().getCurrentSetIdx() - 1)));
        getTotalRow().duration.setText(formatSecondsAlt(getWorkout().getTotalDuration()));

        getSetViewRowAt(getWorkout().getCurrentSetIdx() - 1).parent.setBackgroundColor(
                getContext().getResources().getColor(R.color.transparent));
        getCurrentSetViewRow().parent.setBackgroundColor(
                getContext().getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onWorkoutFinished() {
        Timber.tag(TAG).v("onWorkoutFinished, %s", this.hashCode());

        if (!mIsTimeBased) {
            getSetViewRowAt(getWorkout().getCurrentSetIdx()).reps.setText(
                    String.valueOf(getWorkout().getRepsFromSet(getWorkout().getCurrentSetIdx())));
            getSetViewRowAt(getWorkout().getCurrentSetIdx()).avgPace.setText(
                    String.valueOf(getWorkout().getAvgPaceFromSet(getWorkout().getCurrentSetIdx())));
            getTotalRow().reps.setText(String.valueOf(getWorkout().getTotalReps()));
            getTotalRow().avgPace.setText(String.valueOf(getWorkout().getTotalAvgPace()));
        }

        getSetViewRowAt(getWorkout().getCurrentSetIdx()).duration.setText(
                formatSecondsAlt(getWorkout().getDurationFromSet(getWorkout().getCurrentSetIdx())));
        getTotalRow().duration.setText(formatSecondsAlt(getWorkout().getTotalDuration()));

        getSetViewRowAt(getWorkout().getCurrentSetIdx()).parent.setBackgroundColor(
                getContext().getResources().getColor(R.color.transparent));
        getTotalRow().parent.setBackgroundColor(
                getContext().getResources().getColor(R.color.colorAccent));
    }

    public InProgressWorkout getWorkout() {
        return BuddyApplication.getWorkoutManager().getWorkout();
    }

    public int getElapsedTime() {
        return BuddyApplication.getWorkoutManager().getElapsedTime();
    }
}
