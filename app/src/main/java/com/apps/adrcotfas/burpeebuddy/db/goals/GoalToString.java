package com.apps.adrcotfas.burpeebuddy.db.goals;

import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalType.REPS;
import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalType.TIME;

public class GoalToString {
    public static String goalToString(Goal goal) {
        String s = "";

        final int sets = goal.sets;
        GoalType goalType = goal.type;
        if (goalType.equals(TIME)) {
            s += sets == 1 ? "" : sets + " x ";
            s += formatSeconds(goal.duration);
        } else if (goalType.equals(REPS)) {
            s += sets == 1 ? "" : sets + " x ";
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
}
