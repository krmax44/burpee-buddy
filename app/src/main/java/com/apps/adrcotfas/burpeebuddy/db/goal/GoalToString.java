package com.apps.adrcotfas.burpeebuddy.db.goal;

import static com.apps.adrcotfas.burpeebuddy.db.goal.GoalType.REPS;
import static com.apps.adrcotfas.burpeebuddy.db.goal.GoalType.TIME;

public class GoalToString {
    public static String goalToString(Goal goal) {
        String s = "";

        final int sets = goal.sets;
        GoalType goalType = goal.type;
        if (goalType.equals(TIME)) {
            s += sets == 1 ? "" : sets + " × ";
            s += formatSeconds(goal.duration);
        } else if (goalType.equals(REPS)) {
            s += sets == 1 ? "" : sets + " × ";
            s += goal.reps + " reps";
        }

        return s;
    }

    public static String formatSeconds(int seconds) {

        final int minutes = seconds / 60;
        final int hours = minutes / 60;
        final int remSec = seconds % 60;

        String result;
        if (seconds != 0) {
            result =  (hours != 0 ? "" + hours + "h" : "")
                    + (minutes != 0 ? (hours != 0 ? " " : "") + minutes + "min" : "")
                    + (remSec != 0 ? (minutes != 0 ? " " : "") + remSec + "s" : "");
        } else {
            result = "0s";
        }
        return result;
    }

    public static String formatSecondsAlt(int seconds) {

        final int minutes = seconds / 60;
        final int remSec = seconds % 60;

        String result;
        if (seconds != 0) {
            result =  minutes >= 10
                    ? minutes + ":" + (remSec < 10 ? "0" + remSec : remSec)
                    : "0" + minutes + ":" + (remSec < 10 ? "0" + remSec : remSec);
        } else {
            result = "00:00";
        }
        return result;
    }
}
