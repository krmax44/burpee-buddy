package com.apps.adrcotfas.burpeebuddy.workout;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class WorkoutFinishedDialog extends DialogFragment {

    private static String KEY_REPS = "reps";
    public static WorkoutFinishedDialog getInstance(int reps) {
        WorkoutFinishedDialog dialog = new WorkoutFinishedDialog();
        Bundle args = new Bundle(1);
        args.putInt(KEY_REPS, reps);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {
        int reps = getArguments().getInt(KEY_REPS);
        String message;
        if (reps == 0) {
            message = "No reps were completed.\nYou can do better next time!";
        } else if (reps == 1){
            message = "One rep was completed.\nYou can do better next time!";
        } else {
            message = "Congratulations!\nYou have completed " + reps + " reps.";
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
