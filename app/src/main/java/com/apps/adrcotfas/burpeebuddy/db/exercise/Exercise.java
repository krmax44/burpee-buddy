package com.apps.adrcotfas.burpeebuddy.db.exercise;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(indices = {@Index(value = {"name", "type", "color"}, unique = true)})
public class Exercise implements Parcelable {

    /**
     * The workout name, which can be pre-defined like burpees, plank
     * or something custom added by the user.
     */
    @PrimaryKey
    @NonNull
    public String name;

    @TypeConverters(ExerciseTypeConverter.class)
    public ExerciseType type;

    /**
     * Index of the color used to represent this workout type.
     */
    public int color;

    public boolean visible;
    public int order;

    @Ignore
    public Exercise() {
        this.name = "";
        this.type = ExerciseType.INVALID;
        this.color = 0;
        this.visible = true;
        this.order = Integer.MAX_VALUE;
    }

    public Exercise(String name, ExerciseType type) {
        this.name = name;
        this.type = type;
        this.color = 0; //TODO: implement this
        this.visible = true;
        this.order = Integer.MAX_VALUE;
    }

    /**
     * Parcelable stuff bellow
     */
    protected Exercise(Parcel in) {
        name = in.readString();
        type = ExerciseTypeConverter.getExerciseTypeFromInt(in.readInt());
        color = in.readInt();
        visible = in.readBoolean();
        order = in.readInt();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {

        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(type.getValue());
        dest.writeInt(color);
        dest.writeBoolean(visible);
        dest.writeInt(order);
    }
}
