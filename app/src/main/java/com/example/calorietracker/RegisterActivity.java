package com.example.calorietracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener  {

    EditText firstName, lastName, username, password, email, address, postcode, height, weight, stpes_per_mile;
    TextView dob;
    RadioButton gender_M, gender_F;
    RadioGroup gender;
    Spinner level_of_activity;
    Button btn_register;
    String selectedGender, userId;
    int errorNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firstName = (EditText) findViewById(R.id.et_first_name);
        lastName = (EditText) findViewById(R.id.et_last_name);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_reg_password);
        email = (EditText) findViewById(R.id.et_reg_email);
        address = (EditText) findViewById(R.id.et_reg_address);
        postcode = (EditText) findViewById(R.id.et_reg_postcode);
        dob = (TextView) findViewById(R.id.dp_dob);
        height = (EditText) findViewById(R.id.et_height);
        weight = (EditText) findViewById(R.id.et_weight);
        stpes_per_mile = (EditText) findViewById(R.id.et_stpes_per_mile);
        gender = (RadioGroup) findViewById(R.id.rg_gender);
        gender_M = (RadioButton) findViewById(R.id.rb_gender_M);
        gender_F = (RadioButton) findViewById(R.id.rb_gender_F);
        level_of_activity = (Spinner) findViewById(R.id.spinner_LOA);
        List<String> level_of_activityList = new ArrayList<>();
        level_of_activityList.add("1");
        level_of_activityList.add("2");
        level_of_activityList.add("3");
        level_of_activityList.add("4");
        level_of_activityList.add("5");
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext()
                , android.R.layout.simple_spinner_item, level_of_activityList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level_of_activity.setAdapter(spinnerAdapter);
        btn_register = (Button) findViewById(R.id.btn_register);


        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorNumber = 0;
                if (firstName.getText().toString().isEmpty()) {
                    firstName.setError("First name is required!");
                    errorNumber++;
                }
                if (lastName.getText().toString().isEmpty()) {
                    lastName.setError("Last name is required!");
                    errorNumber++;
                }
                if (username.getText().toString().isEmpty()) {
                    username.setError("Username is required!");
                    errorNumber++;
                } else {
                    FindExistenceByUsername findExistenceByUsername = new FindExistenceByUsername();
                    findExistenceByUsername.execute(username.getText().toString());
                }
                if (password.getText().toString().isEmpty()) {
                    password.setError("Password is required!");
                    errorNumber++;
                }
                if (email.getText().toString().isEmpty()) {
                    email.setError("Email is required!");
                    errorNumber++;
                } else {
                    FindExistenceByEmail findExistenceByEmail = new FindExistenceByEmail();
                    findExistenceByEmail.execute(email.getText().toString());
                }
                if (address.getText().toString().isEmpty()) {
                    address.setError("Email is required!");
                    errorNumber++;
                }
                if (postcode.getText().toString().isEmpty()) {
                    postcode.setError("Postcode is required!");
                    errorNumber++;
                }
                RadioButton selectedGenderButton = findViewById(gender.getCheckedRadioButtonId());
                if (selectedGenderButton == null) {
                    gender_M.setError("Select gender");
                    gender_F.setError("Select gender");
                    errorNumber++;
                }
                if (dob.getText().toString().isEmpty()) {
                    dob.setError("Date of birth is required!");
                    errorNumber++;
                }

                if (height.getText().toString().isEmpty()) {
                    height.setError("Height is required!");
                    errorNumber++;
                } else {
                    if (height.getText().toString().charAt(0) == '0') {
                        height.setError("Invalid height!");
                        errorNumber++;
                    }
                }

                if (weight.getText().toString().isEmpty()) {
                    weight.setError("Weight is required!");
                    errorNumber++;
                } else {
                    if (weight.getText().toString().charAt(0) == '0') {
                        weight.setError("Invalid weight!");
                        errorNumber++;
                    }
                }
                if (stpes_per_mile.getText().toString().isEmpty()) {
                    stpes_per_mile.setError("Steps per mile is required!");
                    errorNumber++;
                } else {
                    if (stpes_per_mile.getText().toString().charAt(0) == '0') {
                        stpes_per_mile.setError("Invalid stpes per mile!");
                        errorNumber++;
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                if (errorNumber != 0)
                    return;

                userId = String.valueOf(getId());
                if (selectedGenderButton.getText().toString().equals("Male")) {
                    selectedGender = "M";
                } else {
                    selectedGender = "F";
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                String signUpDate = sdf.format(calendar.getTime());
                RegisterActivity.RegisterUser registerUser = new RegisterActivity.RegisterUser();
                registerUser.execute(firstName.getText().toString(),
                        lastName.getText().toString(),
                        email.getText().toString(),
                        dob.getText().toString(),
                        height.getText().toString(),
                        weight.getText().toString(),
                        selectedGender,
                        address.getText().toString(),
                        postcode.getText().toString(),
                        level_of_activity.getSelectedItem().toString(),
                        stpes_per_mile.getText().toString());

                RegisterCredential registerCredential = new RegisterCredential();
                registerCredential.execute(username.getText().toString(),
                        password.getText().toString(),
                        signUpDate);
            }
        });
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int monthIndex, int dayOfMonth) {
        int month = monthIndex +1;
        String strMonth = String.valueOf(month);
        String strDay = String.valueOf(dayOfMonth);
        if (month < 10) {
            strMonth = "0" + month;
        }
        if (dayOfMonth < 10) {
            strDay = "0" + dayOfMonth;
        }
        String date = year + "-" + strMonth + "-" + strDay;
        dob.setText(date);
    }

    private class FindExistenceByUsername extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String returnString = RestClient.findUserByUsername(params[0]);
            if (!returnString.equals("[]")) {
                return "This username has been used!";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String details) {
            if (details != null) {
                username.setError(details);
                errorNumber++;
            }
        }
    }

    private class FindExistenceByEmail extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String returnString = RestClient.findUserByEmail(params[0]);
            if (!returnString.equals("[]")) {
                return "You have already registered!";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String details) {
            if (details != null) {
                email.setError(details);
                errorNumber++;
            }
        }
    }

    private class RegisterUser extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String jsonUserString = "{\"address\":\"" + params[7] + "\",\"dob\":\"" + params[3] + "T00:00:00+11:00\",\"email\":\""
                    + params[2] + "\",\"gender\":\"" + params[6] + "\",\"height\":" + Double.valueOf(params[4]) + ",\"levelOfActivity\":\"" + params[9]
                    + "\",\"name\":\"" + params[0] + "\",\"postcode\":\"" + params[8] + "\",\"stepsPerMile\":" + params[10] + ",\"surname\":\""
                    + params[1] + "\",\"userId\":\"" + userId + "\",\"weight\":" + Double.valueOf(params[5]) + "}";
            RestClient.createAppUser(jsonUserString);
            return null;
        }
    }

    private class RegisterCredential extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String jsonAppUser = RestClient.findByUserId(userId);
            while (jsonAppUser == null) {
            }
            String passwordHash = getHashResult(params[1]);
            String jsonCredentialString = "{\"passwordhash\":\"" + passwordHash + "\",\"signUpDate\":\""
                    + params[2] + "T01:00:00+11:00\",\"userId\":" + jsonAppUser + ",\"username\":\""
                    + params[0] + "\"}";
            RestClient.createCredential(jsonCredentialString);

            //Log in directly
            Intent i = new Intent(RegisterActivity.this,
                    MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", userId);
            i.putExtras(bundle);
            startActivity(i);
            return null;
        }
    }

    //ID Creator
    private static int inc = 0;

    private static long getId() {
        long id = Long.parseLong(String.valueOf(System.currentTimeMillis())
                .substring(1, 9)
                .concat(String.valueOf(inc)));
        inc = (inc + 1) % 10;
        return id;
    }

    //Password hashing
    private String getHashResult(String inputStr) {
        BigInteger bigInteger = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] inputData = inputStr.getBytes();
            md.update(inputData);
            bigInteger = new BigInteger(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bigInteger.toString(16);
    }

}
