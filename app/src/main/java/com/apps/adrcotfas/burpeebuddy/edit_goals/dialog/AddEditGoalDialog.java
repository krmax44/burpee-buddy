package com.apps.adrcotfas.burpeebuddy.edit_goals.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.DialogFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;
import com.apps.adrcotfas.burpeebuddy.edit_goals.view.GoalConfigurator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;


public class AddEditGoalDialog extends DialogFragment implements GoalConfigurator.Listener {

    private static final String TAG = "AddEditGoalDialog";

    private boolean mEditMode;
    private Goal mGoal;
    private GoalConfigurator mGoalConfigurator;

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

        mGoalConfigurator = new GoalConfigurator(v, mGoal, this, getContext());

        Dialog d = b
                .setCancelable(false)
                .setTitle(mEditMode ? "Edit Goal" : "Add Goal")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    mGoal = mGoalConfigurator.getGoal();
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

    @Override public void onTypeChanged(GoalType type) {}
    @Override public void onSetsChanged(int sets) {}
    @Override public void onRepsChanged(int reps) {}
    @Override public void onDurationChanged(int duration) {}
}
