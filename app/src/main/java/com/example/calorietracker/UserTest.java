package com.example.calorietracker;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class UserTest  extends Fragment {
    private View vUserTest;
    UserDatabase db = null;
    EditText editText = null;
    TextView textView_insert = null;
    TextView textView_read = null;
    TextView textView_delete = null;
    TextView textView_update = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vUserTest = inflater.inflate(R.layout.user_test, container, false);
        super.onCreate(savedInstanceState);

        db = Room.databaseBuilder(getActivity().getApplicationContext(), UserDatabase.class, "UserDatabase").fallbackToDestructiveMigration().build();
        Button addButton = (Button) vUserTest.findViewById(R.id.addButton);
        editText = (EditText) vUserTest.findViewById(R.id.editText);
        textView_insert = (TextView) vUserTest.findViewById(R.id.textView);
        Button readButton = (Button) vUserTest.findViewById(R.id.readButton);
        textView_read = (TextView) vUserTest.findViewById(R.id.textView_read);
        Button deleteButton = (Button) vUserTest.findViewById(R.id.deleteButton);
        textView_delete = (TextView) vUserTest.findViewById(R.id.textView_delete);
        Button updateButton = (Button) vUserTest.findViewById(R.id.updateButton);
        textView_update = (TextView) vUserTest.findViewById(R.id.textView_update);
        Button alarmButton = (Button) vUserTest.findViewById(R.id.alarmManagerbt);
        TextView textView_alarm = (TextView) vUserTest.findViewById(R.id.textView_alarm);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InsertDatabase addDatabase = new InsertDatabase();
                addDatabase.execute();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DeleteDatabase deleteDatabase = new DeleteDatabase();
                deleteDatabase.execute();
            }
        });
        readButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ReadDatabase readDatabase = new ReadDatabase();
                readDatabase.execute();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UpdateDatabase updateDatabase = new UpdateDatabase();
                updateDatabase.execute();
            }
        });
        alarmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar calendar= Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                PendingIntent pendingIntent = PendingIntent.getService(getActivity().getApplicationContext(), 0,
                        new Intent(getActivity(), ReportPostingIntentService.class),PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                textView_alarm.setText("Initiated");
            }
        });
        return vUserTest;
    }

    private class InsertDatabase extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            if (!(editText.getText().toString().isEmpty())) {
                String[] details = editText.getText().toString().split(" ");
                if (details.length == 4) {
                    User user = new User(4,Integer.parseInt(details[0]),Integer.parseInt(details[1]),Integer.parseInt(details[2]),Integer.parseInt(details[3]));
                    long id = db.userDao().insert(user);
                    return (id + " " + details[0] + " " + details[1] + " " + details[2] + " " + details[3]);
                } else
                    return "";
            } else
                return "";
        }

        @Override
        protected void onPostExecute(String details) {
            textView_insert.setText("Added Record: " + details);
        }
    }



    private class ReadDatabase extends AsyncTask < Void, Void, String > {
        @Override
        protected String doInBackground(Void...params) {
            List< User > users = db.userDao().getAll();
            if (!(users.isEmpty() || users == null)) {
                String allUsers = "";
                for (User temp: users) {
                    String userstr = (temp.getId() + " " + temp.getSet_cal_goal() + " " + temp.getTotal_cal_consumed() + " " + temp.getTotal_steps() + " " + temp.getTotal_cal_burned() +" , ");
                    allUsers = allUsers + userstr;
                }
                return allUsers;
            }
            else
                return "";
        }
        @Override
        protected void onPostExecute(String details) {
            textView_read.setText("All data: " + details);
        }
    }

    private class DeleteDatabase extends AsyncTask < Void, Void, Void > {
        @Override
        protected Void doInBackground(Void...params) {
            db.userDao().deleteAll();
            return null;
        }
        protected void onPostExecute(Void param) {
            textView_delete.setText("All data was deleted");
        }
    }
    private class UpdateDatabase extends AsyncTask < Void, Void, String > {
        @Override protected String doInBackground(Void...params) {
            User user = null;
            String[] details = editText.getText().toString().split(" ");
            if (details.length == 5) {
                int id = Integer.parseInt(details[0]);
                user = db.userDao().findById(id);
                user.setSet_cal_goal(Integer.parseInt(details[1]));
                user.setTotal_cal_consumed(Integer.parseInt(details[2]));
                user.setTotal_steps(Integer.parseInt(details[3]));
                user.setTotal_cal_burned(Integer.parseInt(details[4]));
            }
            if (user != null) {
                db.userDao().updateUsers(user);
                return (details[0] + " " + details[1] + " " + details[2] + " " + details[3]);
            }
            return "";
        }
        @Override
        protected void onPostExecute(String details) {
            textView_update.setText("Updated details: " + details);
        }
    }
}
