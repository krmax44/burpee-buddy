package com.apps.adrcotfas.burpeebuddy.db.exercisetype;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(indices = {@Index(value = {"name", "type", "color"}, unique = true)})
public class Exercise {

    /**
     * The workout name, which can be pre-defined like burpees, plank
     * or something custom added by the user.
     */
    @PrimaryKey
    @NonNull
    private String name;

    @TypeConverters(ExerciseTypeConverter.class)
    @NonNull
    private ExerciseType type;

    /**
     * Index of the color used to represent this workout type.
     */
    private int color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExerciseType getType() {
        return type;
    }

    public void setType(ExerciseType type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Exercise(String name, ExerciseType type) {
        this.name = name;
        this.type = type;
        this.color = 0; //TODO: implement this
    }
}
