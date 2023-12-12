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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.uit.sensordht.API.APIManager;
import com.uit.sensordht.Interface.HistoryWeatherCallback;
import com.uit.sensordht.Model.GlobalVars;
import com.uit.sensordht.Model.ItemHistoryWeather;
import com.uit.sensordht.Model.XAxisTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HourTemperatureFragment extends DialogFragment {
    Button btTimePicker;
    long previousTimeDay;
    GraphView graphView;
    long current;
    long nextTimeDay;
    private Map<String, JsonArray> dataChart = new HashMap<>();

    long temp;
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
        btTimePicker = v.findViewById(R.id.btTimePicker1);
        graphView = v.findViewById(R.id.lineChartTemperature);

    }

    private void InitEvent() {
        Calendar timeDefault = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String currentTime = dateFormat.format(timeDefault.getTime());
        current = timeDefault.getTimeInMillis();
        loadTemperatureData(current);
    }
    private List<Pair<Long, Float>> fnSetTime(Map<String, JsonArray> array) {
        List<Pair<Long, Float>> dataList = new ArrayList<>();

        for (Map.Entry<String, JsonArray> entry : array.entrySet()) {
            JsonArray jsonArray = entry.getValue();
            if (jsonArray != null && jsonArray.size() > 0) {
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                long timestamp = jsonObject.get("time").getAsLong();
                float value = jsonObject.get("temperature").getAsFloat();
                dataList.add(new Pair<>(timestamp, value));
            }
        }

        return dataList;
    }


    private void loadTemperatureData(long timeCurrent) {
        long temp = timeCurrent / 1000 - 3600;
        final int numberOfRequests = 10;
        previousTimeDay = temp;
        for (int i = 0; i < numberOfRequests; i++) {
            final int index = i;
            long timestamp = temp + (i * 300);
            nextTimeDay = timestamp;
            APIManager.fnGetHistoryWeather(timestamp, new HistoryWeatherCallback() {
                @Override
                public void onSuccess(JsonArray data) {
                    dataChart.put(String.valueOf(index), data);

                    if (dataChart.size() == numberOfRequests) {
                        setupChart();
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Xử lý lỗi ở đây nếu cần thiết
                }
            });
        }
    }
    private void setupChart()
    {
        List<Pair<Long, Float>> dataList = fnSetTime(dataChart);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        if (dataList != null && !dataList.isEmpty()) {
            int dataSize = dataList.size();
            for (int i = 0; i < dataSize; i++) {
                Pair<Long, Float> pair = dataList.get(i);
                series.appendData(new DataPoint(pair.first, pair.second), true, dataSize);
            }
        } else {
            System.out.println("dataList is empty or null.");
        }
        // Customize the series appearance
        series.setDrawDataPoints(true);
        series.setColor(Color.RED);
        series.setDataPointsRadius(10f);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Date date = new Date((long) dataPoint.getX());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss", Locale.ENGLISH);
                String formattedDate = simpleDateFormat.format(date);
                Toast.makeText(requireContext(), "Time: " + formattedDate + " / Y: " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });
//        series.setSize(5);
        graphView.removeAllSeries();
        graphView.addSeries(series);
        graphView.setCursorMode(true);
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left);
        animation.setDuration(1000);
        graphView.setAnimation(animation);

        graphView.getGridLabelRenderer().setNumHorizontalLabels(10);
        graphView.getGridLabelRenderer().setNumVerticalLabels(10);
        GridLabelRenderer gridLabelRenderer = graphView.getGridLabelRenderer();
        gridLabelRenderer.setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    long unixTimestamp = (long) value * 1000;
                    Date date = new Date(unixTimestamp);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    return simpleDateFormat.format(date);
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        if (!dataList.isEmpty()) {
            long minX = previousTimeDay;
            long maxX = nextTimeDay;
            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMinX(minX);
            graphView.getViewport().setMaxX(maxX);

        }
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(20);
        graphView.getViewport().setMaxY(36);
//        graphView.getViewport().setDrawBorder(true);
        graphView.getViewport().setScalable(true);
        graphView.invalidate();
        gridLabelRenderer.setTextSize(15);
    }
}
