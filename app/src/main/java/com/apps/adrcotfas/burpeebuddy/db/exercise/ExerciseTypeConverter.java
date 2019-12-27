package com.apps.adrcotfas.burpeebuddy.db.exercise;

import androidx.room.TypeConverter;

import static com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType.INVALID;

public class ExerciseTypeConverter {

    @TypeConverter
    public static ExerciseType getExerciseTypeFromInt(int data) {
        for (ExerciseType type : ExerciseType.values()) {
            if (type.getValue() == data) {
                return type;
            }
        }
        return INVALID;
    }

    @TypeConverter
    public static int getIntFromExerciseType(ExerciseType type) {
        return type.getValue();
    }
}
