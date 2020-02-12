package com.example.calorietracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class FoodAPI {
    private static final String API_KEY = "217a053122e9fae719dec6929d368439";
    private static final String Application_ID = "feba8120";

    public static String search(String keyword, String[] params, String[] values) {
        keyword = keyword.replace(" ", "+");
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query_parameter = "";
        if (params != null && values != null) {
            for (int i = 0; i < params.length; i++) {
                query_parameter += "&";
                query_parameter += params[i];
                query_parameter += "=";
                query_parameter += values[i];
            }
        }
        try {
            url = new URL("https://api.edamam.com/api/food-database/parser?ingr=" + keyword + "&app_id=" + Application_ID + "&app_key=" + API_KEY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return textResult;
    }

    public static String getPhotoURL(String result) {
        String pic_link = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("hints");
            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject food = jsonArray.getJSONObject(0).getJSONObject("food");
                pic_link = food.getString("image");
            }
        } catch (Exception e) {
            e.printStackTrace();
            pic_link = "NO INFO FOUND";
        }
        //return pic_link;
        return pic_link;
    }

    public static String getCalories(String result) {
        String calories = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("hints");
            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject food = jsonArray.getJSONObject(0).getJSONObject("food");
                JSONObject nutrients = food.getJSONObject("nutrients");
                calories = nutrients.getString("ENERC_KCAL");
            }
        } catch (Exception e) {
            e.printStackTrace();
            calories = "NO INFO FOUND";
        }
        //return calories;
        return calories;
    }

    public static String getFat(String result) {
        String fat = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("hints");
            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject food = jsonArray.getJSONObject(0).getJSONObject("food");
                JSONObject nutrients = food.getJSONObject("nutrients");
                fat = nutrients.getString("FAT");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fat = "NO INFO FOUND";
        }
        //return fat;
        return fat;
    }

    public static String getServingUnit(String result) {
        String servingUnit = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("hints");
            if (jsonArray != null && jsonArray.length() > 0) {
                JSONArray measures = jsonArray.getJSONObject(0).getJSONArray("measures");
                servingUnit = measures.getJSONObject(0).getString("label");
            }
        } catch (Exception e) {
            e.printStackTrace();
            servingUnit = "NO INFO FOUND";
        }
        //return fat;
        return servingUnit;
    }
}
