package com.example.calorietracker.StepsData;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface StepDAO {

    @Query("SELECT * FROM step")
    List<Step> getAll();

    @Query("SELECT * FROM step WHERE id = :userId")
    List<Step> findById (int userId);

    @Insert
    void inserAll(Step... Steps);

    @Insert  (onConflict = REPLACE)
    long insert (Step step);

    @Delete
    void delete (Step step);

    @Update (onConflict = REPLACE)
    public void updateSteps(Step... steps);

    @Query("DELETE FROM step")
    void deleteAll();

}