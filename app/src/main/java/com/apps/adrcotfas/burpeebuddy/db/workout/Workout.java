package com.apps.adrcotfas.burpeebuddy.db.workout;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseTypeConverter;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {@ForeignKey(
                entity = Exercise.class,
                parentColumns = {"name", "type", "color"},
                childColumns = {"exerciseName", "type", "color"},
                onUpdate = CASCADE, onDelete = CASCADE)},
        indices = {@Index(value = {"exerciseName", "type", "color"}, unique = true)})
public class Workout {

    public Workout(String exerciseName, ExerciseType type, int color, long timestamp, int duration, int reps,
            int distance, double pace, double weight) {
        this.id = 0;
        this.exerciseName = exerciseName;
        this.type = type;
        this.color = color;
        this.timestamp =  timestamp;
        this.reps = reps;
        this.duration = duration;
        this.distance = distance;
        this.pace = pace;
        this.weight = weight;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String exerciseName;
    @TypeConverters(ExerciseTypeConverter.class)
    public ExerciseType type;
    public int color;

    public long timestamp;
    public int duration;  // [seconds]
    public int reps;      // [number of reps]
    public int distance;  // [meters]
    public double weight; // [kg]
    public double pace;   // [reps/min] or [min/km] or [min/mile]

    // TODO maybe later: public float weight;
    // TODO maybe later: public float distance;
    // TODO maybe later: think about auto-pause when combining with other exercises
}
