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
                parentColumns = {"name", "type"},
                childColumns = {"exerciseName", "type"},
                onUpdate = CASCADE, onDelete = CASCADE)},
        indices = {@Index(value = {"exerciseName", "type"})})
public class Workout {

    public Workout() {
        this.id = 0;
        this.exerciseName = "";
        this.type = ExerciseType.INVALID;
        this.timestamp = System.currentTimeMillis();
        this.reps = 0;
        this.duration = 0;
        this.pace = 0;
    }

    @Ignore
    public Workout(String exerciseName, ExerciseType type, long timestamp, int duration, int reps,
            double pace) {
        this.id = 0;
        this.exerciseName = exerciseName;
        this.type = type;
        this.timestamp =  timestamp;
        this.reps = reps;
        this.duration = duration;
        this.pace = pace;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String exerciseName;
    @TypeConverters(ExerciseTypeConverter.class)
    public ExerciseType type;

    public long timestamp;
    public int duration;  // [seconds]
    public int reps;      // [number of reps]
    public double pace;   // [reps/min] or [min/km] or [min/mile]

    // TODO maybe later: public float weight;
    // TODO maybe later: public float distance;
    // TODO maybe later: think about auto-pause when combining with other exercises
}
