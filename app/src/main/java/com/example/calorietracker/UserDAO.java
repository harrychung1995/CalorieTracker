package com.example.calorietracker;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDAO {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE id = :userId LIMIT 1")
    User findById (int userId);

    @Insert
    void inserAll(User... users);

    @Insert  (onConflict = REPLACE)
    long insert (User user);

    @Delete
    void delete (User user);

    @Update (onConflict = REPLACE)
    public void updateUsers(User... users);

    @Query("DELETE FROM user")
    void deleteAll();

}

