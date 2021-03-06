package com.apps.adrcotfas.burpeebuddy.workout.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.common.viewmvc.BaseObservableView;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;
import com.apps.adrcotfas.burpeebuddy.workout.manager.InProgressWorkout;
import com.apps.adrcotfas.burpeebuddy.workout.manager.State;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.view.animation.AnimationUtils.loadAnimation;
import static com.apps.adrcotfas.burpeebuddy.db.goal.GoalToString.formatSecondsAlt;

public class WorkoutViewImpl extends BaseObservableView<WorkoutView.Listener>
        implements WorkoutView {

    private static final String TAG = "WorkoutViewImpl";
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
    private final MaterialButton mFinishSetButton;
    private final MaterialButton mStopButton;

    private LinearLayout mStatsContainer;
    private ScrollView mStatsScrollView;
    private TextView advice;

    private List<SetViewRow> mStatRowViews;

    private boolean mIsTimeBased;

    public WorkoutViewImpl(LayoutInflater inflater, ViewGroup parent) {
        mInflater = inflater;
        setRootView(mInflater.inflate(R.layout.fragment_workout, parent, false));

        final Chip exerciseChip = findViewById(R.id.exercise_chip);
        final Chip goalChip = findViewById(R.id.goal_chip);
        exerciseChip.setText(getWorkout().getExerciseName());
        goalChip.setText(getWorkout().getGoalName());
        advice = findViewById(R.id.advice);
        advice.setVisibility(getWorkout().getExerciseType() == ExerciseType.COUNTABLE ? View.VISIBLE : View.GONE);

        mIsTimeBased = getWorkout().getExerciseType() == ExerciseType.TIME_BASED;
        setupStats();

        mStopButton = findViewById(R.id.stop_button);
        mStopButton.setOnClickListener(v -> {
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
        setFinishSetButtonVisibility(
                (getWorkout().getState() == State.ACTIVE || getWorkout().getState() == State.PAUSED)
                        && (getWorkout().getGoalType() == GoalType.REPS && getWorkout().getExerciseType() == ExerciseType.UNCOUNTABLE)
                        ? View.VISIBLE : View.GONE);

        mTimer = findViewById(R.id.timer);
        mTimer.setText(StringUtils.secondsToTimerFormatAlt(getElapsedTime()));
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
            SetViewRow row = createStatsRow(i);
            mStatRowViews.add(row);
            mStatsContainer.addView(row.parent);
        }
        if (getWorkout().getState() == State.WORKOUT_FINISHED_IDLE) {
            SetViewRow total = createStatsRow(STATS_TOTAL_ROW_INDEX);
            mStatsContainer.addView(total.parent);
        }

        toggleRowAppearance(getWorkout().getState() == State.ACTIVE
                || getWorkout().getState() == State.PAUSED
                || getWorkout().getState() == State.SET_FINISHED);

        mStatsScrollView = findViewById(R.id.stats_scroll_view);
        mStatsScrollView.post(() -> mStatsScrollView.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public void toggleRowAppearance(boolean isWorkingOut) {
        if (isWorkingOut) {
            ConstraintLayout parent = getCurrentSetViewRow().parent;
            parent.clearAnimation();
            ViewHelper.setBackgroundTint(getContext(), parent, R.color.colorAccent);
            if (!mIsTimeBased) {
                getCurrentSetViewRow().reps.setVisibility(View.VISIBLE);
                getCurrentSetViewRow().avgPace.setVisibility(View.VISIBLE);
            }
            getCurrentSetViewRow().header.setVisibility(View.VISIBLE);
            getCurrentSetViewRow().duration.setText("-");
        } else {
            ConstraintLayout parent = getCurrentSetViewRow().parent;
            parent.startAnimation(loadAnimation(getContext(), R.anim.blink));
            ViewHelper.setBackgroundTint(getContext(), parent, R.color.gray800);

            getCurrentSetViewRow().header.setVisibility(View.GONE);
            getCurrentSetViewRow().reps.setVisibility(View.GONE);
            getCurrentSetViewRow().avgPace.setVisibility(View.GONE);
            getCurrentSetViewRow().duration.setText("Get ready");
        }
    }

    @Override
    public void onStartBreak() {
        // refresh previous row
        final int currentSetIdx = getWorkout().getCurrentSetIdx();
        refreshStatsRow(currentSetIdx - 1);
        // new row for the new set
        SetViewRow row = createStatsRow(currentSetIdx);
        mStatRowViews.add(row);
        mStatsContainer.addView(row.parent);
        mStatsScrollView.post(() -> mStatsScrollView.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public void onWorkoutFinished() {
        Timber.tag(TAG).v("onWorkoutFinished, %s", this.hashCode());

        // refresh last row
        refreshStatsRow(getWorkout().getCurrentSetIdx());
        // new row for total
        SetViewRow total = createStatsRow(STATS_TOTAL_ROW_INDEX);

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
        mStatsScrollView.post(() -> mStatsScrollView.fullScroll(View.FOCUS_DOWN));

        mTimer.setVisibility(View.GONE);
        mStopButton.setVisibility(View.GONE);
        mFinishSetButton.setVisibility(View.GONE);
    }

    private SetViewRow createStatsRow(int index) {
        SetViewRow row = new SetViewRow(
                (ConstraintLayout) mInflater.inflate(
                        R.layout.view_workout_set_row, null, false));

        if (index == STATS_TOTAL_ROW_INDEX) {
            row.header.setText("Total");
        } else {
            row.header.setText(index < 9 ? "0" + (index + 1) : (index + 1) + "");
        }

        final boolean isCurrentSet = index == getWorkout().getCurrentSetIdx();

        final int duration = isCurrentSet
                ? getElapsedTime()
                : (index == STATS_TOTAL_ROW_INDEX)
                    ? getWorkout().getTotalDuration()
                    : getWorkout().getDurationFromSet(index);

        row.duration.setText(duration > 0 ? formatSecondsAlt(duration): "-");

        if (!mIsTimeBased) {
            final int reps = (index == STATS_TOTAL_ROW_INDEX)
                    ? getWorkout().getTotalReps()
                    : getWorkout().getRepsFromSet(index);
            row.reps.setText(reps > 0 ? reps + "" : "-");

            double avgPace = (index == STATS_TOTAL_ROW_INDEX)
                    ? getWorkout().getTotalAvgPace()
                    : isCurrentSet
                    ? getWorkout().getAvgPace(reps, getElapsedTime())
                    : getWorkout().getAvgPaceFromSet(index);

            // remove trailing zeros if it's the case
            row.avgPace.setText(avgPace > 0
                    ? ((avgPace == (long) avgPace)
                        ? String.valueOf((long) avgPace)
                        : String.valueOf(avgPace))
                    : "-");
        } else {
            row.reps.setVisibility(View.GONE);
            row.avgPace.setVisibility(View.GONE);
        }

        ViewHelper.setBackgroundTint(getContext(), row.parent,
                getWorkout().getCurrentSetIdx() == index || index == STATS_TOTAL_ROW_INDEX
                ? R.color.colorAccent
                : R.color.transparent);
        return row;
    }

    private void refreshStatsRow(int index) {
        SetViewRow row = getSetViewRowAt(index);
        if (!mIsTimeBased) {
            row.reps.setText(
                    String.valueOf(getWorkout().getRepsFromSet(index)));

            double avgPaceFromSet = getWorkout().getAvgPaceFromSet(index);
            // remove trailing zeros if it's the case
            row.avgPace.setText(avgPaceFromSet == (long) avgPaceFromSet
                    ? String.valueOf((long)avgPaceFromSet)
                    : String.valueOf(avgPaceFromSet));
        }
        row.header.setText(index < 9 ? "0" + (index + 1) : (index + 1) + "");
        row.duration.setText(
                formatSecondsAlt(getWorkout().getDurationFromSet(index)));

        ViewHelper.setBackgroundTint(getContext(), row.parent, getWorkout().getCurrentSetIdx() == index
                && getWorkout().getState() != State.WORKOUT_FINISHED
                && getWorkout().getState() != State.WORKOUT_FINISHED_IDLE
                ? R.color.colorAccent
                : R.color.transparent);
    }

    private SetViewRow getCurrentSetViewRow() {
        return mStatRowViews.get(getWorkout().getCurrentSetIdx());
    }

    private SetViewRow getSetViewRowAt(int index) {
        return mStatRowViews.get(index);
    }

    @Override
    public void onRepComplete() {
        final int reps = getWorkout().getCurrentReps();
        getCurrentSetViewRow().reps.setText(reps + "");

        final int duration = getElapsedTime();
        if (duration > 0) {
            double avgPace = getWorkout().getAvgPace(reps, duration);

            // remove trailing zeros if it's the case
            getCurrentSetViewRow().avgPace.setText(avgPace == (long) avgPace
                    ? String.valueOf((long) avgPace)
                    : String.valueOf(avgPace));
        }
    }

    @Override
    public void onTimerTick(int seconds) {
        mTimer.setText(StringUtils.secondsToTimerFormatAlt(seconds));
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
