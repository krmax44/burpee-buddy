package com.apps.adrcotfas.burpeebuddy.workout.view;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;

public class ConfirmStopDialog extends DialogFragment {

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {
        setCancelable(false);
        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Stop workout?")
                .setMessage("Are you sure you want to stop this workout?")
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> {
                            EventBus.getDefault().post(new Events.StopWorkoutEvent());
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_confirmStopDialog_to_mainFragment);
                        })
                .setNegativeButton(android.R.string.cancel, (dialog, which)
                        -> EventBus.getDefault().post(new Events.ToggleWorkoutEvent()));

        Dialog d = b.create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }
}
