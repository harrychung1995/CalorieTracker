package com.example.calorietracker;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    TextView tvRegister;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvRegister = (TextView) findViewById(R.id.tv_register);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                if (username.isEmpty()) {
                    etUsername.setError("Username is required!");
                    return;
                }
                String password = etPassword.getText().toString();
                if (password.isEmpty()) {
                    etPassword.setError("Password is required!");
                    return;
                }
                RestAsyncTask restAsyncTask = new RestAsyncTask();
                restAsyncTask.execute();
            }
        });
    }

    private class RestAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String passwordhash = getHashResult(password);
            String result = RestClient.findByUsernameAndPasswordToIdentifyUser(username, passwordhash);
            while (result == null) {}

            if (!result.equals("false")) {
                Intent i = new Intent(LoginActivity.this,
                        MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", result);
                i.putExtras(bundle);
                startActivity(i);
            }
            else {
                return "Error!";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String details) {
            if (details != null) {
                etUsername.setError("Username or password is incorrect!");
                etPassword.setError("Username or password is incorrect!");
            }
        }
    }

    //Password hashing
    private String getHashResult(String inputStr)
    {
        BigInteger bigInteger=null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] inputData = inputStr.getBytes();
            md.update(inputData);
            bigInteger = new BigInteger(md.digest());
        } catch (Exception e) {e.printStackTrace();}
        return bigInteger.toString(16);
    }
}