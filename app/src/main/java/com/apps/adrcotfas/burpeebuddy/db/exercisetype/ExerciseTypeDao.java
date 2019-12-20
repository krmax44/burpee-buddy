package com.apps.adrcotfas.burpeebuddy.db.exercisetype;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ExerciseTypeDao {

    @Query("select * from ExerciseType")
    LiveData<List<ExerciseType>> getAll();

    @Query("select * from ExerciseType where name = :name")
    ExerciseType getExercise(String name);

    @Insert(onConflict = REPLACE)
    void addExercise(ExerciseType exerciseType);

    @Insert(onConflict = REPLACE)
    void insertAll(List<ExerciseType> exerciseTypes);

    @Query("delete from ExerciseType where name = :name")
    void delete(String name);
}
