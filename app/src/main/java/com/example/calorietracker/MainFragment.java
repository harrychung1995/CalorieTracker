package com.example.calorietracker;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainFragment extends Fragment implements View.OnClickListener {
    private View vMain;
    private TextView textViewDate;
    UserDatabase db = null;
    private TextView textViewCalGoal;
    private TextView tvFeedback;
    private EditText insertCalGoal;
    private Button bSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMain = inflater.inflate(R.layout.fragment_main, container, false);

        //Welcome User with first name
        GetUserFirstName getUserFirstName = new GetUserFirstName();
        getUserFirstName.execute();

        //Calender
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        String currentDate = String.valueOf(sdf.format(calendar.getTime()));
        textViewDate = vMain.findViewById(R.id.fragment_main_date);
        textViewDate.setText(currentDate);

        //Read Database
        db = Room.databaseBuilder(getActivity().getApplicationContext(), UserDatabase.class, "UserDatabase").fallbackToDestructiveMigration().build();
        ReadDatabase readDatabase = new ReadDatabase();
        readDatabase.execute();

        textViewCalGoal = (TextView) vMain.findViewById(R.id.fragment_main_cal_goal);
        insertCalGoal = (EditText) vMain.findViewById(R.id.fragment_insert_cal_goal);
        bSubmit = (Button) vMain.findViewById(R.id.b_submit);
        tvFeedback = (TextView) vMain.findViewById(R.id.tv_feedback);
        bSubmit.setOnClickListener(this);
        return vMain;
    }

    @Override
    public void onClick(View v) {
        String newCalGoal = insertCalGoal.getText().toString();
        if (newCalGoal.isEmpty()) {
            insertCalGoal.setError("Steps is required!");
            return;
        }
        UpdateDatabase updateDatabase = new UpdateDatabase();
        updateDatabase.execute();
        String feedback = "New Calories goal has been updated.";
        tvFeedback.setText(feedback);
    }

    private class ReadDatabase extends AsyncTask< Void, Void, String > {
        @Override
        protected String doInBackground(Void...params) {

            int userId = MainActivity.getUserId();
            User user = db.userDao().findById(userId);
            int calGoal = user.getSet_cal_goal();
            return  String.valueOf(calGoal);
        }
        @Override
        protected void onPostExecute(String details) {
            textViewCalGoal.setText(details);
        }
    }

    private class UpdateDatabase extends AsyncTask < Void, Void, String > {
        @Override protected String doInBackground(Void...params) {
            int userId = MainActivity.getUserId();
            User user = db.userDao().findById(userId);
            String newCalGoal = insertCalGoal.getText().toString();
            user.setSet_cal_goal(Integer.parseInt(newCalGoal));
            db.userDao().updateUsers(user);
            return newCalGoal;
        }

        @Override
        protected void onPostExecute(String details) {
            textViewCalGoal.setText(details);
        }
    }

    private class GetUserFirstName extends AsyncTask< Void, Void, String > {
        @Override
        protected String doInBackground(Void...params) {

            String firstName = RestClient.findByUserIdToFetchFirstName(String.valueOf(MainActivity.getUserId()));
            return  firstName;
        }

        @Override
        protected void onPostExecute(String details) {
            TextView firstName = (TextView) vMain.findViewById(R.id.user_first_name);
            firstName.setText(details + "!");
        }
    }
}