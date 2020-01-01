package com.apps.adrcotfas.burpeebuddy.workout.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalToString;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.apps.adrcotfas.burpeebuddy.workout.manager.InProgressWorkout;
import com.apps.adrcotfas.burpeebuddy.workout.manager.State;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;

import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalToString.formatSeconds;
import static com.apps.adrcotfas.burpeebuddy.edit_goals.dialog.AddEditGoalDialog.BREAK_DURATION_FACTOR;

public class SetFinishedDialog extends DialogFragment {

    private InProgressWorkout mWorkout;
    private int mBreakDuration;
    private MaterialAlertDialogBuilder mBuilder;
    private boolean mIsFinalSet;

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {
        setCancelable(false);
        mWorkout = BuddyApplication.getWorkoutManager().getWorkout();

        mBreakDuration = mWorkout.goal.duration_break;

        mBuilder = new MaterialAlertDialogBuilder(getActivity());
        final View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_set_finished, null, false);

        setupChips(v);
        setupButtonsAndTitle(v);
        setupAutoStartBreakCheckbox(v);

        v.findViewById(R.id.overview_container).setVisibility(View.VISIBLE);
        setupOverview(v, mWorkout.goal.type);

        if (mWorkout.goal.type.equals(GoalType.AMRAP)) {
            v.findViewById(R.id.reps_container).setVisibility(View.VISIBLE);
            setupRepsEditText(v);
            setupModifierButtons(v);
        }

