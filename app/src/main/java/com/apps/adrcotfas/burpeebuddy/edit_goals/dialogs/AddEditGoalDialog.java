package com.apps.adrcotfas.burpeebuddy.edit_goals.dialogs;

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
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.db.goals.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalTypeConverter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;

import org.greenrobot.eventbus.EventBus;

import static com.apps.adrcotfas.burpeebuddy.db.goals.Goal.DEFAULT_BREAK;
import static com.apps.adrcotfas.burpeebuddy.db.goals.Goal.DEFAULT_DURATION;
import static com.apps.adrcotfas.burpeebuddy.db.goals.Goal.DEFAULT_REPS;
import static com.apps.adrcotfas.burpeebuddy.db.goals.Goal.DEFAULT_SETS;
import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalToString.formatSeconds;

public class AddEditGoalDialog extends DialogFragment {

    private static final String TAG = "AddEditGoalDialog";

    private static String KEY_TYPE = TAG + "_key_type";
    private static String KEY_SETS = TAG + "_key_sets";
    private static String KEY_REPS = TAG + "_key_reps";
    private static String KEY_DURATION = TAG + "_key_duration";
    private static String KEY_BREAK = TAG + "_key_break";

    private static int REPS_FACTOR = 5;
    private static int DURATION_FACTOR = 30;

    private boolean mEditMode;
    private int mGoalId;
    private Goal mCreatedGoal;

    public static AddEditGoalDialog getInstance(Goal goal, boolean editMode) {
        AddEditGoalDialog dialog = new AddEditGoalDialog();
        dialog.mEditMode = editMode;

        Bundle args = new Bundle(5);
        if (editMode && goal != null) {
            dialog.mGoalId = goal.id;
            dialog.mCreatedGoal = goal;
            args.putInt(KEY_TYPE, GoalTypeConverter.getIntFromGoal(goal.type));
            args.putInt(KEY_SETS, goal.sets);
            args.putInt(KEY_REPS, goal.reps);
            args.putInt(KEY_DURATION, goal.duration);
            args.putInt(KEY_BREAK, goal.duration_break);
            dialog.setArguments(args);
        } else {
            dialog.mCreatedGoal  = new Goal(GoalType.REP_BASED, DEFAULT_SETS, DEFAULT_REPS, DEFAULT_DURATION, DEFAULT_BREAK);
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
                    -> EventBus.getDefault().post(new Events.DeleteGoal(mGoalId))));
        }

        Dialog d = b
                .setCancelable(false)
                .setTitle(mEditMode ? "Edit Goal" : "Add Goal")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    EventBus.getDefault().post(mEditMode
                            ? new Events.EditGoal(mGoalId, mCreatedGoal)
                            : new Events.AddGoal(mCreatedGoal));
                })
                .setNegativeButton(android.R.string.cancel, ((dialog, which) -> {}))
                .setView(v)
                .create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }

    private void setupViews(View v) {
        MaterialRadioButton repBasedRadio = v.findViewById(R.id.rep_based);
        MaterialRadioButton amrapRadio = v.findViewById(R.id.amrap);
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
                mCreatedGoal.sets = sets;
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
                mCreatedGoal.reps = reps;
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
                mCreatedGoal.duration = seconds;
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
                final int seconds = progress * DURATION_FACTOR;
                mCreatedGoal.duration_break = seconds;
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
                mCreatedGoal.type = GoalType.REP_BASED;
            }
        });

        amrapRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                repsContainer.setVisibility(View.GONE);
                durationContainer.setVisibility(View.VISIBLE);
                mCreatedGoal.type = GoalType.AMRAP;
            }
        });

        durationRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                repsContainer.setVisibility(View.GONE);
                durationContainer.setVisibility(View.VISIBLE);
                mCreatedGoal.type = GoalType.TIME_BASED;
            }
        });

        if(mEditMode) {
            final int sets = getArguments().getInt(KEY_SETS);
            final int reps = getArguments().getInt(KEY_REPS);
            final int duration = getArguments().getInt(KEY_DURATION);
            final int durationBreak = getArguments().getInt(KEY_BREAK);

            final int repsProgress = reps / REPS_FACTOR;
            final int durationProgress = duration / DURATION_FACTOR;
            final int durationBreakProgress = durationBreak / DURATION_FACTOR;

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

            GoalType goalType = GoalTypeConverter.getGoalTypeFromInt(getArguments().getInt(KEY_TYPE));
            switch (goalType) {
                case TIME_BASED:
                    durationRadio.setChecked(true);
                    repsContainer.setVisibility(View.GONE);
                    break;
                case REP_BASED:
                    repBasedRadio.setChecked(true);
                    durationContainer.setVisibility(View.GONE);
                    break;
                case AMRAP:
                    amrapRadio.setChecked(true);
                    repsContainer.setVisibility(View.GONE);
                    break;
                case INVALID:
                    break;
            }
        } else {
            setsDesc.setText(formatSetsDesc(mCreatedGoal.sets));
            repsDesc.setText(formatRepsDesc(mCreatedGoal.reps));
            durationDesc.setText(formatDurationDesc(mCreatedGoal.duration));
            breakDesc.setText(formatBreakDesc(mCreatedGoal.duration_break));
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
