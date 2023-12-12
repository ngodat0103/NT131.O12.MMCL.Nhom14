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


        esp_Thread = new Thread(new Runnable() {
            CustomRequest request;
            Map<String,String> response;
            @Override
            public void run() {
                while(true){
                    try {

                        request = new CustomRequest(
                                "https://server.uitprojects.com/device/info?device_name=esp8266",
                                "GET",
                                null,
                                null
                        );
                        response = request.sendRequest();
                        GlobalVars.is_esp_online = Boolean.parseBoolean(response.getOrDefault("online","null"));

                        request = new CustomRequest(
                                "https://server.uitprojects.com/device/info?device_name=ras",
                                "GET",
                                null,
                                null
                        );
                        response = request.sendRequest();
                        GlobalVars.is_ras_online = Boolean.parseBoolean(response.getOrDefault("online","null"));


                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                if (GlobalVars.is_esp_online)
                                    esp.setText("Online");
                                else
                                    esp.setText("Offline");

                                if (GlobalVars.is_ras_online)
                                    ras.setText("Online");
                                else
                                    ras.setText("Offline");
                            }
                        }, 1000);






                        Thread.sleep(1000);
                        int stop = 0 ;


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });




    }
}
