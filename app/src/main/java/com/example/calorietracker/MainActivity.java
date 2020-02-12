package com.example.calorietracker;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static int userId;
    private AlarmManager alarmMgr;
    UserDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userId = Integer.valueOf(bundle.getString("id"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle("Calorie Tracker");
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
        db = Room.databaseBuilder(getApplicationContext(), UserDatabase.class, "UserDatabase").fallbackToDestructiveMigration().build();
        InsertCurrentUserToDatabase insertCurrentUserToDatabase = new InsertCurrentUserToDatabase();
        insertCurrentUserToDatabase.execute();

        //Alarm Manager
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                new Intent(this, ReportPostingIntentService.class),PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment nextFragment = null;
        switch (id) {
            case R.id.nav_home:
                nextFragment = new MainFragment();
                break;
            case R.id.nav_my_daily_diet:
                nextFragment = new MyDaileyDietFragment();
                break;
            case R.id.nav_steps:
                nextFragment = new StepsFragment();
                break;
            case R.id.nav_calorie_tracker:
                nextFragment = new CalorieTrackerFragment();
                break;
            case R.id.nav_report:
                nextFragment = new ReportFragment();
                break;
            case R.id.nav_map:
                nextFragment = new AppMapFragment();
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, nextFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static int getUserId() {
        return userId;
    }

    private class InsertCurrentUserToDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if ((db.userDao().findById(userId) == null)) {
                User user = new User(userId, 0, 0, 0, 0);
                db.userDao().insert(user);
            }
            return null;
        }
    }
}
