package com.example.calorietracker;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.calorietracker.StepsData.Step;
import com.example.calorietracker.StepsData.StepDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepsFragment extends Fragment implements View.OnClickListener {
    View vSteps;
    private int userId;
    private TextView textViewSteps;
    private TextView tvFeedback;
    private EditText insertSteps;
    private Button bSubmit;
    UserDatabase dbUser = null;
    StepDatabase dbStep = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vSteps = inflater.inflate(R.layout.fragment_steps, container, false);
        dbStep = Room.databaseBuilder(getActivity().getApplicationContext(), StepDatabase.class, "StepDatabase").fallbackToDestructiveMigration().build();
        dbUser = Room.databaseBuilder(getActivity().getApplicationContext(), UserDatabase.class, "UserDatabase").fallbackToDestructiveMigration().build();
        ReadUserDatabase readUserDatabase = new ReadUserDatabase();
        readUserDatabase.execute();
        StepListView stepListView = new StepListView();
        stepListView.execute();
        textViewSteps = (TextView) vSteps.findViewById(R.id.user_steps);
        insertSteps = (EditText) vSteps.findViewById(R.id.insertSteps);
        bSubmit = (Button) vSteps.findViewById(R.id.b_submit);
        tvFeedback = (TextView) vSteps.findViewById(R.id.tv_feedback);
        bSubmit.setOnClickListener(this);
        return vSteps;
    }

    @Override
    public void onClick(View v) {
        String newSteps = insertSteps.getText().toString();
        if (newSteps.isEmpty()) {
            insertSteps.setError("Steps is required!");
            return;
        }
        UpdateDatabase updateDatabase = new UpdateDatabase();
        updateDatabase.execute();
        StepListView stepListView = new StepListView();
        stepListView.execute();
        String feedback = "New Steps has been updated.";
        tvFeedback.setText(feedback);
    }


    private class ReadUserDatabase extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            userId = MainActivity.getUserId();
            User user = dbUser.userDao().findById(userId);
            int steps = user.getTotal_steps();
            return String.valueOf(steps);
        }

        @Override
        protected void onPostExecute(String details) {
            textViewSteps.setText(details);
        }
    }

    private class UpdateDatabase extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            User user = dbUser.userDao().findById(userId);
            String newSteps = insertSteps.getText().toString();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
            String currentTime = String.valueOf(sdf.format(calendar.getTime()));
            Step stepTransaction = new Step(userId, currentTime, Integer.parseInt(newSteps));
            dbStep.stepDao().insert(stepTransaction);

            int steps = user.getTotal_steps();
            steps = steps + Integer.parseInt(newSteps);
            user.setTotal_steps(steps);
            dbUser.userDao().updateUsers(user);
            return String.valueOf(steps);
        }

        @Override
        protected void onPostExecute(String details) {
            textViewSteps.setText(details);
        }
    }


    private class StepListView extends AsyncTask<Void, Void, SimpleAdapter> {
        @Override
        protected SimpleAdapter doInBackground(Void... params) {
            List<Step> stepTransaction = dbStep.stepDao().findById(userId);
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

            for (int i = 0; i < stepTransaction.size(); i++) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("Time", stepTransaction.get(i).getTime());
                item.put("Steps", stepTransaction.get(i).getSteps());
                items.add(item);}

            SimpleAdapter adapter = new SimpleAdapter(
                    getActivity(),
                    items,
                    R.layout.list_view_step,
                    new String[]{"Time", "Steps"},
                    new int[]{R.id.list_view_time, R.id.list_view_stpes}
            );
            return adapter;
        }

        @Override
        protected void onPostExecute(SimpleAdapter adapter) {
            ListView stepListView = vSteps.findViewById(R.id.steps_listView);
            stepListView.setAdapter(adapter);
        }
    }

}
