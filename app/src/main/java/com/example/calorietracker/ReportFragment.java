package com.example.calorietracker;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static android.content.Context.ALARM_SERVICE;

public class ReportFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    View vReport;
    int remainingCal, calConsumed, calBurned, setTextFlag;
    List<DateReport> reportList = new ArrayList<DateReport>();
    private TextView dateTextView, startDateTextView, endDateTextView;
    PieChart pieChart;
    BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vReport = inflater.inflate(R.layout.fragment_report, container, false);
        dateTextView = (TextView) vReport.findViewById(R.id.dp_date);
        startDateTextView = (TextView) vReport.findViewById(R.id.dp_start_date);
        endDateTextView = (TextView) vReport.findViewById(R.id.dp_end_date);
        pieChart = vReport.findViewById(R.id.pieChart);
        barChart = vReport.findViewById(R.id.barChart);
        Button alarmButton = vReport.findViewById(R.id.alarmManagerbt);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar calendar= Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                PendingIntent pendingIntent = PendingIntent.getService(getActivity().getApplicationContext(), 0,
                        new Intent(getActivity(), ReportPostingIntentService.class),PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                alarmButton.setText("Initiated");
            }
        });
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextFlag = 1;
                showDatePickerDialog();
            }
        });
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextFlag = 2;
                showDatePickerDialog();
            }
        });
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextFlag = 3;
                showDatePickerDialog();
            }
        });
        return vReport;
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
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
        switch (setTextFlag) {
            case 1:
                dateTextView.setText(date);
                ReturnDateReport returnDateReport = new ReturnDateReport();
                returnDateReport.execute(dateTextView.getText().toString(), String.valueOf(MainActivity.getUserId()));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                addPieChartDataSet();
                break;
            case 2:
                startDateTextView.setText(date);
                if (endDateTextView.equals("Select a date"))
                    endDateTextView.setError("End date is required.");
                else {
                    reportList.clear();
                    addBarChartDataSet(startDateTextView.getText().toString(),
                            endDateTextView.getText().toString());}
                break;
            case 3:
                endDateTextView.setText(date);
                if (startDateTextView.equals("Select a date"))
                    startDateTextView.setError("Start date is required.");
                else {
                    reportList.clear();
                    addBarChartDataSet(startDateTextView.getText().toString(),
                            endDateTextView.getText().toString());}
                break;
        }
    }

    private void addPieChartDataSet() {
        if (remainingCal < 0)
            remainingCal = remainingCal * -1;
        float pieTotal = calConsumed + calBurned + remainingCal;
        float[] yData = {calConsumed / pieTotal, calBurned / pieTotal, remainingCal / pieTotal};
        String[] xData = {"Consumed", "Burned", "Remaining"};

        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            entries.add(new PieEntry(yData[i], xData[i]));
        }

        PieDataSet set = new PieDataSet(entries, "Fitness Report");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(196, 219, 25));
        colors.add(Color.rgb(164, 11, 8));
        colors.add(Color.rgb(8, 162, 164));
        set.setColors(colors);

        PieData data = new PieData(set);
        data.setValueTextSize(14f);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Fitness Report");
        pieChart.setCenterTextSize(20);
        pieChart.setDrawEntryLabels(true);
        pieChart.invalidate(); // refresh
    }


    private void addBarChartDataSet(String startDate, String endDate) {
        ReturnPeriodReport returnPeriodReport = new ReturnPeriodReport();
        returnPeriodReport.execute(startDate, endDate, String.valueOf(MainActivity.getUserId()));
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        String[] dates = new String[reportList.size()];
        for (int i = 0; i < reportList.size(); i++) {
            barEntries1.add(new BarEntry(i + 1, Integer.valueOf(reportList.get(i).getTotalCalConsumed())));
            barEntries2.add(new BarEntry(i + 1, Integer.valueOf(reportList.get(i).getTotalCalBurned())));
            dates[i] = reportList.get(i).getDate().substring(4, 10);
        }
        BarDataSet barDataSet1 = new BarDataSet(barEntries1, "Consumed");
        barDataSet1.setColors(Color.parseColor("#9C27B0"));
        BarDataSet barDataSet2 = new BarDataSet(barEntries2, "Burned");
        barDataSet1.setColors(Color.parseColor("#e241f4"));
        BarData data = new BarData(barDataSet1, barDataSet2);
        barChart.setData(data);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        barChart.getAxisLeft().setAxisMinimum(0);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(0);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularityEnabled(true);
        float barSpace = 0.02f;
        float groupSpace = 0.06f;
        int groupCount = dates.length;
        xAxis.setLabelCount(groupCount, false);
        //IMPORTANT *****
        data.setBarWidth(0.15f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
        barChart.setFitBars(true);
    }

    private class ReturnDateReport extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String dateReport = RestClient.returnDateReport(params[0], params[1]);
            try {
                JSONObject jsonObject = new JSONObject(dateReport);
                remainingCal = Integer.valueOf(jsonObject.getString("totalCalRemained"));
                calConsumed = Integer.valueOf(jsonObject.getString("totalCalConsumed"));
                calBurned = Integer.valueOf(jsonObject.getString("totalCalBurned"));
            } catch (final JSONException e) {
                Log.e(TAG, "JSON parsing error: " + e.getMessage());
                remainingCal = 0;
                calConsumed = 0;
                calBurned = 0;
            }
            return null;
        }
    }

    private class ReturnPeriodReport extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String periodReport = RestClient.returnPeriodReport(params[0], params[1], params[2]);
            String jsonPeriodReport = "{\"" + "report" + "\":" + periodReport + "}";
            try {
                JSONObject jsonObject = new JSONObject(jsonPeriodReport);
                JSONArray jsonDateReport = jsonObject.getJSONArray("report");
                for (int i = 0; i < jsonDateReport.length(); i++) {
                    JSONObject r = jsonDateReport.getJSONObject(i);
                    String date = r.getString("date");
                    String totalCalConsumed = r.getString("totalCalConsumed");
                    String totalCalBurned = r.getString("totalCalBurned");
                    DateReport dateReport = new DateReport(date, totalCalConsumed, totalCalBurned);
                    reportList.add(dateReport);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "JSON parsing error: " + e.getMessage());
            }
            return null;
        }
    }
}