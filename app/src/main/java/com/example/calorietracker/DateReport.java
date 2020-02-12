package com.example.calorietracker;

public class DateReport {
    private String date;
    private String totalCalConsumed;
    private String totalCalBurned;

    public DateReport(String date, String totalCalConsumed, String totalCalBurned) {
        this.date = date;
        this.totalCalConsumed = totalCalConsumed;
        this.totalCalBurned = totalCalBurned;
    }

    public String getDate() {
        return date;
    }

    public String getTotalCalConsumed() {
        return totalCalConsumed;
    }

    public String getTotalCalBurned() {
        return totalCalBurned;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTotalCalConsumed(String totalCalConsumed) {
        this.totalCalConsumed = totalCalConsumed;
    }

    public void setTotalCalBurned(String totalCalBurned) {
        this.totalCalBurned = totalCalBurned;
    }
}
