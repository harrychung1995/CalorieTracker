package com.example.calorietracker;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;


import com.example.calorietracker.StepsData.StepDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportPostingIntentService extends IntentService {
    String setCalGoal;
    String stepsTaken;
    String calConsumed;
    String calBurned;
    UserDatabase dbUser = null;
    StepDatabase dbStep = null;
    List<User> listUser;
    String userId;

    public ReportPostingIntentService() {
        super("ReportPostingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dbUser = Room.databaseBuilder(getApplicationContext(), UserDatabase.class, "UserDatabase").fallbackToDestructiveMigration().build();
        dbStep = Room.databaseBuilder(getApplicationContext(), StepDatabase.class, "StepDatabase").fallbackToDestructiveMigration().build();
        FindAllDBUser findAllDBUser = new FindAllDBUser();
        findAllDBUser.execute();
        while (listUser == null) {}
        for (int index = 0; listUser.size() > index; index++) {
            userId = String.valueOf(listUser.get(index).getId());
            GetStepsTaken getStepsTaken = new GetStepsTaken();
            getStepsTaken.execute();
            GetCalGoal getCalGoal = new GetCalGoal();
            getCalGoal.execute();
            GetCalConsumed getCalConsumed = new GetCalConsumed();
            getCalConsumed.execute();
            GetCalBurned getCalBurned = new GetCalBurned();
            getCalBurned.execute();
            ReportPosing reportPosing = new ReportPosing();
            while (setCalGoal == null || calBurned ==null || calConsumed == null || stepsTaken == null) {}
            reportPosing.execute(setCalGoal, calBurned, calConsumed, stepsTaken);
        }
        Log.i("message   ", "The number of users:  " + listUser.size() + " has been uploaded");
        ClearDatabase clearDatabase = new ClearDatabase();
        clearDatabase.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private class ReportPosing extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String appUser = RestClient.findByUserId(userId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            String date = sdf.format(calendar.getTime());
            String jsonString = "{\"appUser\":" + appUser + ",\"reportPK\":{\"date\":\"" + date
                    + "T00:00:00+10:00\",\"userId\":\"" + userId + "\"},\"setCalGoal\":"
                    + params[0] + ",\"totalCalBurned\":" + params[1] + ",\"totalCalConsumed\":"
                    + params[2] + ",\"totalStepsTaken\":" + params[3] + "}";
            RestClient.createReport(jsonString);
            return null;
        }
    }

    private class FindAllDBUser extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            listUser = dbUser.userDao().getAll();
            return null;
        }
    }

    private class GetCalGoal extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            User user = dbUser.userDao().findById(Integer.valueOf(userId));
            setCalGoal = String.valueOf(user.getSet_cal_goal());
            return null;
        }
    }

    private class GetCalConsumed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //Get current data in YYYY-MM-DD format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            String totalCalConsumed = RestClient.findByUserIdAndDateToFetchTotalCaloriesConsumed(sdf.format(calendar.getTime()), userId);
            int roundValue = (int) Math.round(Double.valueOf(totalCalConsumed));
            calConsumed =  String.valueOf(roundValue);
            return null;
        }
    }

    private class GetStepsTaken extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            User user = dbUser.userDao().findById(Integer.valueOf(userId));
            stepsTaken = String.valueOf(user.getTotal_steps());
            return null;
        }
    }

    private class GetCalBurned extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String str_caloriesBurnedAtRest = RestClient.findByUserIdToFetchTotalCaloriesBurnedAtRest(userId);
            String str_calBurnedPerStep = RestClient.findByUserIdToFetchCalBurnedPerStep(userId);
            double calBurnedAtRest = Double.valueOf(str_caloriesBurnedAtRest);
            double calBurnedPerStep = Double.valueOf(str_calBurnedPerStep);
            double totalBurned = calBurnedAtRest + calBurnedPerStep * Integer.valueOf(stepsTaken);
            int roundValue = (int) Math.round(totalBurned);
            calBurned = String.valueOf(roundValue);
            return null;
        }
    }

    private class ClearDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            dbUser.userDao().deleteAll();
            User user = new User(MainActivity.getUserId(), 0, 0, 0, 0);
            dbUser.userDao().insert(user);
            dbStep.stepDao().deleteAll();
            return null;
        }
    }
}
