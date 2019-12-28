package com.apps.adrcotfas.burpeebuddy.main.edit_exercises.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.bl.Events;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseTypeConverter;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

public class AddEditExerciseDialog extends DialogFragment {

    private static final String TAG = "AddEditExerciseDialog";

    private static String KEY_NAME  = TAG + "_key_name";
    private static String KEY_TYPE  = TAG + "_key_type";
    private static String KEY_COLOR = TAG + "_key_color";

    private boolean mEditMode;
    /** only relevant in edit mode */
    private String mExerciseName;

    public static AddEditExerciseDialog getInstance(Exercise exercise, boolean editMode) {
        AddEditExerciseDialog dialog = new AddEditExerciseDialog();
        dialog.mEditMode = editMode;

        Bundle args = new Bundle(3);
        if (editMode && exercise != null) {
            args.putString(KEY_NAME, exercise.name);
            args.putInt(KEY_TYPE, ExerciseTypeConverter.getIntFromExerciseType(exercise.type));
            args.putInt(KEY_COLOR, exercise.color);
            dialog.setArguments(args);
        }
        return dialog;
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {

        final MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(getActivity());
        final View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_add_edit_exercise, null, false);

        final MaterialCheckBox repsBox = v.findViewById(R.id.reps);
        final MaterialCheckBox proxyBox = v.findViewById(R.id.proxy);
        final TextInputLayout nameEditTextLayout = v.findViewById(R.id.name_layout);
        final TextInputEditText nameEditText = v.findViewById(R.id.name);

        repsBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && proxyBox.isChecked()) {
                proxyBox.setChecked(false);
            }
        });

        proxyBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && repsBox.isChecked()) {
                repsBox.setChecked(false);
            }
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                if (s.length() == 0) {
                    nameEditTextLayout.setError(getString(R.string.exercise_dialog_name_error));
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                } else {
                    nameEditTextLayout.setError(null);
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }
            }
        });

        if(mEditMode) {
            final String name = getArguments().getString(KEY_NAME);
            mExerciseName = name;
            nameEditText.setText(name);

            final ExerciseType type =
                    ExerciseTypeConverter.getExerciseTypeFromInt(getArguments().getInt(KEY_TYPE));
            if (type.equals(ExerciseType.TIME_BASED)) {
                // do nothing
            } else if (type.equals(ExerciseType.COUNTABLE)) {
                proxyBox.setChecked(true);
            } else if (type.equals(ExerciseType.REP_BASED)) {
                repsBox.setChecked(true);
            } else {
                Timber.tag(TAG).wtf("Invalid exercise type.");
            }
            b.setNeutralButton(getString(R.string.delete), ((dialog, which) -> {
                //TODO: confirmation
                EventBus.getDefault().post(new Events.DeleteExercise(mExerciseName));
            }));
        }

        Dialog d = b
                .setTitle(mEditMode
                        ? getString(R.string.exercise_dialog_edit_exercise)
                        : getString(R.string.exercise_dialog_add_exercise))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> {
                            if (mExerciseName == null || mExerciseName.length() == 0) {
                                nameEditTextLayout.setError(getString(R.string.exercise_dialog_name_error));
                                return;
                            }
                            EventBus.getDefault().post(mEditMode
                                    ? new Events.EditExercise(
                                    mExerciseName,
                                    getCreatedExercise(
                                            nameEditText.getText().toString(),
                                            repsBox.isChecked(),
                                            proxyBox.isChecked()))
                                    : new Events.AddExercise(
                                    getCreatedExercise(
                                            nameEditText.getText().toString(),
                                            repsBox.isChecked(),
                                            proxyBox.isChecked())));
                        }
                )
                .setNegativeButton(android.R.string.cancel, ((dialog, which) -> {}))
                .setView(v)
                .create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }

    private Exercise getCreatedExercise(String name, boolean repsRelevant, boolean proxyRelevant) {
        ExerciseType type = ExerciseType.TIME_BASED;

        if (proxyRelevant) {
            type = ExerciseType.COUNTABLE;
        } else if (repsRelevant){
            type = ExerciseType.REP_BASED;
        }
        return new Exercise(name, type);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mEditMode) {
            AlertDialog dialog = (AlertDialog) getDialog();
            if (dialog != null) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        }
    }
}
