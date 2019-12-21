package com.apps.adrcotfas.burpeebuddy.db.goals;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Goal {

    /**
     * Time oriented goal
     * e.g. Plank 3 x 1 minute plank with 30 seconds between sets
     */
    public static Goal getTimeBasedGoal(int sets, int duration, int duration_break) {
        return new Goal(GoalType.TIME_BASED, sets, 0, duration, duration_break);

    }

    /**
     * Rep oriented goal with automatic counting
     * e.g. Burpees 3 x 25 reps
     */
    public static Goal getRepBasedGoal(int sets, int reps, int duration_break) {
        return new Goal(GoalType.REP_BASED, sets, reps, 0, duration_break);
    }

    /**
     * AMRAP oriented goal wihth or without automatic counting
     * e.g. burpees or pull-ups 3 x AMRAP 3 minute
     */
    public static Goal getAmrapBasedGoal(int sets, int duration, int duration_break) {
        return new Goal(GoalType.AMRAP, sets, 0, duration, duration_break);

    }

    public Goal(GoalType type, int sets, int reps, int duration, int duration_break) {
        this.id = 0;
        this.type = type;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
        this.duration_break = duration_break;
    }

    public int getId() {
        return id;
    }

    @PrimaryKey(autoGenerate = true)
    int id;

    public GoalType getType() {
        return type;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public int getDuration() {
        return duration;
    }

    public int getDurationBreak() {
        return duration_break;
    }

    @TypeConverters(GoalTypeConverter.class)
    private GoalType type;

    /**
     * The number of sets for an exercise
     */
    private int sets;

    /**
     * The number of reps per set for an exercise.
     * This is not used for time based exercises
     */
    private int reps;

    /**
     * The workout duration per set in seconds
     */
    private int duration;

    /**
     * The break duration between sets in seconds
     */
    int duration_break;
}
