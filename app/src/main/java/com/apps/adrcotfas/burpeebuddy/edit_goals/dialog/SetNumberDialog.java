package com.apps.adrcotfas.burpeebuddy.edit_goals.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
import static com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper.GOAL_DURATION;
import static com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper.GOAL_BREAK;
import static com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper.GOAL_REPS;
import static com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper.GOAL_SETS;

public class SetNumberDialog extends DialogFragment {

    private Listener mListener;
    private String mType;

    public interface Listener {
        void onValueSet(String what, int value);
    }

    public static SetNumberDialog getInstance(String title, Listener listener) {
        SetNumberDialog dialog = new SetNumberDialog();
        dialog.mType = title;
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {
        final View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_set_number, null, false);

        String title = "";
        switch (mType) {
            case GOAL_SETS:
                title = getContext().getResources().getString(R.string.goal_dialog_sets);
                break;
            case GOAL_REPS:
                title = getContext().getResources().getString(R.string.goal_dialog_reps);
                break;
            case GOAL_DURATION:
                title = getContext().getResources().getString(R.string.goal_dialog_duration);
                break;
            case GOAL_BREAK:
                title = getContext().getResources().getString(R.string.goal_dialog_break);
                break;
        }

        final EditText input = v.findViewById(R.id.value);
        input.setOnClickListener(v12 -> {
            input.setFocusable(false);
            input.setFocusableInTouchMode(true);
        });
        AlertDialog d = new MaterialAlertDialogBuilder(getActivity())
                .setTitle(title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    final String name = input.getText().toString();
                    if (!TextUtils.isEmpty(name)) {
                        mListener.onValueSet(mType, Integer.parseInt(name));
                    }
                    ((InputMethodManager) this.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                            .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                })
                .setNegativeButton(android.R.string.cancel, ((dialog, which) -> {
                    ((InputMethodManager) this.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                            .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                }))
                .create();

        input.setOnFocusChangeListener((v1, hasFocus)
                -> d.getWindow().setSoftInputMode(hasFocus
                ? SOFT_INPUT_STATE_ALWAYS_VISIBLE
                : SOFT_INPUT_STATE_ALWAYS_HIDDEN));
        return d;
    }
}
