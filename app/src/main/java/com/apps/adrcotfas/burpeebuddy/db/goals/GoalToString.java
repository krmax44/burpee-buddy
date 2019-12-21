package com.apps.adrcotfas.burpeebuddy.db.goals;

import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalType.AMRAP;
import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalType.REP_BASED;
import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalType.TIME_BASED;

public class GoalToString {
    public static String goalToString(Goal goal) {
        String s = "";

        final int sets = goal.getSets();
        GoalType goalType = goal.getType();
        if (goalType.equals(AMRAP)) {
            s += sets == 1 ? "" : sets + " x ";
            s += "AMRAP ";
            s += formatSeconds(goal.getDuration());
        } else if (goalType.equals(TIME_BASED)) {
            s += sets == 1 ? "" : sets + " x ";
            s += formatSeconds(goal.getDuration());
        } else if (goalType.equals(REP_BASED)) {
            s += sets == 1 ? "" : sets + " x ";
            s += goal.getReps() + " reps";
        }

        return s;
    }

    private static String formatSeconds(int seconds) {

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
