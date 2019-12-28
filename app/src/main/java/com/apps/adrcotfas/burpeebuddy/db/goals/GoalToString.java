package com.apps.adrcotfas.burpeebuddy.db.goals;

import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalType.AMRAP;
import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalType.REP_BASED;
import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalType.TIME_BASED;

public class GoalToString {
    public static String goalToString(Goal goal) {
        String s = "";

        final int sets = goal.sets;
        GoalType goalType = goal.type;
        if (goalType.equals(AMRAP)) {
            s += sets == 1 ? "" : sets + " x ";
            s += "AMRAP ";
            s += formatSeconds(goal.duration);
        } else if (goalType.equals(TIME_BASED)) {
            s += sets == 1 ? "" : sets + " x ";
            s += formatSeconds(goal.duration);
        } else if (goalType.equals(REP_BASED)) {
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
