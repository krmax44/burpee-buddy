package com.apps.adrcotfas.burpeebuddy.db.challenge;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChallengeDao {

    @Query("select * from Challenge where complete = 0 order by date desc")
    LiveData<List<Challenge>> getInProgress();

    @Query("select * from Challenge where complete = 1 order by date desc")
    LiveData<List<Challenge>> getCompleted();

    @Insert(onConflict = REPLACE)
    void addChallenge(Challenge challenge);

    @Query("delete from Challenge where id = :id")
    void delete(int id);

    @Query("update Challenge set `complete` = 1, 'date' = :date, 'failed' = :failed where id = :id")
    void completeChallenge(int id, long date, boolean failed);
}
