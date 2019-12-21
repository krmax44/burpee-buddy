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

    @TypeConverters(GoalTypeConverter.class)
    @Query("select * from Goal where type = :type")
    LiveData<List<Goal>> getGoals(GoalType type);

    @TypeConverters(GoalTypeConverter.class)
    @Query("select * from Goal where type = 1 or type = 2") // REP_BASED(1) and AMRAP(2)
    LiveData<List<Goal>> getCountableGoals();

    @Insert(onConflict = REPLACE)
    void addGoal(Goal goal);

    @Insert(onConflict = REPLACE)
    void insertAll(List<Goal> goals);
}
