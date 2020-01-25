package com.apps.adrcotfas.burpeebuddy.db.challenge;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChallengeDao {

    @Query("select * from Challenge")
    LiveData<List<Challenge>> getAll();

    @Insert(onConflict = REPLACE)
    void addChallenge(Challenge challenge);

    @Query("delete from Challenge where id = :id")
    void delete(int id);

}
