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

    private boolean isEditMode;
    private Workout workout;
    private List<String> exercisesNames;
    private List<Exercise> exercises;
    private TextInputEditText seconds;
    private TextInputEditText minutes;
    private TextView date;
    private TextView time;

    public static AddEditWorkoutDialog getInstance(Workout workout, boolean editMode) {
        AddEditWorkoutDialog dialog = new AddEditWorkoutDialog();
        dialog.isEditMode = editMode;

        if (editMode && workout != null) {
            dialog.workout = workout;
        } else {
            dialog.workout = new Workout();
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
                .setTitle(isEditMode ? "Edit Workout" : "Add Workout")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    workout.pace = InProgressWorkout.getAvgPace(workout.reps, workout.duration);
                    EventBus.getDefault().post(isEditMode
                            ? new Events.EditWorkout(workout.id, workout)
                            : new Events.AddWorkout(workout));
                })
                .setNegativeButton(android.R.string.cancel, ((dialog, which) -> {}))
                .setView(v)
                .create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }

    private void setupDateAndTimeViews(View v) {
        date = v.findViewById(R.id.edit_date);
        time = v.findViewById(R.id.edit_time);
        date.setText(StringUtils.formatDate(workout.timestamp));
        time.setText(StringUtils.formatTime(workout.timestamp));

        date.setOnClickListener(v1 -> {
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

        time.setOnClickListener(v12 -> {
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
                    this.exercises = exercises;
                    exercisesNames = new ArrayList<>();
                    exercisesNames.clear();
                    for (Exercise e : exercises) {
                        exercisesNames.add(e.name);
                    }
                    nameEditText.setAdapter(new ArrayAdapter<>(getContext(),
                            R.layout.select_dialog_item, exercisesNames));
                    if(isEditMode) {
                        nameEditText.setText(workout.exerciseName);
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
                if (s.length() == 0 || !exercisesNames.contains(s.toString())) {
                    nameEditTextLayout.setError(getString(R.string.exercise_dialog_name_error));
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        v.findViewById(R.id.reps_layout).setVisibility(View.GONE);
                        v.findViewById(R.id.minutes_layout).setVisibility(View.GONE);
                        v.findViewById(R.id.seconds_layout).setVisibility(View.GONE);
                    }
                } else {
                    nameEditTextLayout.setError(null);
                    for (Exercise e : exercises) {
                        if (e.name.equals(s.toString())) {
                            workout.exerciseName = e.name;
                            workout.type = e.type;
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

        if (isEditMode) {
            repsEditText.setText(String.valueOf(workout.reps));
            v.findViewById(R.id.reps_layout).setVisibility(
                    workout.type != ExerciseType.TIME_BASED ? View.VISIBLE : View.GONE);
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
                workout.reps = value;
            }
        });
    }

    private void setupDurationsEditText(View v) {
        final TextInputLayout minutesLayout = v.findViewById(R.id.minutes_layout);
        final TextInputLayout secondsLayout = v.findViewById(R.id.seconds_layout);
        minutes = v.findViewById(R.id.minutes);
        seconds = v.findViewById(R.id.seconds);

        if (isEditMode) {
            minutes.setText(String.valueOf(workout.duration / 60));
            seconds.setText(String.valueOf(workout.duration % 60));
        }

        minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                if (s.length() == 0) {
                    if (workout.type == ExerciseType.TIME_BASED
                            && (seconds.length() == 0 || seconds.getText().toString().equals("0"))) {
                        minutesLayout.setError("");
                        if (dialog != null) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }
                } else {
                    minutesLayout.setError(null);
                    if (dialog != null) {
                        if (workout.type == ExerciseType.TIME_BASED) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                    int minutes = Integer.valueOf(s.toString());
                    final String seconds = AddEditWorkoutDialog.this.seconds.getText().toString().equals("") ? "0" : AddEditWorkoutDialog.this.seconds.getText().toString();
                    workout.duration = (int) TimeUnit.MINUTES.toSeconds(minutes)
                            + Integer.valueOf(seconds);
                }
            }
        });

        seconds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                if (s.length() == 0) {
                    if (workout.type == ExerciseType.TIME_BASED
                            && (minutes.length() == 0 || minutes.getText().toString().equals("0"))) {
                        secondsLayout.setError("");
                        if (dialog != null) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }
                } else {
                    if (Integer.valueOf(s.toString()) > 60 ) {
                        seconds.setText(String.valueOf(60));
                    }
                    secondsLayout.setError(null);
                    if (dialog != null) {
                        if (workout.type == ExerciseType.TIME_BASED) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                    int seconds = Integer.valueOf(s.toString());
                    final String minutes = AddEditWorkoutDialog.this.minutes.getText().toString().equals("") ? "0" : AddEditWorkoutDialog.this.minutes.getText().toString();
                    workout.duration = (int) TimeUnit.MINUTES.toSeconds(Integer.valueOf(minutes))
                            + seconds;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isEditMode) {
            AlertDialog dialog = (AlertDialog) getDialog();
            if (dialog != null) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        final long millis = new DateTime(workout.timestamp)
                .withYear(year)
                .withMonthOfYear(monthOfYear + 1)
                .withDayOfMonth(dayOfMonth).getMillis();
        workout.timestamp = millis;
        date.setText(StringUtils.formatDate(millis));
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        final long millis = new DateTime(workout.timestamp)
                .withHourOfDay(hourOfDay)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(second).getMillis();
        workout.timestamp = millis;
        time.setText(StringUtils.formatTime(millis));
    }
}
