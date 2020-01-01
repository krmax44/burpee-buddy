package com.apps.adrcotfas.burpeebuddy.db.goals;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface GoalDao {

    @Query("select * from Goal")
    LiveData<List<Goal>> getAll();

    @TypeConverters(GoalTypeConverter.class)
    @Query("select * from Goal where type = :type")
    LiveData<List<Goal>> getGoals(GoalType type);

    @Insert(onConflict = REPLACE)
    void addGoal(Goal goal);

    @Insert(onConflict = REPLACE)
    void insertAll(List<Goal> goals);

    @TypeConverters(GoalTypeConverter.class)
    @Query("update Goal SET `type` = :type, 'sets' = :sets, 'reps' = :reps, 'duration' = :duration, 'duration_break' = :duration_break WHERE id = :id")
    void editGoal(int id, GoalType type, int sets, int reps, int duration, int duration_break);

    @Query("delete from Goal where id = :id")
    void deleteGoal(int id);
}
