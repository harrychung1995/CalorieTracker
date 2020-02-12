package com.example.calorietracker.StepsData;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(primaryKeys = {"time","id"})
public class Step {
    @ColumnInfo (name = "time")
    @NonNull
    public String time;

    @ColumnInfo (name = "id")
    @NonNull
    public int id;

    @ColumnInfo (name = "Steps")
    public int steps;

    public Step (int id, String time, int steps) {
        this.id = id;
        this.time = time;
        this.steps =steps;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public int getSteps() {return steps;}

    public void setTime(String time) {this.time = time;}

    public void setSteps(int steps) {this.steps = steps;}


}
