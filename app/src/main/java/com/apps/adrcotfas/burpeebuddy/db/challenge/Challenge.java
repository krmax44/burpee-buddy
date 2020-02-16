package com.apps.adrcotfas.burpeebuddy.db.challenge;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalType;
import com.apps.adrcotfas.burpeebuddy.db.goal.GoalTypeConverter;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {@ForeignKey(
        entity = Exercise.class,
        parentColumns = {"name"},
        childColumns = {"exerciseName"},
        onUpdate = CASCADE, onDelete = CASCADE)},
        indices = {@Index(value = {"exerciseName", "type"})})
public class Challenge {

    @Ignore
    public Challenge() {
        this.id = 0;
        this.days = 30; //default
    }

    public Challenge(String exerciseName, GoalType type, int reps, int duration, long date, int days) {
        this.id = 0;
        this.exerciseName = exerciseName;
        this.type = type;
        this.reps = reps;
        this.duration = duration;
        this.date = date;
        this.days = days;
        this.complete = false;
        this.failed = false;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @TypeConverters(GoalTypeConverter.class)
    public GoalType type;

    public String exerciseName;

    public int reps;        // per day
    public int duration;    // per day

    /**
     * The first day in millis
     * When a Challenge is completed, this is updated to reflect the day it was failed or completed
     */
    public long date;

    public int days;

    public boolean complete;
    public boolean failed;
}
