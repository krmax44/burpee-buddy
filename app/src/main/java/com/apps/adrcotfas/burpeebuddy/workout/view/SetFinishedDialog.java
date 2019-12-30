package com.apps.adrcotfas.burpeebuddy.workout.view;

import android.app.Dialog;
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

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalToString;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.apps.adrcotfas.burpeebuddy.workout.manager.InProgressWorkout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;

import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalToString.formatSeconds;
import static com.apps.adrcotfas.burpeebuddy.edit_goals.dialog.AddEditGoalDialog.DURATION_FACTOR;

public class SetFinishedDialog extends DialogFragment {

    private InProgressWorkout mWorkout;
    private int mBreakDuration;
    private boolean mIsFinalSet;

    public static SetFinishedDialog getInstance(InProgressWorkout workout) {
        SetFinishedDialog dialog = new SetFinishedDialog();
        dialog.mWorkout = workout;
        return dialog;
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {

        mIsFinalSet = mWorkout.crtSet - 1 == mWorkout.goal.sets;
        mBreakDuration = mWorkout.goal.duration_break;

        final MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(getActivity());
        final View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_set_finished, null, false);


        setupChips(v);

        v.findViewById(R.id.overview_container).setVisibility(View.VISIBLE);
        setupOverview(v, mWorkout.goal.type);

        if (mWorkout.goal.type.equals(GoalType.AMRAP)) {
            v.findViewById(R.id.reps_container).setVisibility(View.VISIBLE);
            setupRepsEditText(v);
            setupModifierButtons(v);
        }

        setupBreakSeekbar(v);

        if (!mIsFinalSet) {
            b.setNegativeButton(android.R.string.cancel,
                    (dialog, which) -> new Events.FinishedWorkoutEvent());
        }

        Dialog d = b
            .setTitle("Set finished(" + (mWorkout.crtSet - 1) + "/" + mWorkout.goal.sets + ")")
            .setCancelable(false)
            .setPositiveButton(mIsFinalSet ? getString(android.R.string.ok) : "Start break",
                    (dialog, which) -> EventBus.getDefault().post(mIsFinalSet
                            ? new Events.FinishedWorkoutEvent()
                            : new Events.StartBreak(mBreakDuration)))
            .setView(v)
            .create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }

    private void setupBreakSeekbar(View v) {
        LinearLayout breakContainer = v.findViewById(R.id.break_container);
        if (mIsFinalSet) {
            breakContainer.setVisibility(View.GONE);
            return;
        }

        AppCompatSeekBar breakSeekbar = v.findViewById(R.id.break_seekbar);
        TextView breakDesc = v.findViewById(R.id.break_title);
        breakSeekbar.setProgress(mWorkout.goal.duration_break / DURATION_FACTOR);

        breakSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int seconds = progress * DURATION_FACTOR;
                mBreakDuration = seconds;
                breakDesc.setText(formatBreakDesc(seconds));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}
        });

        if (mBreakDuration / DURATION_FACTOR == breakSeekbar.getProgress()) {
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

        TextInputLayout repsEditTextLayout = v.findViewById(R.id.reps_layout);
        TextInputEditText repsEditText = v.findViewById(R.id.reps);
        repsEditText.setText(String.valueOf(mWorkout.reps.get(mWorkout.crtSet - 1)));

        repsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                if (s.length() == 0) {
                    repsEditTextLayout.setError("reps");
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                    totalRepsText.setText(String.valueOf(mWorkout.totalReps));
                } else {
                    repsEditTextLayout.setError(null);
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                    totalRepsText.setText(String.valueOf(mWorkout.totalReps + Integer.valueOf(s.toString())));
                }
            }
        });
    }

    private void setupOverview(View v, GoalType type) {
        if (type.equals(GoalType.TIME_BASED)) {
            v.findViewById(R.id.overview_container).setVisibility(View.GONE);
            return;
        }

        v.findViewById(R.id.prev_set_container).setVisibility(mWorkout.crtSet == 2 ? View.GONE : View.VISIBLE);

        TextView prevSetText = v.findViewById(R.id.prev_set);
        TextView totalRepsText = v.findViewById(R.id.total_reps);
        TextView avgPaceText = v.findViewById(R.id.avg_pace);

        if (type.equals(GoalType.AMRAP)) {
            prevSetText.setText(String.valueOf(mWorkout.crtSet - 1 == 1
                    ? 0
                    : mWorkout.reps.get(mWorkout.crtSet - 1)));
            totalRepsText.setText(String.valueOf(mWorkout.totalReps));
            avgPaceText.setText(getAvgPaceText());
        } else if (type.equals(GoalType.REP_BASED)) {
            v.findViewById(R.id.crt_set_container).setVisibility(View.VISIBLE);
            TextView crtSetText = v.findViewById(R.id.crt_set);
            crtSetText.setText(formatSeconds(mWorkout.durations.get(mWorkout.crtSet - 1)));

            prevSetText.setText(mWorkout.crtSet - 1 == 1
                    ? String.valueOf(0)
                    : formatSeconds(mWorkout.durations.get(mWorkout.crtSet - 2)));
            totalRepsText.setText(formatSeconds(mWorkout.totalDuration));
            avgPaceText.setText(getAvgPaceText());
        }
    }

    private String getAvgPaceText() {
        final double reps = mWorkout.reps.get(mWorkout.crtSet - 1);
        if (mIsFinalSet) {
            double totalReps = reps;
            for (int i = 0; i < mWorkout.goal.sets; ++i) {
                totalReps += mWorkout.reps.get(i);
            }
            final double totalDuration = mWorkout.totalDuration;
            return Math.round(totalReps * 60.0 * 10.0 / totalDuration) / 10.0 + " reps/min";
        } else {
            final double duration = mWorkout.durations.get(mWorkout.crtSet - 1);
            return Math.round(reps * 60.0 * 10.0 / duration) / 10.0 + " reps/min";
        }
    }

    private void setupChips(View v) {
        Chip chipExercise = v.findViewById(R.id.chip_exercise);
        Chip chipGoal = v.findViewById(R.id.chip_goal);
        chipExercise.setText(mWorkout.exercise.name);
        chipGoal.setText(GoalToString.goalToString(mWorkout.goal));
    }

    @Override
    public void onResume() {
        super.onResume();
        AlertDialog dialog = (AlertDialog) getDialog();
//        if (dialog != null) {
//            TextInputEditText repsEditText = dialog.findViewById(R.id.reps);
//            if (repsEditText.getText() == null || Integer.valueOf(repsEditText.getText().toString()) == 0) {
//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//            }
//        }
    }

    @NonNull
    private String formatBreakDesc(int seconds) {
        return getString(R.string.goal_dialog_break) + ": " + formatSeconds(seconds);
    }
}
