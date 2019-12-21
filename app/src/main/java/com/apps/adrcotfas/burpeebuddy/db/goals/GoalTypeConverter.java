package com.apps.adrcotfas.burpeebuddy.db.goals;

import androidx.room.TypeConverter;

import static com.apps.adrcotfas.burpeebuddy.db.goals.GoalType.INVALID;

public class GoalTypeConverter {

    @TypeConverter
    public GoalType getGoalFromInt(int data) {
        for (GoalType goal : GoalType.values()) {
            if (goal.getValue() == data) {
                return goal;
            }
        }
        return INVALID;
    }

    @TypeConverter
    public int getIntFromGoal(GoalType goal) {
        return goal.getValue();
    }
}
