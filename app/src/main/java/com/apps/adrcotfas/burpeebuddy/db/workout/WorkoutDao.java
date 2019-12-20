package com.apps.adrcotfas.burpeebuddy.db.workout;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WorkoutDao {

    @Query("select * from Workout")
    LiveData<List<Workout>> getAll();

    @Query("select * from Workout where id = :id")
    Workout getWorkout(int id);

    @Insert(onConflict = REPLACE)
    void addWorkout(Workout workout);

    @Query("delete from Workout where id = :id")
    void delete(int id);

}
