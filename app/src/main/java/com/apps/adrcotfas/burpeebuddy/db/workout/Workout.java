package com.apps.adrcotfas.burpeebuddy.db.workout;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.apps.adrcotfas.burpeebuddy.db.exercisetype.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseTypeConverter;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {@ForeignKey(
                entity = Exercise.class,
                parentColumns = {"name", "type", "color"},
                childColumns = {"exerciseName", "type", "color"},
                onUpdate = CASCADE, onDelete = CASCADE)},
        indices = {@Index(value = {"exerciseName", "type", "color"}, unique = true)})
public class Workout {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String exerciseName;
    @TypeConverters(ExerciseTypeConverter.class)
    public ExerciseType type;
    public int color;

    /**
     * The time of completion.
     */
    public long timestamp;

    /**
     * the number of reps.
     */
    public int reps;

    /**
     * The number of seconds spent in this workout.
     */
    public int duration;

    // TODO maybe later: public float weight;
    // TODO maybe later: public float distance;
    // TODO maybe later: think about auto-pause when combining with other exercises
}
