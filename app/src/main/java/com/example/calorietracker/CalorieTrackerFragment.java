package com.example.calorietracker;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalorieTrackerFragment extends Fragment {
    View vCalorieTracker;
    UserDatabase db = null;
    int setCalGoal;
    int stepsTaken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vCalorieTracker = inflater.inflate(R.layout.fragment_calorie_tracker, container, false);

        db = Room.databaseBuilder(getActivity().getApplicationContext(), UserDatabase.class, "UserDatabase").fallbackToDestructiveMigration().build();

        GetCalGoal getCalGoal = new GetCalGoal();
        getCalGoal.execute();
        GetStepsTaken getStepsTaken = new GetStepsTaken();
        getStepsTaken.execute();
        GetCalConsumed getCalConsumed = new GetCalConsumed();
        getCalConsumed.execute();
        GetCalBurned getCalBurned = new GetCalBurned();
        getCalBurned.execute();

        return vCalorieTracker;
    }

    private class GetCalGoal extends AsyncTask< Void, Void, String > {
        @Override protected String doInBackground(Void...params) {
            User user = db.userDao().findById(MainActivity.getUserId());
            setCalGoal = user.getSet_cal_goal();
            return  String.valueOf(setCalGoal);
        }

        @Override
        protected void onPostExecute(String details) {
            TextView textViewCalGoal = (TextView) vCalorieTracker.findViewById(R.id.num_cal_goal);
            textViewCalGoal.setText(details);
        }
    }

    private class GetStepsTaken extends AsyncTask< Void, Void, String > {
        @Override protected String doInBackground(Void...params) {
            User user = db.userDao().findById(MainActivity.getUserId());
            stepsTaken = user.getTotal_steps();
            return  String.valueOf(stepsTaken);
        }

        @Override
        protected void onPostExecute(String details) {
            TextView textViewSteps = (TextView) vCalorieTracker.findViewById(R.id.num_steps);
            textViewSteps.setText(details);
        }
    }

    private class GetCalConsumed extends AsyncTask< Void, Void, String > {
        @Override
        protected String doInBackground(Void...params) {
            //Get current data in YYYY-MM-DD format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            String totalCalConsumed = RestClient.findByUserIdAndDateToFetchTotalCaloriesConsumed(sdf.format(calendar.getTime()), String.valueOf(MainActivity.getUserId()));
            int roundValue = (int) Math.round(Double.valueOf(totalCalConsumed));
            return  String.valueOf(roundValue);
        }

        @Override
        protected void onPostExecute(String details) {
            TextView textViewCalConsumed = (TextView) vCalorieTracker.findViewById(R.id.num_cal_consumed);
            textViewCalConsumed.setText(details);
        }
    }

    private class GetCalBurned extends AsyncTask< Void, Void, String > {
        @Override
        protected String doInBackground(Void...params) {
            String str_caloriesBurnedAtRest = RestClient.findByUserIdToFetchTotalCaloriesBurnedAtRest(String.valueOf(MainActivity.getUserId()));
            String str_calBurnedPerStep = RestClient.findByUserIdToFetchCalBurnedPerStep(String.valueOf(MainActivity.getUserId()));
            double calBurnedAtRest = Double.valueOf(str_caloriesBurnedAtRest);
            double calBurnedPerStep = Double.valueOf(str_calBurnedPerStep);
            double totalBurned = calBurnedAtRest + calBurnedPerStep * stepsTaken;
            int roundValue = (int) Math.round(totalBurned);
            return  String.valueOf(roundValue);
        }

        @Override
        protected void onPostExecute(String details) {
            TextView textViewCalBurned = (TextView) vCalorieTracker.findViewById(R.id.num_cal_burned);
            textViewCalBurned.setText(details);
        }
    }
}
