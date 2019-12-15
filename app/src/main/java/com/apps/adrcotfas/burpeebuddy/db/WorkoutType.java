package com.apps.adrcotfas.burpeebuddy.db;

import java.util.ArrayList;
import java.util.List;

public class WorkoutType {

    /**
     * The workout name, which can be pre-defined like burpees, plank
     * or something custom added by the user.
     */
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
    private List<Metrics> metrics;

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

    public List<Metrics> getMetrics() {
        return metrics;
    }

    public void setMetrics(ArrayList<Metrics> metrics) {
        this.metrics = metrics;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    WorkoutType(String name, List<Metrics> metrics) {

        if ((metrics.size() <= 0)) throw new AssertionError();

        this.name = name;
        this.metrics = metrics;
        this.color = 0; //TODO: implement this
    }
}
