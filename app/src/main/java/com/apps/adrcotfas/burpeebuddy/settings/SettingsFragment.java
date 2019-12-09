package com.apps.adrcotfas.burpeebuddy.settings;


import android.os.Bundle;

import androidx.preference.SwitchPreferenceCompat;

import com.apps.adrcotfas.burpeebuddy.R;
import com.takisoft.preferencex.EditTextPreference;
import com.takisoft.preferencex.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        //TODO: clean-up

        //TODO: start activity for result for the special lock permission here

        // TODO: give warning to users of S10 and other similar phones
        // proximity sensor does not work when the screen is on
        final EditTextPreference wakeupInterval = findPreference(SettingsHelper.WAKEUP_INTERVAL);
        wakeupInterval.setVisible(SettingsHelper.wakeupEnabled());
        wakeupInterval.setSummary(SettingsHelper.getWakeUpInterval() + " reps");
        wakeupInterval.setOnPreferenceChangeListener((preference, newValue) -> {
            wakeupInterval.setSummary(newValue + " reps");
            return true;
        });

        final SwitchPreferenceCompat enableWakeup = findPreference(SettingsHelper.ENABLE_WAKEUP);
        enableWakeup.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean enable = (boolean)newValue;
            wakeupInterval.setVisible(enable);
            return true;
        });

        final EditTextPreference specialSoundInterval = findPreference(SettingsHelper.SPECIAL_SOUND_INTERVAL);
        specialSoundInterval.setVisible(SettingsHelper.specialSoundEnabled());
        specialSoundInterval.setSummary(SettingsHelper.getSpecialSoundInterval() + " reps");
        specialSoundInterval.setOnPreferenceChangeListener((preference, newValue) -> {
            specialSoundInterval.setSummary(newValue + " reps");
            return true;
        });

        SwitchPreferenceCompat enableSpecialSound = findPreference(SettingsHelper.ENABLE_SPECIAL_SOUND);
        enableSpecialSound.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean enable = (boolean)newValue;
            specialSoundInterval.setVisible(enable);
            return true;
        });



    }
}
