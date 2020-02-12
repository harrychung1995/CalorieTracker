package com.example.calorietracker;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyDaileyDietFragment_AddFood extends Fragment {
    View vMyDailyDietAddFood;
    Spinner sCategory;
    private ImageView foodImage;
    List<String> categoryList = new ArrayList<String>();
    String calorieAmount, servingUnit, fat;
    private static int inc = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMyDailyDietAddFood = inflater.inflate(R.layout.fragment_my_dailey_diet_add_food, container, false);
        foodImage = (ImageView) vMyDailyDietAddFood.findViewById(R.id.pic_food);
        sCategory = (Spinner) vMyDailyDietAddFood.findViewById(R.id.food_category_spinner);
        GetFoodCategory getFoodCategory = new GetFoodCategory();
        getFoodCategory.execute();
        final EditText insertFood = (EditText) vMyDailyDietAddFood.findViewById(R.id.insertFood);
        Button btnSearch = (Button) vMyDailyDietAddFood.findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = sCategory.getSelectedItem().toString() + " " + insertFood.getText().toString();
                SearchFoodAsyncTask searchFoodAsyncTask = new SearchFoodAsyncTask();
                searchFoodAsyncTask.execute(keyword);
                SearchNutritionAsyncTask searchNutritionAsyncTask = new SearchNutritionAsyncTask();
                searchNutritionAsyncTask.execute(keyword);
            }
        });

        Button btnSubmit = (Button) vMyDailyDietAddFood.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodPostAsyncTask foodPostAsyncTask = new FoodPostAsyncTask();
                String strFoodId = String.valueOf(Long.parseLong(String.valueOf(System.currentTimeMillis())
                        .substring(1, 9)
                        .concat(String.valueOf(inc))));
                String name = insertFood.getText().toString();
                String category = sCategory.getSelectedItem().toString();
                if (name.isEmpty()) {
                    insertFood.setError("Food name is required!");
                    return;
                }
                if (calorieAmount == null) {
                    btnSearch.setError("Please search to access food details!");
                    return;
                }
                foodPostAsyncTask.execute(strFoodId, name, category, calorieAmount, servingUnit, fat, "1");
            }
        });
        return vMyDailyDietAddFood;
    }

    private class GetFoodCategory extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
            String food = RestClient.findAllFood();
            String jsonFoodItem = "{\"" + "food" + "\":" + food + "}";
            try {
                JSONObject jsonObject = new JSONObject(jsonFoodItem);
                JSONArray items = jsonObject.getJSONArray("food");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject u = items.getJSONObject(i);
                    String category = u.getString("category");
                    if (!categoryList.contains(category))
                        categoryList.add(category);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "JSON parsing error: " + e.getMessage());
            }
            return categoryList;
        }

        @Override
        protected void onPostExecute(List<String> details) {
            List<String> categoryList = details;
            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext()
                    , android.R.layout.simple_spinner_item, categoryList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sCategory.setAdapter(spinnerAdapter);
        }
    }

    private class SearchNutritionAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return FoodAPI.search(params[0], new String[]{"num"}, new String[]{"1"});
        }

        @Override
        protected void onPostExecute(String result) {
            //calorie
            TextView cal_amount = (TextView) vMyDailyDietAddFood.findViewById(R.id.food_cal_amount);
            calorieAmount = FoodAPI.getCalories(result);
            cal_amount.setText(calorieAmount);
            //serving unit
            TextView serving_unit = (TextView) vMyDailyDietAddFood.findViewById(R.id.food_serving_unit_value);
            servingUnit = FoodAPI.getServingUnit(result);
            serving_unit.setText(servingUnit);
            //fat
            TextView fat_amount = (TextView) vMyDailyDietAddFood.findViewById(R.id.food_fat_amount);
            fat = FoodAPI.getFat(result);
            fat_amount.setText(fat);
            //photo
            ImageView foodImage = (ImageView) vMyDailyDietAddFood.findViewById(R.id.pic_food);
            loadImageFromURL(FoodAPI.getPhotoURL(result));
        }
    }

    private class SearchFoodAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return SearchGoogleAPI.search(params[0], new String[]{"num"}, new String[]{"1"});
        }

        @Override
    protected void onPostExecute(String result) {
        TextView tv = (TextView) vMyDailyDietAddFood.findViewById(R.id.tv_desc);
        tv.setText(SearchGoogleAPI.getSnippet(result));
    }
}

    private class FoodPostAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Food food = new Food(params[0], params[1], params[2], Double.valueOf(params[3]).intValue(),
                    params[4], Double.valueOf(params[5]).intValue(), Double.valueOf(params[6]));
            RestClient.createFood(food);
            return "New food was added.";
        }

        @Override
        protected void onPostExecute(String response) {
            TextView tv_feedback = (TextView) vMyDailyDietAddFood.findViewById(R.id.tv_feedback);
            tv_feedback.setText(response);
        }
    }

    private void loadImageFromURL(String url) {
        Picasso.with(getActivity().getApplicationContext()).load(url).placeholder(R.mipmap.ic_launcher)
                .into(foodImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {
                    }
                });
    }
}


