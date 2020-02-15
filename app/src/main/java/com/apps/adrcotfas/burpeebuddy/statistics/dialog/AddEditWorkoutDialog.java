package com.apps.adrcotfas.burpeebuddy.statistics.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.DialogFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.workout.Workout;
import com.apps.adrcotfas.burpeebuddy.workout.manager.InProgressWorkout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AddEditWorkoutDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "AddEditWorkoutDialog";

    private boolean mEditMode;
    private Workout mWorkout;
    private List<String> mExercisesNames;
    private List<Exercise> mExercises;
    private TextInputEditText mSeconds;
    private TextInputEditText mMinutes;
    private TextView mDate;
    private TextView mTime;

    public static AddEditWorkoutDialog getInstance(Workout workout, boolean editMode) {
        AddEditWorkoutDialog dialog = new AddEditWorkoutDialog();
        dialog.mEditMode = editMode;

        if (editMode && workout != null) {
            dialog.mWorkout  = workout;
        } else {
            dialog.mWorkout = new Workout();
        }
        return dialog;
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {
        final MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(getActivity());

        final View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_add_edit_workout, null, false);

        setupExerciseTextView(v);
        setupRepsEditText(v);
        setupDurationsEditText(v);
        setupDateAndTimeViews(v);

        Dialog d = b
                .setCancelable(false)
                .setTitle(mEditMode ? "Edit Workout" : "Add Workout")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    mWorkout.pace = InProgressWorkout.getAvgPace(mWorkout.reps, mWorkout.duration);
                    EventBus.getDefault().post(mEditMode
                            ? new Events.EditWorkout(mWorkout.id, mWorkout)
                            : new Events.AddWorkout(mWorkout));
                })
                .setNegativeButton(android.R.string.cancel, ((dialog, which) -> {}))
                .setView(v)
                .create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }

    private void setupDateAndTimeViews(View v) {
        mDate = v.findViewById(R.id.edit_date);
        mTime = v.findViewById(R.id.edit_time);
        mDate.setText(StringUtils.formatDate(mWorkout.timestamp));
        mTime.setText(StringUtils.formatTime(mWorkout.timestamp));

        mDate.setOnClickListener(v1 -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog d = DatePickerDialog.newInstance(
                    AddEditWorkoutDialog.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            d.setThemeDark(true);
            d.setAccentColor(getContext().getResources().getColor(R.color.colorPrimary));
            d.setMaxDate(now);
            d.show(getParentFragmentManager(), TAG);
        });

        mTime.setOnClickListener(v12 -> {
            Calendar now = Calendar.getInstance();
            TimePickerDialog d = TimePickerDialog.newInstance(
                    AddEditWorkoutDialog.this,
                    now.get(Calendar.HOUR),
                    now.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(getContext()));
            d.setThemeDark(true);
            d.setAccentColor(getContext().getResources().getColor(R.color.colorPrimary));
            d.show(getParentFragmentManager(), TAG);
        });
    }

    private void setupExerciseTextView(View v) {
        final TextInputLayout nameEditTextLayout = v.findViewById(R.id.name_layout);
        final AppCompatAutoCompleteTextView nameEditText = v.findViewById(R.id.name);

        AppDatabase.getDatabase(getContext()).exerciseDao().getAll().observe(this,
                exercises -> {
                    mExercises = exercises;
                    mExercisesNames = new ArrayList<>();
                    mExercisesNames.clear();
                    for (Exercise e : exercises) {
                        mExercisesNames.add(e.name);
                    }
                    nameEditText.setAdapter(new ArrayAdapter<>(getContext(),
                            R.layout.select_dialog_item, mExercisesNames));
                    if(mEditMode) {
                        nameEditText.setText(mWorkout.exerciseName);
                    }
                });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                if (s.length() == 0 || !mExercisesNames.contains(s.toString())) {
                    nameEditTextLayout.setError(getString(R.string.exercise_dialog_name_error));
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        v.findViewById(R.id.reps_layout).setVisibility(View.GONE);
                        v.findViewById(R.id.minutes_layout).setVisibility(View.GONE);
                        v.findViewById(R.id.seconds_layout).setVisibility(View.GONE);
                    }
                } else {
                    nameEditTextLayout.setError(null);
                    for (Exercise e : mExercises) {
                        if (e.name.equals(s.toString())) {
                            mWorkout.exerciseName = e.name;
                            mWorkout.type = e.type;
                            v.findViewById(R.id.reps_layout).setVisibility(
                                    e.type != ExerciseType.TIME_BASED ? View.VISIBLE : View.GONE);
                            v.findViewById(R.id.minutes_layout).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.seconds_layout).setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void setupRepsEditText(View v) {
        TextInputLayout repsEditTextLayout = v.findViewById(R.id.reps_layout);
        TextInputEditText repsEditText = v.findViewById(R.id.reps);

        if (mEditMode) {
            repsEditText.setText(String.valueOf(mWorkout.reps));
            v.findViewById(R.id.reps_layout).setVisibility(
                    mWorkout.type != ExerciseType.TIME_BASED ? View.VISIBLE : View.GONE);
        }

        repsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                int value = 0;
                if (s.length() == 0 || Integer.valueOf(s.toString()) == 0) {
                    repsEditTextLayout.setError("");
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                } else {
                    repsEditTextLayout.setError(null);
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                    value = Integer.valueOf(s.toString());
                }
                mWorkout.reps = value;
            }
        });
    }

    private void setupDurationsEditText(View v) {
        final TextInputLayout minutesLayout = v.findViewById(R.id.minutes_layout);
        final TextInputLayout secondsLayout = v.findViewById(R.id.seconds_layout);
        mMinutes = v.findViewById(R.id.minutes);
        mSeconds = v.findViewById(R.id.seconds);

        if (mEditMode) {
            mMinutes.setText(String.valueOf(mWorkout.duration / 60));
            mSeconds.setText(String.valueOf(mWorkout.duration % 60));
        }

        mMinutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                if (s.length() == 0) {
                    if (mWorkout.type == ExerciseType.TIME_BASED
                            && (mSeconds.length() == 0 || mSeconds.getText().toString().equals("0"))) {
                        minutesLayout.setError("");
                        if (dialog != null) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }
                } else {
                    minutesLayout.setError(null);
                    if (dialog != null) {
                        if (mWorkout.type == ExerciseType.TIME_BASED) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                    int minutes = Integer.valueOf(s.toString());
                    final String seconds = mSeconds.getText().toString().equals("") ? "0" : mSeconds.getText().toString();
                    mWorkout.duration = (int) TimeUnit.MINUTES.toSeconds(minutes)
                            + Integer.valueOf(seconds);
                }
            }
        });

        mSeconds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                if (s.length() == 0) {
                    if (mWorkout.type == ExerciseType.TIME_BASED
                            && (mMinutes.length() == 0 || mMinutes.getText().toString().equals("0"))) {
                        secondsLayout.setError("");
                        if (dialog != null) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }
                } else {
                    if (Integer.valueOf(s.toString()) > 60 ) {
                        mSeconds.setText(String.valueOf(60));
                    }
                    secondsLayout.setError(null);
                    if (dialog != null) {
                        if (mWorkout.type == ExerciseType.TIME_BASED) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                    int seconds = Integer.valueOf(s.toString());
                    final String minutes = mMinutes.getText().toString().equals("") ? "0" : mMinutes.getText().toString();
                    mWorkout.duration = (int) TimeUnit.MINUTES.toSeconds(Integer.valueOf(minutes))
                            + seconds;
                }
            }
        });
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        final long millis = new DateTime(mWorkout.timestamp)
                .withYear(year)
                .withMonthOfYear(monthOfYear + 1)
                .withDayOfMonth(dayOfMonth).getMillis();
        mWorkout.timestamp = millis;
        mDate.setText(StringUtils.formatDate(millis));
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        final long millis = new DateTime(mWorkout.timestamp)
                .withHourOfDay(hourOfDay)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(second).getMillis();
        mWorkout.timestamp = millis;
        mTime.setText(StringUtils.formatTime(millis));
    }
}
