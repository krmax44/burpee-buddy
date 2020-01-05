package com.apps.adrcotfas.burpeebuddy.db.goals;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Goal {

    public static int DEFAULT_SETS = 3;
    public static int DEFAULT_REPS = 10;
    public static int DEFAULT_DURATION = 60;
    public static int DEFAULT_BREAK = 30;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @TypeConverters(GoalTypeConverter.class)
    public GoalType type;

    /**
     * The number of sets for an exercise
     */
    public int sets;

    /**
     * The number of reps per set for an exercise.
     * This is not used for time based exercises
     */
    public int reps;

    /**
     * The workout duration per set in seconds
     */
    public int duration;

    /**
     * The break duration between sets in seconds
     */
    public int duration_break;

    public int color;

    /**
     * Time oriented goal
     * e.g. Plank 3 x 1 minute plank with 30 seconds between sets
     */
    public static Goal getTimeBasedGoal(int sets, int duration, int duration_break) {
        return new Goal(GoalType.TIME, sets, DEFAULT_REPS, duration, duration_break);

    }

    /**
     * Rep oriented goal with automatic counting
     * e.g. Burpees 3 x 25 reps
     */
    public static Goal getRepBasedGoal(int sets, int reps, int duration_break) {
        return new Goal(GoalType.REPS, sets, reps, DEFAULT_DURATION, duration_break);
    }

    @Ignore
    public Goal() {
        this.type = GoalType.REPS;
        this.sets = DEFAULT_SETS;
        this.reps = DEFAULT_REPS;
        this.duration = DEFAULT_DURATION;
        this.duration_break = DEFAULT_BREAK;
    }

    public Goal(GoalType type, int sets, int reps, int duration, int duration_break) {
        this.id = 0;
        this.type = type;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
        this.duration_break = duration_break;
        this.color = 0;
    }
}
