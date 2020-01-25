package com.apps.adrcotfas.burpeebuddy.db.workout;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseType;
import com.apps.adrcotfas.burpeebuddy.db.exercise.ExerciseTypeConverter;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WorkoutDao {

    @Query("select * from Workout order by timestamp desc")
    LiveData<List<Workout>> getAll();

    @Query("select * from Workout where id = :id")
    Workout getWorkout(int id);

    @Insert(onConflict = REPLACE)
    void addWorkout(Workout workout);

    @TypeConverters(ExerciseTypeConverter.class)
    @Query("update Workout SET `type` = :type, 'timestamp' = :timestamp, " +
            "'duration' = :duration, 'reps' = :reps, 'pace' = :pace WHERE id = :id")
    void editWorkout(int id, ExerciseType type, long timestamp, int duration, int reps, double pace);

    @Query("delete from Workout where id = :id")
    void delete(int id);
}
