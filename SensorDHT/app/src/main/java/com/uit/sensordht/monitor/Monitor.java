package com.uit.sensordht.monitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.uit.sensordht.Model.GlobalVars;
import com.uit.sensordht.R;
import com.uit.sensordht.TemperatureDialogFragment;

import java.io.IOException;
import java.util.Map;

public class Monitor extends AppCompatActivity {
    Thread esp_Thread;
    TextView esp,ras;

    @Override
    protected void onStart() {
        esp_Thread.start();
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);
        esp = findViewById(R.id.esp_value);
        ras  = findViewById(R.id.ras_value);


    }
}
