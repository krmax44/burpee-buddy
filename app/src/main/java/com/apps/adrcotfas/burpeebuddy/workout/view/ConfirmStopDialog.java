package com.apps.adrcotfas.burpeebuddy.workout.view;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;

import org.greenrobot.eventbus.EventBus;

public class ConfirmStopDialog extends DialogFragment {

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Stop workout?")
                .setMessage("Are you sure you want to stop this workout?")
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> {
                            EventBus.getDefault().post(new Events.StopWorkoutEvent());
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_confirmStopDialog_to_mainFragment);
                        })
                .setNegativeButton(android.R.string.cancel, (dialog, which)
                        -> EventBus.getDefault().post(new Events.ToggleWorkoutEvent()))
                .create();
    }
}
