package com.example.calorietracker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class RestTest extends Fragment {
    private TextView resultTextView;
    private View vRestTest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vRestTest = inflater.inflate(R.layout.rest_test, container, false);
        super.onCreate(savedInstanceState);
        Button findAllAppUsersBtn = (Button) vRestTest.findViewById(R.id.btnFindAll);
        findAllAppUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestAsyncTask getAllCourses = new RestAsyncTask();
                getAllCourses.execute();
            }
        });
        Button findAllAppUsersNameBtn = (Button) vRestTest.findViewById(R.id.btnFindAllName);
        findAllAppUsersNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestAsyncTask2 getAllCourses = new RestAsyncTask2();
                getAllCourses.execute();
            }
        });

        Button findByUserNameBtn = (Button) vRestTest.findViewById(R.id.btnFindByUsername);
        findByUserNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestAsyncTask3 getAllCourses = new RestAsyncTask3();
                getAllCourses.execute();
            }
        });
        return vRestTest;
    }

    private class RestAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.findAllAppUser();
        }

        @Override
        protected void onPostExecute(String users) {
            TextView resultTextView = (TextView) vRestTest.findViewById(R.id.tvResult);
            resultTextView.setText(users);
        }
    }

    private class RestAsyncTask2 extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String jsonString = RestClient.findAllAppUser();


            int index = jsonString.length() - 1;
            String jsonString2 = jsonString.substring(1,index);

            String abc = "{\"appusers\":[" + jsonString2 + "]}";


            String nameList = "";
            try{
                JSONObject jsonObject = new JSONObject(abc);
                JSONArray users = jsonObject.getJSONArray("appusers");
                nameList = "test ";

                for (int i = 0; i < users.length(); i++) {
                    JSONObject u = users.getJSONObject(i);
                    String name = u.getString("name");
                    nameList = nameList + name + ", ";
                }
            }
            catch(final JSONException e) {
                Log.e(TAG, "JSON parsing error: " + e.getMessage());
                nameList = abc;
            }
            return nameList;
        }

        @Override
        protected void onPostExecute(String users) {
            TextView resultTextView = (TextView) vRestTest.findViewById(R.id.tvResult);
            resultTextView.setText(users);
        }
    }

    private class RestAsyncTask3 extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.findUserByUsername("hchu0004");
        }

        @Override
        protected void onPostExecute(String users) {
            TextView resultTextView = (TextView) vRestTest.findViewById(R.id.tvResult);
            resultTextView.setText(users);
        }
    }
}

/*public class RestTest extends AppCompatActivity {
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_test);
        Button findAllCoursesBtn = (Button) findViewById(R.id.btnFindAll);
        findAllCoursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestAsyncTask getAllCourses = new RestAsyncTask();
                getAllCourses.execute();
            }
        });
    }

    private class RestAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.findAllCourses();
        }

        @Override
        protected void onPostExecute(String courses) {
            TextView resultTextView = (TextView) findViewById(R.id.tvResult);
            resultTextView.setText(courses);
        }
    }
}*/
