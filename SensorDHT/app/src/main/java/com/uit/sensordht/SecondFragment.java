package com.uit.sensordht;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.uit.sensordht.API.APIManager;
import com.uit.sensordht.Interface.CurrentWeatherCallback;
import com.uit.sensordht.Interface.DialogListener;
import com.uit.sensordht.Model.GlobalVars;
import com.uit.sensordht.Model.ItemWeather;
import com.uit.sensordht.Model.Weather;
import com.uit.sensordht.Model.XAxisTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SecondFragment extends DialogFragment {
    LineChart lineChart;
    int setValue = 0;
    private LineData data;
    private XAxis xAxis;
    private YAxis yAxisLeft, yAxisRight;

    Timer timer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitViews(view);
        InitEvent();
        LiveChartData();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second_temperature, container, false);
    }
    private void InitViews(View v)
    {
        lineChart = v.findViewById(R.id.lineChartTemperature);
    }
    private void InitEvent() {

    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Real-time Data");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawCircles(false);
        set.setLineWidth(4f);
        set.setColor(R.drawable.bg_temperature);
        set.setDrawValues(true);
        set.setDrawCircles(true);
        return set;
    }
    private void setupChart() {
        data = new LineData();
        lineChart.setData(data);
        //Y-Axis
        yAxisRight = lineChart.getAxisRight();
        yAxisLeft = lineChart.getAxisLeft();
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(20f);
        yAxisLeft.setAxisMaximum(40f);

        //X-axis
        xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.RED);
//        xAxis.setDrawAxisLine(true);
//        xAxis.setDrawGridLines(true);
//        xAxis.setDrawLabels(true);
//        xAxis.setDrawGridLinesBehindData(true);
        xAxis.setLabelCount(9, false);
        xAxis.setValueFormatter(new XAxisTimeFormatter());

    }
    private void LiveChartData()
    {
        setupChart();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() ->{
                    APIManager.fnGetCurrentWeather(new CurrentWeatherCallback() {
                        @Override
                        public void onSuccess(Weather weather, ItemWeather temperatureData, ItemWeather humidityData) {
                            float newValue = temperatureData.current;
                            LineDataSet dataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                            if (dataSet == null) {
                                dataSet = createSet();
                                data.addDataSet(dataSet);
                            }
                            xAxis.setAxisMaximum(GlobalVars.currentTime + setValue);
                            data.addEntry(new Entry(dataSet.getEntryCount(), newValue), 0);
                            if(setValue > 9)
                            {
                                lineChart.moveViewToX(setValue - 9);
                            }

                                data.notifyDataChanged();
                                xAxis.setDrawGridLinesBehindData(true);

                                setValue++;
                                lineChart.notifyDataSetChanged();
                                lineChart.fitScreen();
                                lineChart.setVisibleXRangeMaximum(10);
                                lineChart.invalidate();
                        }

                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });


                });
            }
        }, 0, 1000);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}

