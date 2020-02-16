package com.apps.adrcotfas.burpeebuddy.db.goal;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface GoalDao {

    @TypeConverters(GoalTypeConverter.class)
    @Query("select * from Goal order by type, sets")
    LiveData<List<Goal>> getAll();

    @TypeConverters(GoalTypeConverter.class)
    @Query("select * from Goal where type = :type order by sets")
    LiveData<List<Goal>> getGoals(GoalType type);

    @Insert(onConflict = IGNORE)
    void addGoal(Goal goal);

    @Insert(onConflict = IGNORE)
    void insertAll(List<Goal> goals);

    @TypeConverters(GoalTypeConverter.class)
    @Query("update Goal SET `type` = :type, 'sets' = :sets, 'reps' = :reps, 'duration' = :duration, 'duration_break' = :duration_break WHERE id = :id")
    void editGoal(int id, GoalType type, int sets, int reps, int duration, int duration_break);

    @Query("delete from Goal where id = :id")
    void deleteGoal(int id);
}
