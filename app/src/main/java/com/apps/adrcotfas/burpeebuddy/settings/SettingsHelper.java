package com.apps.adrcotfas.burpeebuddy.settings;

import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class SettingsHelper {
    private static final String IS_FIRST_RUN = "pref_is_first_run";
    private static final String SHOW_START_SNACK = "pref_show_start_snack";

    public final static String AUTO_START_BREAK_COUNTABLE = "pref_auto_start_break_countable";
    public final static String AUTO_START_BREAK_UNCOUNTABLE = "pref_auto_start_break_uncountable";
    public final static String AUTO_START_BREAK_TIME_BASED = "pref_auto_start_break_time_based";


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
        if (type.equals(ExerciseType.UNCOUNTABLE)) {
            if (BuddyApplication.getWorkoutManager().getWorkout().getGoalType() == GoalType.TIME) {
                return false;
            }
            return getDefaultSharedPreferences(BuddyApplication.getInstance())
                    .getBoolean(AUTO_START_BREAK_UNCOUNTABLE, false);
        } else if (type.equals(ExerciseType.COUNTABLE)) {
            return getDefaultSharedPreferences(BuddyApplication.getInstance())
                    .getBoolean(AUTO_START_BREAK_COUNTABLE, true);
        } else if (type.equals(ExerciseType.TIME_BASED)) {
            return getDefaultSharedPreferences(BuddyApplication.getInstance())
                    .getBoolean(AUTO_START_BREAK_TIME_BASED, false);
        } else {
            return false;
        }
    }

    public static void setAutoStartBreak(ExerciseType type, boolean autoStartBreak) {
        if (type.equals(ExerciseType.UNCOUNTABLE)) {
            getDefaultSharedPreferences(BuddyApplication.getInstance()).edit()
                    .putBoolean(AUTO_START_BREAK_UNCOUNTABLE, autoStartBreak).apply();
        } else if (type.equals(ExerciseType.COUNTABLE)) {
            getDefaultSharedPreferences(BuddyApplication.getInstance()).edit()
                    .putBoolean(AUTO_START_BREAK_COUNTABLE, autoStartBreak).apply();
        } else if (type.equals(ExerciseType.TIME_BASED)) {
            getDefaultSharedPreferences(BuddyApplication.getInstance()).edit()
                    .putBoolean(AUTO_START_BREAK_TIME_BASED, autoStartBreak).apply();
        }
    }
}
