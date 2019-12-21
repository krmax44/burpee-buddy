package com.apps.adrcotfas.burpeebuddy.db.exercisetype;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ExerciseTypeDao {

    @Query("select * from Exercise")
    LiveData<List<Exercise>> getAll();

    @Query("select * from Exercise where name = :name")
    Exercise getExercise(String name);

    @Insert(onConflict = REPLACE)
    void addExercise(Exercise exercise);

    @Insert(onConflict = REPLACE)
    void insertAll(List<Exercise> exercises);

    @Query("delete from Exercise where name = :name")
    void delete(String name);
}
