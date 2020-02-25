package com.apps.adrcotfas.burpeebuddy.settings;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.preference.Preference;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.takisoft.preferencex.PreferenceFragmentCompat;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class SettingsFragment extends PreferenceFragmentCompat implements TimePickerDialog.OnTimeSetListener {

    private Preference timePreference;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        timePreference = findPreference(SettingsHelper.REMINDER_TIME);
        timePreference.setSummary(StringUtils.formatTime(SettingsHelper.getTimeOfReminder()));
        timePreference.setOnPreferenceClickListener(preference -> {
            final long millis = SettingsHelper.getTimeOfReminder();
            final DateTime time = new DateTime(millis);

            TimePickerDialog d = new TimePickerDialog(
                    getActivity(),
                    R.style.DialogStyle,
                    SettingsFragment.this,
                    time.getHourOfDay(),
                    time.getMinuteOfHour(),
                    DateFormat.is24HourFormat(getContext()));
            d.show();
            return true;
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final long millis = new LocalTime(hourOfDay, minute).toDateTimeToday().getMillis();
        SettingsHelper.setTimeOfReminder(millis);
        timePreference.setSummary(StringUtils.formatTime(millis));
    }
}
