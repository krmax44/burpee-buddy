package com.apps.adrcotfas.burpeebuddy.settings;

import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class SettingsHelper {
    private static final String IS_FIRST_RUN = "pref_is_first_run";
    private static final String SHOW_START_SNACK = "pref_show_start_snack";

    public final static String ENABLE_AUTO_LOCK = "pref_auto_lock";
    public final static String ENABLE_SOUND = "pref_enable_sound";
    public final static String ENABLE_SPECIAL_SOUND = "pref_enable_sound_special";
    public final static String SPECIAL_SOUND_INTERVAL = "pref_sound_special_reps";
    public final static String ENABLE_WAKEUP = "pref_enable_wakeup";
    public final static String WAKEUP_INTERVAL = "pref_wakeup_reps";

    public final static String AUTO_START_BREAK_COUNTABLE = "pref_auto_start_break_countable";
    public final static String AUTO_START_BREAK_TIME_BASED = "pref_auto_start_break_time_based";

    public static boolean autoLockEnabled() {
        return getDefaultSharedPreferences(BuddyApplication.getInstance())
                .getBoolean(ENABLE_AUTO_LOCK, false);
    }

    public static boolean soundEnabled() {
        return getDefaultSharedPreferences(BuddyApplication.getInstance())
                .getBoolean(ENABLE_SOUND, true);
    }

    public static boolean specialSoundEnabled() {
        return getDefaultSharedPreferences(BuddyApplication.getInstance())
                .getBoolean(ENABLE_SPECIAL_SOUND, true);
    }

    public static boolean wakeupEnabled() {
        return getDefaultSharedPreferences(BuddyApplication.getInstance())
                .getBoolean(ENABLE_WAKEUP, false);
    }

    public static int getSpecialSoundInterval() {
        return Integer.parseInt(getDefaultSharedPreferences(BuddyApplication.getInstance())
                .getString(SPECIAL_SOUND_INTERVAL, "5"));
    }

    public static int getWakeUpInterval() {
        return Integer.parseInt(getDefaultSharedPreferences(BuddyApplication.getInstance())
                .getString(WAKEUP_INTERVAL, "10"));
    }

    public static boolean isFirstRun() {
        return BuddyApplication.getPrivatePreferences().getBoolean(IS_FIRST_RUN, true);
    }

    public static void setIsFirstRun(boolean value) {
        BuddyApplication.getPrivatePreferences().edit()
                .putBoolean(IS_FIRST_RUN, value).apply();
    }

    public static boolean showStartSnack() {
        return BuddyApplication.getPrivatePreferences()
                .getBoolean(SHOW_START_SNACK, true);
    }

    public static void setShowStartSnack(boolean value) {
        BuddyApplication.getPrivatePreferences().edit()
                .putBoolean(SHOW_START_SNACK, value).apply();
    }

    public static boolean autoStartBreak(ExerciseType type) {
        if (type.equals(ExerciseType.COUNTABLE)) {
            return getDefaultSharedPreferences(BuddyApplication.getInstance())
                    .getBoolean(AUTO_START_BREAK_COUNTABLE, false);
        } else if (type.equals(ExerciseType.TIME_BASED)) {
            return getDefaultSharedPreferences(BuddyApplication.getInstance())
                    .getBoolean(AUTO_START_BREAK_TIME_BASED, false);
        } else {
            return false;
        }
    }

    public static void setAutoStartBreak(ExerciseType type, boolean autoStartBreak) {
        if (type.equals(ExerciseType.COUNTABLE)) {
            getDefaultSharedPreferences(BuddyApplication.getInstance()).edit()
                    .putBoolean(AUTO_START_BREAK_COUNTABLE, autoStartBreak).apply();
        } else if (type.equals(ExerciseType.TIME_BASED)) {
            getDefaultSharedPreferences(BuddyApplication.getInstance()).edit()
                    .putBoolean(AUTO_START_BREAK_TIME_BASED, autoStartBreak).apply();
        }
    }
}
