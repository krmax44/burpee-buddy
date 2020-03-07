package com.apps.adrcotfas.burpeebuddy.intro;

import android.app.TimePickerDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.db.AppDatabase;
import com.apps.adrcotfas.burpeebuddy.db.challenge.Challenge;
import com.apps.adrcotfas.burpeebuddy.edit_challenges.view.ChallengeConfigurator;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;
import com.google.android.material.button.MaterialButton;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class IntroCreateChallengeFragment extends SlideFragment implements ChallengeConfigurator.Listener, TimePickerDialog.OnTimeSetListener {

    private ChallengeConfigurator challengeConfigurator;
    private MaterialButton saveButton;
    private LinearLayout reminderContainer;
    private TextView reminderButton;
    private Switch reminderSwitch;

    public IntroCreateChallengeFragment() {}

    public static IntroCreateChallengeFragment newInstance() {
        return new IntroCreateChallengeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.getContext().setTheme(R.style.AppTheme);
        final View v = inflater.inflate(R.layout.fragment_intro_create_challenge, null, false);
        challengeConfigurator = new ChallengeConfigurator(getContext(), this, this, v);

        saveButton = v.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v1 -> {
            AppDatabase.addChallenge(getContext(), challengeConfigurator.getChallenge());
            nextSlide();
        });

        reminderContainer = v.findViewById(R.id.reminder_container);
        reminderButton = v.findViewById(R.id.reminder_time);
        reminderButton.setText(StringUtils.formatTime(getContext(), SettingsHelper.getTimeOfReminder()));
        reminderButton.setOnClickListener(v12 -> {
                final long millis = SettingsHelper.getTimeOfReminder();
                final DateTime time = new DateTime(millis);
                TimePickerDialog d = new TimePickerDialog(
                        getActivity(),
                        R.style.DialogStyle,
                        IntroCreateChallengeFragment.this,
                        time.getHourOfDay(),
                        time.getMinuteOfHour(),
                        DateFormat.is24HourFormat(getContext()));
                d.show();
            });

        reminderSwitch = v.findViewById(R.id.reminder_switch);
        reminderSwitch.setChecked(SettingsHelper.isReminderEnabled());
        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminderButton.setClickable(isChecked);
            reminderButton.setFocusable(isChecked);
            reminderButton.setTextColor(getContext().getResources().getColor(isChecked ? R.color.white : R.color.gray800));
            SettingsHelper.setReminderEnabled(isChecked);
        });
        reminderButton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override public void onPositiveButtonClick(Challenge challenge) {}

    @Override
    public void toggleState(boolean valid) {
        saveButton.setEnabled(valid);
        reminderContainer.setVisibility(valid ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean canGoBackward() {
        return false;
    }

    @Override
    public boolean canGoForward() {
        return saveButton.isEnabled();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final long millis = new LocalTime(hourOfDay, minute).toDateTimeToday().getMillis();
        SettingsHelper.setTimeOfReminder(millis);
        reminderButton.setText(StringUtils.formatTime(getContext(), millis));
    }
}
