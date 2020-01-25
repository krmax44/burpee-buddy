package com.apps.adrcotfas.burpeebuddy.db.challenge;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.apps.adrcotfas.burpeebuddy.db.exercise.Exercise;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalType;
import com.apps.adrcotfas.burpeebuddy.db.goals.GoalTypeConverter;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {@ForeignKey(
        entity = Exercise.class,
        parentColumns = {"name", "type"},
        childColumns = {"exerciseName", "type"},
        onUpdate = CASCADE, onDelete = CASCADE)},
        indices = {@Index(value = {"exerciseName", "type"})})
public class Challenge {

    //TODO: delte this
    @Ignore
    public Challenge(String s) {
        this.exerciseName = s;
    }

    public Challenge(String exerciseName, GoalType type, int reps, int duration, long start, int days) {
        this.id = 0;
        this.exerciseName = exerciseName;
        this.type = type;
        this.reps = reps;
        this.duration = duration;
        this.duration = 0;
        this.start = start;
        this.days = days;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @TypeConverters(GoalTypeConverter.class)
    public GoalType type;

    public String exerciseName;

    int reps; // per day
    int duration;    // per day

    long start; // the first day
    int days;   // number of days
}
