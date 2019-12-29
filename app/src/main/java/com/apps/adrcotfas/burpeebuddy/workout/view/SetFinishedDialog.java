package com.apps.adrcotfas.burpeebuddy.workout.view;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.apps.adrcotfas.burpeebuddy.workout.manager.InProgressWorkout;

public class SetFinishedDialog extends DialogFragment {

    private InProgressWorkout mWorkout;
    public static SetFinishedDialog getInstance(InProgressWorkout workout) {
        SetFinishedDialog dialog = new SetFinishedDialog();
        dialog.mWorkout = workout;
        return dialog;
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {

        String message;
        if (mWorkout.totalReps == 0) {
            message = "No reps were completed.\nYou can do better next time!";
        } else if (mWorkout.totalReps == 1){
            message = "One rep was completed.\nYou can do better next time!";
        } else {
            message = "Congratulations!\nYou have completed " + mWorkout.totalReps + " reps.";
        }

        //Dialog d = new MaterialAlertDialogBuilder(getContext())
        Dialog d = new AlertDialog.Builder(getActivity())
            .setTitle("Workout finished")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok,
                    (dialog, which) -> {
                    })
            .create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }
}
