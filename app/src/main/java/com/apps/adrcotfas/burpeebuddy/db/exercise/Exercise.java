package com.apps.adrcotfas.burpeebuddy.db.exercise;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(indices =
        {@Index(value = {"name", "type"}, unique = true),
         @Index(value = {"name"}, unique = true)})
public class Exercise {

    /**
     * The workout name, which can be pre-defined like burpees, plank
     * or something custom added by the user.
     */
    @PrimaryKey
    @NonNull
    public String name;

    @TypeConverters(ExerciseTypeConverter.class)
    public ExerciseType type;

    public boolean visible;
    public int order;

    @Ignore
    public Exercise() {
        this.name = "";
        this.type = ExerciseType.INVALID;
        this.visible = true;
        this.order = Integer.MAX_VALUE;
    }

    public Exercise(String name, ExerciseType type) {
        this.name = name;
        this.type = type;
        this.visible = true;
        this.order = Integer.MAX_VALUE;
    }

    @Ignore
    public Exercise(String name, ExerciseType type, boolean visible) {
        this.name = name;
        this.type = type;
        this.visible = visible;
        this.order = Integer.MAX_VALUE;
    }
}
