package com.apps.adrcotfas.burpeebuddy.db.exercisetype;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

@Entity(indices = {@Index(value = {"name", "metrics", "color"}, unique = true)})
public class ExerciseType {

    /**
     * The workout name, which can be pre-defined like burpees, plank
     * or something custom added by the user.
     */
    @PrimaryKey
    @NonNull
    private String name;

    /**
     * The metrics used in this workout. At least one metric must be added.
     * e.g.
     * - time for plank
     * - reps and [optional]time for push-ups and burpees
     * - weight and time for weighted plank
     * - reps and weight for power clean
     * - time and distance for running
     * - time, distance and weight for running with a weighted vest
     * - reps, time and weight for weighted burpees
     * - etc
     */
    @TypeConverters(MetricConverter.class)
    @NonNull
    private List<String> metrics;

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

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(ArrayList<String> metrics) {
        this.metrics = metrics;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ExerciseType(String name, List<String> metrics) {

        if ((metrics.size() <= 0)) throw new AssertionError();

        this.name = name;
        this.metrics = metrics;
        this.color = 0; //TODO: implement this
    }
}