        //TODO: add skip break button
        Dialog d = mBuilder
                .setView(v)
                .create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }

    private void setupAutoStartBreakCheckbox(View v) {
        final MaterialCheckBox checkBox = v.findViewById(R.id.auto_break_checkbox);

        if (mWorkout.exercise.type == ExerciseType.COUNTABLE) {
            checkBox.setText(R.string.auto_start_break_countable);
        } else if (mWorkout.exercise.type == ExerciseType.TIME_BASED) {
            checkBox.setText(R.string.auto_start_break_time_based
            );
        }
        if (SettingsHelper.autoStartBreak(mWorkout.exercise.type)) {
            checkBox.setVisibility(View.GONE);
        } else {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked)
                    -> SettingsHelper.setAutoStartBreak(mWorkout.exercise.type, isChecked));
        }

    }

    private void setupButtonsAndTitle(View v) {
        final DialogInterface.OnClickListener goToMainListener =
                (dialog, which) -> navigateToMain();

        switch (mWorkout.state) {
            case ACTIVE:
                throw new IllegalArgumentException("Invalid workout state" + mWorkout.state);
            case PAUSED:
                mIsFinalSet = true;
                mBuilder.setTitle(getString(R.string.dialog_stop_workout))
                        .setPositiveButton(android.R.string.ok, goToMainListener)
                        .setNegativeButton(android.R.string.cancel,
                                (dialog, which) -> EventBus.getDefault().post(new Events.ToggleWorkoutEvent()));
                break;
            case SET_FINISHED:
                mIsFinalSet = false;
                setupBreakSeekbar(v);
                mBuilder.setTitle(getString(R.string.dialog_set_finished) + "(" + mWorkout.crtSetIdx + "/" + mWorkout.goal.sets + ")")
                        .setPositiveButton(getString(R.string.dialog_start_break),
                                (dialog, which) -> EventBus.getDefault().post(new Events.StartBreak(mBreakDuration)))
                    .setNegativeButton(android.R.string.cancel,
                            goToMainListener);
                break;
            case WORKOUT_FINISHED:
                mIsFinalSet = true;
                //TODO: if autobreak, change the title to "Workout finished"
                mBuilder.setTitle(getString(R.string.dialog_set_finished) + "(" + mWorkout.crtSetIdx + "/" + mWorkout.goal.sets + ")")
                        .setPositiveButton(android.R.string.ok, goToMainListener);
                mBuilder.setOnCancelListener(dialog -> navigateToMain());
                break;
            case INACTIVE:
                // do nothing
        }
    }

    private void navigateToMain() {
        BuddyApplication.getWorkoutManager().getWorkout().state = State.INACTIVE;
        EventBus.getDefault().post(new Events.StopWorkoutEvent());
        NavHostFragment.findNavController(this)
                        .navigate(R.id.action_set_finished_dialog_to_main);
    }

    private void setupBreakSeekbar(View v) {
        LinearLayout breakContainer = v.findViewById(R.id.break_container);
        breakContainer.setVisibility(View.VISIBLE);

        AppCompatSeekBar breakSeekbar = v.findViewById(R.id.break_seekbar);
        TextView breakDesc = v.findViewById(R.id.break_title);
        breakSeekbar.setProgress(mWorkout.goal.duration_break / BREAK_DURATION_FACTOR);

        breakSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int seconds = progress * BREAK_DURATION_FACTOR;
                mBreakDuration = seconds;
                breakDesc.setText(formatBreakDesc(seconds));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}
        });

        if (mBreakDuration / BREAK_DURATION_FACTOR == breakSeekbar.getProgress()) {
            breakDesc.setText(formatBreakDesc(mBreakDuration));
        }
    }

    private void setupModifierButtons(View v) {
        final TextInputEditText repsEditText = v.findViewById(R.id.reps);
        MaterialButton plus_1 = v.findViewById(R.id.reps_plus_one);
        MaterialButton minus_1 = v.findViewById(R.id.reps_minus_one);
        MaterialButton multiply_2 = v.findViewById(R.id.reps_multiply_2);

        // TODO: extract limit of 500 to preferences
        plus_1.setOnClickListener(v1 -> {
            String s = repsEditText.getText().toString();
            final int value = s.equals("") ? 0 : Integer.valueOf(s);

            repsEditText.setText(value == 499
                    ? String.valueOf(500)
                    : String.valueOf(value + 1));
        });
        minus_1.setOnClickListener(v1 -> {
            String s = repsEditText.getText().toString();
            final int value = s.equals("") ? 0 : Integer.valueOf(s);
            repsEditText.setText(value == 0
                    ? String.valueOf(0)
                    : String.valueOf(value - 1));
        });
        multiply_2.setOnClickListener(v1 -> {
            String s = repsEditText.getText().toString();
            final int value = s.equals("") ? 0 : Integer.valueOf(s);
            repsEditText.setText(value >= 250
                    ? String.valueOf(500)
                    : String.valueOf(value * 2));
        });
    }

    private void setupRepsEditText(View v) {
        TextView totalRepsText = v.findViewById(R.id.total_reps);
        TextView avgPaceText = v.findViewById(R.id.avg_pace);

        TextInputLayout repsEditTextLayout = v.findViewById(R.id.reps_layout);
        TextInputEditText repsEditText = v.findViewById(R.id.reps);
        repsEditText.setText(String.valueOf(mWorkout.reps.get(mWorkout.crtSetIdx - 1)));

        repsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            // TODO: I should extract the controller logic from here (the modification of totalReps)
            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                int value = 0;
                if (s.length() == 0) {
                    repsEditTextLayout.setError(getString(R.string.goal_type_reps));
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                } else {
                    repsEditTextLayout.setError(null);
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                    value = Integer.valueOf(s.toString());
                }
                // remove previous reps from total
                mWorkout.totalReps = mWorkout.totalReps - mWorkout.reps.get(mWorkout.crtSetIdx - 1);
                // update crt set reps
                mWorkout.reps.set(mWorkout.crtSetIdx - 1, value);
                // update total reps
                mWorkout.totalReps = mWorkout.totalReps + mWorkout.reps.get(mWorkout.crtSetIdx - 1);
                // update views
                avgPaceText.setText(getAvgPaceText());
                totalRepsText.setText(String.valueOf(mWorkout.totalReps));
            }
        });
    }

    private void setupOverview(View v, GoalType type) {
        if (mWorkout.state == State.PAUSED || type.equals(GoalType.TIME_BASED)) {
            v.findViewById(R.id.overview_container).setVisibility(View.GONE);
            return;
        }

        v.findViewById(R.id.prev_set_container).setVisibility(
                mWorkout.crtSetIdx == 1
                        ? View.GONE
                        : View.VISIBLE);

        TextView prevSetText = v.findViewById(R.id.prev_set);
        TextView totalRepsText = v.findViewById(R.id.total_reps);
        TextView avgPaceText = v.findViewById(R.id.avg_pace);

        if (type.equals(GoalType.AMRAP)) {
            prevSetText.setText(String.valueOf(mWorkout.crtSetIdx == 1
                    ? 0
                    : mWorkout.reps.get(mWorkout.crtSetIdx - 2)));
            totalRepsText.setText(String.valueOf(mWorkout.totalReps));
            avgPaceText.setText(getAvgPaceText());
        } else if (type.equals(GoalType.REP_BASED)) {
            v.findViewById(R.id.crt_set_container).setVisibility(View.VISIBLE);
            TextView crtSetText = v.findViewById(R.id.crt_set);
            crtSetText.setText(formatSeconds(mWorkout.durations.get(mWorkout.crtSetIdx - 1)));

            prevSetText.setText(mWorkout.crtSetIdx == 1
                    ? String.valueOf(0)
                    : formatSeconds(mWorkout.durations.get(mWorkout.crtSetIdx - 2)));
            totalRepsText.setText(formatSeconds(mWorkout.totalDuration));
            avgPaceText.setText(getAvgPaceText());
        }
    }

    private String getAvgPaceText() {
        if (mIsFinalSet) {
            return Math.round(mWorkout.totalReps * 60.0 * 10.0 / mWorkout.totalDuration) / 10.0 + getString(R.string.dialog_reps_per_min);
        } else {
            final double reps = mWorkout.reps.get(mWorkout.crtSetIdx - 1);
            final double duration = mWorkout.durations.get(mWorkout.crtSetIdx - 1);
            return Math.round(reps * 60.0 * 10.0 / duration) / 10.0 + getString(R.string.dialog_reps_per_min);
        }
    }

    private void setupChips(View v) {
        Chip chipExercise = v.findViewById(R.id.chip_exercise);
        Chip chipGoal = v.findViewById(R.id.chip_goal);
        chipExercise.setText(mWorkout.exercise.name);
        chipGoal.setText(GoalToString.goalToString(mWorkout.goal));
    }

    @NonNull
    private String formatBreakDesc(int seconds) {
        return getString(R.string.goal_dialog_break) + ": " + formatSeconds(seconds);
    }
}
