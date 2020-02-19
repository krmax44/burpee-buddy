package com.apps.adrcotfas.burpeebuddy.db.exercise;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface ExerciseDao {

    @Query("select * from Exercise ORDER BY `order`")
    LiveData<List<Exercise>> getAll();

    @Query("select * from Exercise where name = :name")
    Exercise getExercise(String name);

    @Query("select * from Exercise where visible = 1 ORDER BY `order`")
    LiveData<List<Exercise>> getAllVisible();

    @Insert(onConflict = IGNORE)
    void addExercise(Exercise exercise);

    @Insert(onConflict = IGNORE)
    void insertAll(List<Exercise> exercises);

    @Query("delete from Exercise where name = :name")
    void delete(String name);

    @Query("update Exercise SET `order` = :order WHERE name = :name")
    void editOrder(String name, int order);

    @TypeConverters(ExerciseTypeConverter.class)
    @Query("update Exercise SET `name` = :newName, 'type' = :type WHERE name = :name")
    void editExercise(String name, String newName, ExerciseType type);

    @Query("update Exercise SET visible= :visibility WHERE name = :name")
    void editVisibility(String name, boolean visibility);
}
