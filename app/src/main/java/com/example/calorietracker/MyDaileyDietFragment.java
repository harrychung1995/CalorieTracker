package com.example.calorietracker;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyDaileyDietFragment extends Fragment implements View.OnClickListener {
    View vMyDailyDiet;
    Spinner sCategory;
    Spinner sItem;
    private TextView tvFeedback;
    private EditText foodItemQuantity;
    private Button bSubmit;
    List<String> categoryList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMyDailyDiet = inflater.inflate(R.layout.fragment_my_dailey_diet, container, false);
        sCategory = (Spinner) vMyDailyDiet.findViewById(R.id.food_category_spinner);
        sItem = (Spinner) vMyDailyDiet.findViewById(R.id.food_item_spinner);

        foodItemQuantity = (EditText) vMyDailyDiet.findViewById(R.id.food_item_quantity);
        bSubmit = (Button) vMyDailyDiet.findViewById(R.id.b_submit);
        tvFeedback = (TextView) vMyDailyDiet.findViewById(R.id.tv_feedback);
        bSubmit.setOnClickListener(this);

        GetFoodCategory getFoodCategory = new GetFoodCategory();
        getFoodCategory.execute();

        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = sCategory.getSelectedItem().toString();
                GetFoodItems getFoodItems = new GetFoodItems();
                getFoodItems.execute(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                GetFoodItems getFoodItems = new GetFoodItems();
                getFoodItems.execute(categoryList.get(0));
            }
        });

        Button bNewFood = (Button) vMyDailyDiet.findViewById(R.id.b_newFood);
        bNewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCLick:opening add food fragment");
                MyDaileyDietFragment_AddFood myDaileyDietFragment_addFood = new MyDaileyDietFragment_AddFood();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, myDaileyDietFragment_addFood).commit();
            }
        });
        return vMyDailyDiet;
    }

    @Override
    public void onClick(View v) {
        String quantity = foodItemQuantity.getText().toString();
        if (quantity.isEmpty()) {
            foodItemQuantity.setError("Quantity is required!");
            return;
        }
        if (Integer.valueOf(quantity) == 0) {
            foodItemQuantity.setError("Quantity should not be 0!");
            return;
        }

        //Ready for http post (date, food item, and user ID)
        String selectedItem = sItem.getSelectedItem().toString();
        String userId = String.valueOf(MainActivity.getUserId());
        //Get current data in YYYY-MM-DD format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String date = sdf.format(calendar.getTime());
        ConsumptionPost consumptionPost = new ConsumptionPost();
        consumptionPost.execute(userId, date, selectedItem, quantity);
        String feedback = "New transaction has been updated.";
        tvFeedback.setText(feedback);
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

    private class GetFoodItems extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            //String selectedCategory = sCategory.getSelectedItem().toString();
            String selectedCategory = params[0];

            String categoryItem = RestClient.findByFoodCategory(selectedCategory);
            String jsonCategoryItem = "{\"" + selectedCategory + "\":" + categoryItem + "}";
            List<String> itemList = new ArrayList<String>();
            try {
                JSONObject jsonObject = new JSONObject(jsonCategoryItem);
                JSONArray items = jsonObject.getJSONArray(selectedCategory);
                for (int i = 0; i < items.length(); i++) {
                    JSONObject u = items.getJSONObject(i);
                    String name = u.getString("name");
                    itemList.add(name);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "JSON parsing error: " + e.getMessage());
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(List<String> details) {
            List<String> itemList = details;
            //final Spinner sItem = (Spinner) vMyDailyDiet.findViewById(R.id.food_item_spinner);
            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext()
                    , android.R.layout.simple_spinner_item, itemList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sItem.setAdapter(spinnerAdapter);
        }
    }

    private class ConsumptionPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //consumptionPost.execute(userId, date, selectedItem, quantity);
            String jsonAppUser = RestClient.findByUserId(params[0]);
            String jsonArrayFood = RestClient.findFoodByName(params[2]);
            while(jsonAppUser == null || jsonArrayFood == null) {}
            String jsonFood = jsonArrayFood.substring(1, jsonArrayFood.length() - 1);
            double quantity = Double.valueOf(params[3]);
            String foodId = "";
            try {
                JSONObject food = new JSONObject(jsonFood);
                foodId = food.getString("foodId");
            } catch (final JSONException e) {
                Log.e(TAG, "JSON parsing error: " + e.getMessage());
            }
            String consumptionPK = "\"consumptionPK\":{\"date\":\"" + params[1] + "T01:00:00+11:00\",\"foodId\":\"" + foodId + "\",\"userId\":\"" + params[0] + "\"}";

            String jsonString = "{\"appUser\":" + jsonAppUser + "," + consumptionPK + ",\"food\":" + jsonFood + ",\"quantity\":" + quantity + "}";

            RestClient.createConsumption(jsonString);
            return "New consumption was added.";
        }

        @Override
        protected void onPostExecute(String response) {
            TextView tv_feedback = (TextView) vMyDailyDiet.findViewById(R.id.tv_feedback);
            tv_feedback.setText(response);
        }
    }
}


