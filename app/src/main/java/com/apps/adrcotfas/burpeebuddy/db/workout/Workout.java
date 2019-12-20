package com.apps.adrcotfas.burpeebuddy.db.workout;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.apps.adrcotfas.burpeebuddy.db.exercisetype.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.exercisetype.MetricConverter;

import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {@ForeignKey(
                entity = ExerciseType.class,
                parentColumns = {"name", "metrics", "color"},
                childColumns = {"exerciseName", "metrics", "color"},
                onUpdate = CASCADE, onDelete = CASCADE)},
        indices = {@Index(value = {"exerciseName", "metrics", "color"}, unique = true)})
public class Workout {

    @PrimaryKey
    public int id;

    /**
     * part of ExerciseType
     */
    public String exerciseName;
    @TypeConverters(MetricConverter.class)
    public List<String> metrics;
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
