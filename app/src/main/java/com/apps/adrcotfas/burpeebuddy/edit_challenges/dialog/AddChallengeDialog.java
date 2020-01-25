package com.apps.adrcotfas.burpeebuddy.edit_challenges.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.DialogFragment;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.Events;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AddChallengeDialog extends DialogFragment {

    private static final String TAG = "AddChallengeDialog";

    private Challenge challenge = new Challenge();

    private List<String> exerciseNames;
    private List<Exercise> exercises;

    private TextInputLayout daysLayout;
    private TextInputLayout repsOrMinutesLayout;
    private TextInputLayout secondsLayout;

    private TextInputEditText secondsEdit;
    private TextInputEditText repsOrMinutesEdit;

    private TextView startDate;
    private TextView endDate;
    private TextInputEditText daysEdit;

    private LinearLayout intervalContainer;

    public static AddChallengeDialog getInstance() {
        return new AddChallengeDialog();
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstBundle) {
        final MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(getActivity());
        final View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_add_challenge, null, false);

        findViews(v);
        setupExerciseTextView(v);
        setupDaysEditText();
        setupMetrics();
        setupStartDate();

        return b.setCancelable(false)
                .setTitle("Create a challenge")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                    AppDatabase.getDatabase(getContext()).challengeDao().getInProgress().observe(this, challenges -> {
                        boolean found = false;
                        for (Challenge c : challenges) {
                            if (c.exerciseName.equals(challenge.exerciseName)) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            Toast.makeText(getContext(), "There's already a challenge in progress for " + challenge.exerciseName
                                    , Toast.LENGTH_SHORT).show();
                        } else {
                            EventBus.getDefault().post(
                                    new Events.AddChallenge(challenge));
                        }
                    });
                })
                .setNegativeButton(android.R.string.cancel, ((dialog, which) -> {}))
                .setView(v)
                .create();
    }

    private void findViews(View v) {
        daysLayout = v.findViewById(R.id.days_layout);
        daysEdit = v.findViewById(R.id.days);
        repsOrMinutesLayout = v.findViewById(R.id.reps_or_minutes_layout);
        repsOrMinutesEdit = v.findViewById(R.id.reps_or_minutes);
        secondsLayout = v.findViewById(R.id.seconds_layout);
        secondsEdit = v.findViewById(R.id.seconds);
        intervalContainer = v.findViewById(R.id.interval_container);
        startDate = v.findViewById(R.id.edit_start);
        endDate = v.findViewById(R.id.edit_end);
    }

    private void setupExerciseTextView(View v) {
        final TextInputLayout nameEditTextLayout = v.findViewById(R.id.name_layout);
        final AppCompatAutoCompleteTextView nameEditText = v.findViewById(R.id.name);

        AppDatabase.getDatabase(getContext()).exerciseDao().getAll().observe(this,
                exercises -> {
                    this.exercises = exercises;
                    exerciseNames = new ArrayList<>();
                    exerciseNames.clear();
                    for (Exercise e : exercises) {
                        this.exerciseNames.add(e.name);
                    }
                    nameEditText.setAdapter(new ArrayAdapter<>(getContext(),
                            R.layout.select_dialog_item, exerciseNames));
                });
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();

                repsOrMinutesEdit.setText("");
                secondsEdit.setText("");

                if (s.length() == 0 || !exerciseNames.contains(s.toString())) {
                    nameEditTextLayout.setError(getString(R.string.exercise_dialog_name_error));
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                        daysLayout.setVisibility(View.GONE);
                        repsOrMinutesLayout.setVisibility(View.GONE);
                        secondsLayout.setVisibility(View.GONE);
                        intervalContainer.setVisibility(View.GONE);
                    }
                } else {
                    nameEditTextLayout.setError(null);
                    for (Exercise e : exercises) {
                        if (e.name.equals(s.toString())) {
                            challenge.exerciseName = e.name;

                            daysLayout.setVisibility(View.VISIBLE);
                            repsOrMinutesLayout.setVisibility(View.VISIBLE);
                            intervalContainer.setVisibility(View.VISIBLE);
                            if (e.type != ExerciseType.TIME_BASED ) {
                                challenge.type = GoalType.REPS;
                                repsOrMinutesLayout.setHint(getContext().getString(R.string.reps));
                                secondsLayout.setVisibility(View.GONE);
                            } else {
                                challenge.type = GoalType.TIME;
                                repsOrMinutesLayout.setHint(getContext().getString(R.string.min));
                                secondsLayout.setVisibility(View.VISIBLE);
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    private void setupDaysEditText() {
        daysEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                int value = 0;
                if (s.length() == 0 || Integer.valueOf(s.toString()) == 0) {
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                } else {
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                    value = Integer.valueOf(s.toString());
                }
                challenge.days = value;
                final long endMillis = new DateTime(challenge.date).plusDays(challenge.days).getMillis();
                endDate.setText(StringUtils.formatDate(endMillis));
            }
        });
    }

    private void setupMetrics() {
        repsOrMinutesEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                if (s.length() == 0) {
                    if (secondsEdit.length() == 0 || secondsEdit.getText().toString().equals("0")) {
                        if (dialog != null) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }
                } else {
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }

                    if (challenge.type == GoalType.TIME) {
                        int minutes = Integer.valueOf(s.toString());
                        final String seconds = secondsEdit.getText().toString().equals("")
                                ? "0"
                                : secondsEdit.getText().toString();
                        challenge.duration = (int) TimeUnit.MINUTES.toSeconds(minutes)
                                + Integer.valueOf(seconds);
                    } else {
                        challenge.reps = Integer.valueOf(s.toString());
                    }
                }
            }
        });

        secondsEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) getDialog();
                if (s.length() == 0) {
                    if (repsOrMinutesEdit.length() == 0 || repsOrMinutesEdit.getText().toString().equals("0")) {
                        if (dialog != null) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }
                } else {
                    if (Integer.valueOf(s.toString()) > 60 ) {
                        secondsEdit.setText(String.valueOf(60));
                    }
                    if (dialog != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                    int seconds = Integer.valueOf(s.toString());
                    final String minutes = repsOrMinutesEdit.getText().toString().equals("")
                            ? "0"
                            : repsOrMinutesEdit.getText().toString();
                    challenge.duration = (int) TimeUnit.MINUTES.toSeconds(Integer.valueOf(minutes))
                            + seconds;
                }
            }
        });
    }

    private void setupStartDate() {
        Calendar now = Calendar.getInstance();
        challenge.date = now.getTimeInMillis();
        startDate.setText("Today");
        final long endMillis = new DateTime(challenge.date).plusDays(challenge.days).getMillis();
        endDate.setText(StringUtils.formatDate(endMillis));
    }

    @Override
    public void onResume() {
        super.onResume();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
