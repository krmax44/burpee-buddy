package com.apps.adrcotfas.burpeebuddy.db.goals;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Goal implements Parcelable {

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
        return new Goal(GoalType.TIME_BASED, sets, DEFAULT_REPS, duration, duration_break);

    }

    /**
     * Rep oriented goal with automatic counting
     * e.g. Burpees 3 x 25 reps
     */
    public static Goal getRepBasedGoal(int sets, int reps, int duration_break) {
        return new Goal(GoalType.REP_BASED, sets, reps, DEFAULT_DURATION, duration_break);
    }

    /**
     * AMRAP oriented goal wihth or without automatic counting
     * e.g. burpees or pull-ups 3 x AMRAP 3 minute
     */
    public static Goal getAmrapBasedGoal(int sets, int duration, int duration_break) {
        return new Goal(GoalType.AMRAP, sets, DEFAULT_REPS, duration, duration_break);

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

    /**
     * Parcelable stuff bellow
     */
    protected Goal(Parcel in) {
        id = in.readInt();
        type = GoalTypeConverter.getGoalTypeFromInt(in.readInt());
        sets = in.readInt();
        reps = in.readInt();
        duration = in.readInt();
        duration_break = in.readInt();
    }

    public static final Creator<Goal> CREATOR = new Creator<Goal>() {
        @Override
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        @Override
        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(type.getValue());
        dest.writeInt(sets);
        dest.writeInt(reps);
        dest.writeInt(duration);
        dest.writeInt(duration_break);
    }
}
