package com.uit.sensordht;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.uit.sensordht.API.APIManager;
import com.uit.sensordht.Interface.CurrentWeatherCallback;
import com.uit.sensordht.Interface.DialogListener;
import com.uit.sensordht.Model.ItemWeather;
import com.uit.sensordht.Model.Weather;

import java.util.Timer;
import java.util.TimerTask;

public class SecondFragment extends DialogFragment {
    LineChart lineChart;
    int setValue = 1;
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
        set.setLineWidth(2f);
        set.setColor(Color.BLUE);
        set.setDrawValues(false);
//        set.enableDashedLine(1,1,1);

        return set;
    }
    private void LiveChartData()
    {
        LineData data = new LineData();
        lineChart.setData(data);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(9f);
        xAxis.setLabelCount(10, true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() ->{
                    APIManager.fnGetCurrentWeather(new CurrentWeatherCallback() {
                        @Override
                        public void onSuccess(Weather weather, ItemWeather temperatureData, ItemWeather humidityData) {
                            float newValue = temperatureData.current;
                            setValue++;
                            if(setValue > 9)
                                xAxis.setAxisMaximum(setValue);
                            LineDataSet dataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                            if (dataSet == null) {
                                dataSet = createSet();
                                data.addDataSet(dataSet);
                            }
                            data.addEntry(new Entry(dataSet.getEntryCount(), newValue), 0);

                            // Notify chart data has changed
                            data.notifyDataChanged();
                            lineChart.notifyDataSetChanged();
                            lineChart.setVisibleXRangeMaximum(10); // Display only last 10 entries
                            lineChart.fitScreen();
                            lineChart.moveViewToX(data.getEntryCount()); // Scroll to the latest entry
                            lineChart.invalidate();

                        }

                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });

                    // Add new data point to the chart
//                    float newValue = APIClient.temperature; /* fetch new data */

                });
            }
        }, 1000, 1000);
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

