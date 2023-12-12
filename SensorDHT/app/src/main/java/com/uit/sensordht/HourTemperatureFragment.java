package com.uit.sensordht;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.uit.sensordht.API.APIManager;
import com.uit.sensordht.Interface.HistoryWeatherCallback;
import com.uit.sensordht.Model.GlobalVars;
import com.uit.sensordht.Model.XAxisTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HourTemperatureFragment extends DialogFragment {
    Button btTimePicker;
    long previousTimeDay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hour_temperature, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitViews(view);
        InitEvent();

    }
    private void InitViews(View v)
    {
        btTimePicker = v.findViewById(R.id.btTimePicker);
    }
    private void InitEvent() {
        APIManager.fnGetHistoryWeather(GlobalVars.currentTime, new HistoryWeatherCallback() {
            @Override
            public void onSuccess(long timestamp, float temperature, float humidity) {
                Log.d("API CALL", String.valueOf(temperature));
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
    private List<Pair<Long, Float>> fnSetTime(long timeCurrent) {
        List<Pair<Long, Float>> dataList = new ArrayList<>();
        Date date = new Date(timeCurrent);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, -1);
        previousTimeDay = calendar.getTimeInMillis() / 1000;

        for (int i = 0; i < 10; i++) {
            APIManager.fnGetHistoryWeather(previousTimeDay, new HistoryWeatherCallback() {
                @Override
                public void onSuccess(long timestamp, float temperature, float humidity) {
                    dataList.add(new Pair<>(timestamp, temperature));
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
            previousTimeDay += 300;
        }
        Log.d("Temperature", dataList.get(1).toString());
        return dataList;
    }
}
