package com.apps.adrcotfas.burpeebuddy.settings;

import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.goal.Goal;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalTypeConverter;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class SettingsHelper {
    private static final String IS_FIRST_RUN = "pref_is_first_run";
    private static final String SHOW_START_SNACK = "pref_show_start_snack";

    public final static String AUTO_START_BREAK_COUNTABLE = "pref_auto_start_break_countable";
    public final static String AUTO_START_BREAK_UNCOUNTABLE = "pref_auto_start_break_uncountable";
    public final static String AUTO_START_BREAK_TIME_BASED = "pref_auto_start_break_time_based";

    public final static String GOAL_VIEW_FAVORITES_VISIBLE = "pref_goal_view_favorites";
    public final static String GOAL_TYPE = "pref_goal_type";
    public final static String GOAL_SETS = "pref_goal_sets";
    public final static String GOAL_REPS = "pref_goal_reps";
    public final static String GOAL_DURATION = "pref_goal_duration";
    public final static String GOAL_BREAK = "pref_goal_duration_break";

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

    public static boolean isGoalFavoritesVisible() {
        return BuddyApplication.getPrivatePreferences().getBoolean(GOAL_VIEW_FAVORITES_VISIBLE, false);
    }

    public static void setGoalFavoritesVisibility(boolean visible) {
        BuddyApplication.getPrivatePreferences().edit()
                .putBoolean(GOAL_VIEW_FAVORITES_VISIBLE, visible).apply();
    }

    public static GoalType getGoalType() {
        return GoalTypeConverter.getGoalTypeFromInt(BuddyApplication.getPrivatePreferences().getInt(
                GOAL_TYPE,
                GoalTypeConverter.getIntFromGoal(GoalType.REPS)));
    }

    public static void setGoalType(GoalType type) {
        BuddyApplication.getPrivatePreferences().edit()
                .putInt(GOAL_TYPE, GoalTypeConverter.getIntFromGoal(type)).apply();
    }

    public static int getGoalSets() {
        return BuddyApplication.getPrivatePreferences().getInt(GOAL_SETS, 3);
    }

    public static void setGoalSets(int sets) {
        BuddyApplication.getPrivatePreferences().edit()
                .putInt(GOAL_SETS, sets).apply();
    }

    public static int getGoalReps() {
        return BuddyApplication.getPrivatePreferences().getInt(GOAL_REPS, 15);
    }

    public static void setGoalReps(int reps) {
        BuddyApplication.getPrivatePreferences().edit()
                .putInt(GOAL_REPS, reps).apply();
    }

    public static int getGoalDuration() {
        return BuddyApplication.getPrivatePreferences().getInt(GOAL_DURATION, 60);
    }

    public static void setGoalDuration(int seconds) {
        BuddyApplication.getPrivatePreferences().edit()
                .putInt(GOAL_DURATION, seconds).apply();
    }

    public static int getGoalBreak() {
        return BuddyApplication.getPrivatePreferences().getInt(GOAL_BREAK, 30);
    }

    public static void setGoalBreak(int seconds) {
        BuddyApplication.getPrivatePreferences().edit()
                .putInt(GOAL_BREAK, seconds).apply();
    }

    public static Goal getGoal() {
        return new Goal(getGoalType(), getGoalSets(), getGoalReps(), getGoalDuration(), getGoalBreak());
    }
}
