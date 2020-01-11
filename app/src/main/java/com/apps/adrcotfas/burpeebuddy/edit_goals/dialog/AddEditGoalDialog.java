package com.apps.adrcotfas.burpeebuddy.edit_goals.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.DialogFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalToString;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;

import org.greenrobot.eventbus.EventBus;

import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalToString.formatSeconds;

public class AddEditGoalDialog extends DialogFragment {

    private static final String TAG = "AddEditGoalDialog";

    private static int REPS_FACTOR = 5;
    public static int DURATION_FACTOR = 30;
    public static int BREAK_DURATION_FACTOR = 15;

    private boolean mEditMode;
    private Goal mGoal;

    public static AddEditGoalDialog getInstance(Goal goal, boolean editMode) {
        AddEditGoalDialog dialog = new AddEditGoalDialog();
        dialog.mEditMode = editMode;

        if (editMode && goal != null) {
            dialog.mGoal = goal;
        } else {
            dialog.mGoal = new Goal();
        }
        return dialog;
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {
        final MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(getActivity());

        final View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_add_edit_goal, null, false);

        setupViews(v);

        if(mEditMode) {
            b.setNeutralButton(getString(R.string.delete), ((dialog, which)
                    -> new MaterialAlertDialogBuilder(getActivity())
                            .setPositiveButton(android.R.string.ok, ((dialog2, which2)
                                    -> EventBus.getDefault().post(new Events.DeleteGoal(mGoal.id))))
                            .setNegativeButton(android.R.string.cancel, (dialog3, which3) -> {})
                            .setTitle("Delete " + GoalToString.goalToString(mGoal) + "?")
                            .setMessage("Deleting this goal will not remove completed workouts from the statistics.")
                            .create().show()));
        }

        Dialog d = b
                .setCancelable(false)
                .setTitle(mEditMode ? "Edit Goal" : "Add Goal")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    EventBus.getDefault().post(mEditMode
                            ? new Events.EditGoal(mGoal.id, mGoal)
                            : new Events.AddGoal(mGoal));
                })
                .setNegativeButton(android.R.string.cancel, ((dialog, which) -> {}))
                .setView(v)
                .create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }

    private void setupViews(View v) {
        MaterialRadioButton repBasedRadio = v.findViewById(R.id.rep_based);
        MaterialRadioButton durationRadio = v.findViewById(R.id.time_based);

        LinearLayout repsContainer = v.findViewById(R.id.reps_container);
        LinearLayout durationContainer = v.findViewById(R.id.duration_container);
        LinearLayout breakContainer = v.findViewById(R.id.break_container);

        AppCompatSeekBar setsSeekbar = v.findViewById(R.id.sets_seekbar);
        AppCompatSeekBar repsSeekbar = v.findViewById(R.id.reps_seekbar);
        AppCompatSeekBar durationSeekbar = v.findViewById(R.id.duration_seekbar);
        AppCompatSeekBar breakSeekbar = v.findViewById(R.id.break_seekbar);

        TextView setsDesc = v.findViewById(R.id.sets_title);
        TextView repsDesc = v.findViewById(R.id.reps_title);
        TextView durationDesc = v.findViewById(R.id.duration_title);
        TextView breakDesc = v.findViewById(R.id.break_title);

        setsSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int sets, boolean fromUser) {
                mGoal.sets = sets;
                setsDesc.setText(formatSetsDesc(sets));
                breakContainer.setVisibility(sets == 1 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        repsSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int reps = progress * REPS_FACTOR;
                mGoal.reps = reps;
                repsDesc.setText(formatRepsDesc(reps));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        durationSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int seconds = progress * DURATION_FACTOR;
                mGoal.duration = seconds;
                durationDesc.setText(formatDurationDesc(seconds));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        breakSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int seconds = progress * BREAK_DURATION_FACTOR;
                mGoal.duration_break = seconds;
                breakDesc.setText(formatBreakDesc(seconds));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        repBasedRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                repsContainer.setVisibility(View.VISIBLE);
                durationContainer.setVisibility(View.GONE);
                mGoal.type = GoalType.REPS;
            }
        });

        durationRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                repsContainer.setVisibility(View.GONE);
                durationContainer.setVisibility(View.VISIBLE);
                mGoal.type = GoalType.TIME;
            }
        });

        if(mEditMode) {
            final int sets = mGoal.sets;
            final int reps = mGoal.reps;
            final int duration = mGoal.duration;
            final int durationBreak = mGoal.duration_break;

            final int repsProgress = reps / REPS_FACTOR;
            final int durationProgress = duration / DURATION_FACTOR;
            final int durationBreakProgress = durationBreak / BREAK_DURATION_FACTOR;

            if (sets == setsSeekbar.getProgress()) {
                setsDesc.setText(formatSetsDesc(sets));
            } else {
                setsSeekbar.setProgress(sets);
            }

            if (repsProgress == repsSeekbar.getProgress()) {
                repsDesc.setText(formatRepsDesc(reps));
            } else {
                repsSeekbar.setProgress(repsProgress);
            }

            if (durationProgress == durationSeekbar.getProgress()) {
                durationDesc.setText(formatDurationDesc(duration));
            } else {
                durationSeekbar.setProgress(durationProgress);
            }

            if (durationBreakProgress == breakSeekbar.getProgress()) {
                breakDesc.setText(formatBreakDesc(durationBreak));
            } else {
                breakSeekbar.setProgress(durationBreakProgress);
            }

            switch (mGoal.type) {
                case TIME:
                    durationRadio.setChecked(true);
                    repsContainer.setVisibility(View.GONE);
                    break;
                case REPS:
                    repBasedRadio.setChecked(true);
                    durationContainer.setVisibility(View.GONE);
                    break;
                case INVALID:
                    break;
            }
        } else {
            setsDesc.setText(formatSetsDesc(mGoal.sets));
            repsDesc.setText(formatRepsDesc(mGoal.reps));
            durationDesc.setText(formatDurationDesc(mGoal.duration));
            breakDesc.setText(formatBreakDesc(mGoal.duration_break));
            durationContainer.setVisibility(View.GONE);
        }
    }

    @NonNull
    private String formatSetsDesc(int sets) {
        return getString(R.string.goal_dialog_sets) + ": " + sets;
    }

    @NonNull
    private String formatRepsDesc(int reps) {
        return getString(R.string.goal_dialog_reps) + ": " + reps;
    }

    @NonNull
    private String formatDurationDesc(int seconds) {
        return getString(R.string.goal_dialog_duration) + ": "
                + formatSeconds(seconds);
    }

    @NonNull
    private String formatBreakDesc(int seconds) {
        return getString(R.string.goal_dialog_break) + ": " + formatSeconds(seconds);
    }
}
