package com.example.calorietracker;

public class Food {
    private String foodId;
    private String name;
    private String category;
    private int calorieAmount;
    private String servingUnit;
    private int fat;
    private double servingAmount;

    public Food(String foodId, String name, String category, int calorieAmount, String servingUnit, int fat, double servingAmount) {
        this.foodId = foodId;
        this.name = name;
        this.category = category;
        this.calorieAmount = calorieAmount;
        this.servingUnit = servingUnit;
        this.fat = fat;
        this.servingAmount = servingAmount;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCalorieAmount() {
        return calorieAmount;
    }

    public void setCalorieAmount(int calorieAmount) {
        this.calorieAmount = calorieAmount;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public double getServingAmount() {
        return servingAmount;
    }

    public void setServingAmount(double servingAmount) {
        this.servingAmount = servingAmount;
    }

}
