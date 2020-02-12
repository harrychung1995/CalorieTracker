package com.example.calorietracker;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.calorietracker.StepsData.Step;

@Entity
public class User {
    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo (name = "Set Cal Goal")
    public int set_cal_goal;

    @ColumnInfo (name = "Total Calories Consumed")
    public int total_cal_consumed;

    @ColumnInfo (name = "Total Steps")
    public int total_steps;

    @ColumnInfo (name = "Total Calories Burned")
    public int total_cal_burned;


    public User (int id, int set_cal_goal, int total_cal_consumed, int total_steps, int total_cal_burned) {
        this.id = id;
        this.set_cal_goal =set_cal_goal;
        this.total_cal_consumed = total_cal_consumed;
        this.total_steps = total_steps;
        this.total_cal_burned = total_cal_burned;
    }

    public int getId() {
        return id;
    }

    public int getSet_cal_goal() {return set_cal_goal;}

    public int getTotal_cal_consumed() {return total_cal_consumed;}

    public int getTotal_steps() {return total_steps;}

    public int getTotal_cal_burned() {return total_cal_burned;}

    public void setSet_cal_goal(int set_cal_goal) {this.set_cal_goal = set_cal_goal;}

    public void setTotal_cal_consumed(int total_cal_consumed) {this.total_cal_consumed = total_cal_consumed;}

    public void setTotal_steps(int total_steps) {this.total_steps = total_steps;}

    public void setTotal_cal_burned(int total_cal_burned) {this.total_cal_burned = total_cal_burned;}
}
