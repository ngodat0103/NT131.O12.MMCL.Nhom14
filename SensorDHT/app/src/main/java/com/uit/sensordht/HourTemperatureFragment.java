package com.uit.sensordht;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.uit.sensordht.Model.XAxisTimeFormatter;

import java.util.Calendar;
import java.util.Locale;

public class HourTemperatureFragment extends DialogFragment {
    Button btTimePicker;
    LineChart lineChart;
    LineData data;
    XAxis xAxis;
    YAxis yAxisRight, yAxisLeft;

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
        btTimePicker.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            btTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                        }
                    },
                    hour,
                    minute,
                    true // set true if you want 24-hour time, false for AM/PM
            );

            timePickerDialog.show(); // Show the TimePickerDialog
        });

    }
    private void LiveChartDate(long timestamp)
    {
        long timestampDesc = timestamp;
        setupChart();
        LineDataSet dataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
        if (dataSet == null) {
            dataSet = createSet();
            data.addDataSet(dataSet);
        }
        for(int i = 0; i < 10; i++)
        {
            timestampDesc -= 300;
        }
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
        xAxis.setLabelCount(10, false);
        xAxis.setValueFormatter(new XAxisTimeFormatter());

    }
}
