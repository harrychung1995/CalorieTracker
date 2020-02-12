package com.example.calorietracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class SearchGoogleAPI {
    private static final String API_KEY = "AIzaSyBPi6vL9KnVgvd6hsBBSgJLd0gj2tBduio";
    private static final String SEARCH_ID_cx = "007367754765449206127:swienz4gwss";

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
            url = new URL("https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" + SEARCH_ID_cx + "&q=" + keyword + query_parameter);
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
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject pagemap = jsonArray.getJSONObject(0).getJSONObject("pagemap");
                JSONArray cse_image = pagemap.getJSONArray("cse_image");
                pic_link = cse_image.getJSONObject(0).getString("src");
            }
        } catch (Exception e) {
            e.printStackTrace();
            pic_link = "NO INFO FOUND";
        }
        //return pic_link;
        return pic_link;
    }

    public static String getSnippet(String result) {
        String snippet = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if (jsonArray != null && jsonArray.length() > 0) {
                snippet = jsonArray.getJSONObject(0).getString("snippet");
            }
        } catch (Exception e) {
            e.printStackTrace();
            snippet = "NO INFO FOUND";
        }
        int index =snippet.indexOf(".") + 1;
        String trim_snippet = snippet.substring(0, index);
        //return trim_snippet;
        return trim_snippet;
    }

}