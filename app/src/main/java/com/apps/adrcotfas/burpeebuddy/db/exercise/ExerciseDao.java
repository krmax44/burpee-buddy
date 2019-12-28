package com.apps.adrcotfas.burpeebuddy.db.exercise;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ExerciseDao {

    @Query("select * from Exercise ORDER BY `order`")
    LiveData<List<Exercise>> getAll();

    @Query("select * from Exercise where name = :name")
    Exercise getExercise(String name);

    @Query("select * from Exercise where visible = 1 ORDER BY `order`")
    LiveData<List<Exercise>> getAllVisible();

    @Insert(onConflict = REPLACE)
    void addExercise(Exercise exercise);

    @Insert(onConflict = REPLACE)
    void insertAll(List<Exercise> exercises);

    @Query("delete from Exercise where name = :name")
    void delete(String name);

    @Query("update Exercise SET `order` = :order WHERE name = :name")
    void editOrder(String name, int order);

    @TypeConverters(ExerciseTypeConverter.class)
    @Query("update Exercise SET `name` = :newName, 'type' = :type, 'color' = :color WHERE name = :name")
    void editExercise(String name, String newName, ExerciseType type, int color);

    @Query("update Exercise SET visible= :visibility WHERE name = :name")
    void editVisibility(String name, boolean visibility);
}
