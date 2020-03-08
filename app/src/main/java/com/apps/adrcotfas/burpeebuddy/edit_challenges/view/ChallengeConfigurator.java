package com.apps.adrcotfas.burpeebuddy.edit_challenges.view;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.LifecycleOwner;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChallengeConfigurator {

    public interface Listener {
        void onPositiveButtonClick(Challenge challenge);
        void toggleState(boolean valid);
    }

    private Challenge challenge = new Challenge();

    private List<String> exerciseNames;
    private List<Exercise> exercises;

    private TextInputLayout daysLayout;
    private TextInputLayout repsOrMinutesLayout;
    private TextInputLayout secondsLayout;
    private AppCompatAutoCompleteTextView nameEditText;

    private TextInputEditText daysEdit;
    private TextInputEditText secondsEdit;
    private TextInputEditText repsOrMinutesEdit;

    private ImageView x;
    private TextView intervalText;

    private Context context;
    private Listener listener;
    private View v;

    public ChallengeConfigurator(Context context, Listener listener, LifecycleOwner lifecycleOwner, View v) {
        this.context = context;
        this.listener = listener;
        this.v = v;

        AppDatabase.getDatabase(context).exerciseDao().getAllVisible().observe(lifecycleOwner,
                e -> {
                    this.exercises = e;
                    findViews(v);
                    setupExerciseTextView(v);
                    setupDaysEditText();
                    setupMetrics();
                    setupStartDate();
                });
    }

    @NonNull
    public Dialog createDialog() {
        final MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(context);
        return b.setCancelable(false)
                .setTitle("Create a challenge")
                .setPositiveButton(android.R.string.ok, (dialog, which)
                        -> listener.onPositiveButtonClick(challenge))
                .setNegativeButton(android.R.string.cancel, ((dialog, which) -> {}))
                .setView(v)
                .create();
    }

    public Challenge getChallenge() {
        return challenge;
    }

    private void findViews(View v) {
        nameEditText = v.findViewById(R.id.name);
        daysLayout = v.findViewById(R.id.days_layout);
        daysEdit = v.findViewById(R.id.days);
        repsOrMinutesLayout = v.findViewById(R.id.reps_or_minutes_layout);
        repsOrMinutesEdit = v.findViewById(R.id.reps_or_minutes);
        secondsLayout = v.findViewById(R.id.seconds_layout);
        secondsEdit = v.findViewById(R.id.seconds);
        x = v.findViewById(R.id.cross);
        intervalText = v.findViewById(R.id.interval_text);
    }

    private void setupExerciseTextView(View v) {
        final TextInputLayout nameEditTextLayout = v.findViewById(R.id.name_layout);

        exerciseNames = new ArrayList<>();
        exerciseNames.clear();
        for (Exercise e : exercises) {
            this.exerciseNames.add(e.name);
        }
        nameEditText.setAdapter(new ArrayAdapter<>(context,
                R.layout.select_dialog_item, exerciseNames));

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || !exerciseNames.contains(s.toString())) {
                    nameEditTextLayout.setError(context.getString(R.string.exercise_dialog_name_error));

                    listener.toggleState(false);
                    daysLayout.setVisibility(View.GONE);
                    x.setVisibility(View.GONE);
                    repsOrMinutesLayout.setVisibility(View.GONE);
                    secondsLayout.setVisibility(View.GONE);
                    intervalText.setVisibility(View.GONE);
                } else {
                    nameEditTextLayout.setError(null);
                    for (Exercise e : exercises) {
                        if (e.name.equals(s.toString())) {
                            challenge.exerciseName = e.name;

                            daysLayout.setVisibility(View.VISIBLE);
                            x.setVisibility(View.VISIBLE);
                            repsOrMinutesLayout.setVisibility(View.VISIBLE);
                            intervalText.setVisibility(View.VISIBLE);
                            if (e.type != ExerciseType.TIME_BASED ) {
                                challenge.type = GoalType.REPS;
                                repsOrMinutesLayout.setHint(context.getString(R.string.reps));
                                secondsLayout.setVisibility(View.GONE);
                                repsOrMinutesEdit.setText("");
                                secondsEdit.setText("");
                            } else {
                                challenge.type = GoalType.TIME;
                                repsOrMinutesLayout.setHint(context.getString(R.string.min));
                                secondsLayout.setVisibility(View.VISIBLE);
                                repsOrMinutesEdit.setText("");
                                secondsEdit.setText("");
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
                int value = 0;
                try {
                    if (s.length() == 0 || Integer.valueOf(s.toString()) == 0) {
                        listener.toggleState(false);
                    } else {
                        if ((challenge.type == GoalType.REPS
                                && (repsOrMinutesEdit.length() != 0
                                && !repsOrMinutesEdit.getText().toString().equals("0")))

                                || (challenge.type == GoalType.TIME
                                && (repsOrMinutesEdit.length() != 0
                                && !repsOrMinutesEdit.getText().toString().equals("0"))
                                || (secondsEdit.length() != 0
                                && !secondsEdit.getText().toString().equals("0")))) {
                            listener.toggleState(true);
                        }
                        value = Integer.valueOf(s.toString());
                    }
                } catch (NumberFormatException e) {
                    listener.toggleState(false);
                    daysEdit.setText("");
                }

                challenge.days = value;
                final long endMillis = new DateTime(challenge.date).plusDays(challenge.days - 1).getMillis();
                //TODO: extract string
                intervalText.setText(challenge.days == 0 ? "" : "Today until " + StringUtils.formatDate(endMillis));
            }
        });
    }

    private void setupMetrics() {
        repsOrMinutesEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || daysEdit.length() == 0 || daysEdit.getText().toString().equals("0")) {
                    listener.toggleState(false);
                } else {
                    try {
                        Integer val = Integer.valueOf(s.toString());
                        if (val == 0) {
                            listener.toggleState(false);
                            return;
                        }
                        if (challenge.type == GoalType.TIME) {
                            final String seconds = secondsEdit.getText().toString().equals("")
                                    ? "0"
                                    : secondsEdit.getText().toString();
                            challenge.duration = (int) TimeUnit.MINUTES.toSeconds(val)
                                    + Integer.valueOf(seconds);
                            if (challenge.duration != 0) {
                                listener.toggleState(true);
                            }
                        } else {
                            challenge.reps = val;
                            listener.toggleState(true);
                        }
                    } catch (NumberFormatException e) {
                        listener.toggleState(false);
                        repsOrMinutesEdit.setText("");
                    }
                }
            }
        });

        secondsEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || daysEdit.length() == 0 || daysEdit.getText().toString().equals("0")) {
                    listener.toggleState(false);
                } else {
                    try {
                        int seconds = Integer.valueOf(s.toString());
                        if (Integer.valueOf(s.toString()) > 60 ) {
                            secondsEdit.setText(String.valueOf(60));
                            seconds = 60;
                        }
                        final String minutes = repsOrMinutesEdit.getText().toString().equals("")
                                ? "0"
                                : repsOrMinutesEdit.getText().toString();
                        challenge.duration = (int) TimeUnit.MINUTES.toSeconds(Integer.valueOf(minutes))
                                + seconds;
                        if (challenge.duration != 0) {
                            listener.toggleState(true);
                        }
                    } catch (NumberFormatException e) {
                        listener.toggleState(false);
                        secondsEdit.setText("");
                    }
                }
            }
        });
    }

    private void setupStartDate() {
        final DateTime now = new DateTime();
        final DateTime startOfToday = now.toLocalDate().toDateTimeAtStartOfDay(now.getZone());
        challenge.date = startOfToday.getMillis();
        final long endMillis = new DateTime(challenge.date).plusDays(challenge.days - 1).getMillis();
        //TODO: extract string
        intervalText.setText("Today until " + StringUtils.formatDate(endMillis));
    }
}
