package com.uit.sensordht.monitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.uit.sensordht.Model.GlobalVars;
import com.uit.sensordht.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Monitor extends AppCompatActivity {
    TextView esp,ras;
    Thread background_Thread ;
    Button reboot_ras,reboot_esp;


    @Override
    protected void onStart() {
        background_Thread.start();
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);
        esp = findViewById(R.id.esp_value);
        ras  = findViewById(R.id.ras_value);
        reboot_esp = findViewById(R.id.btn_esp_reboot);
        reboot_esp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {

                    final Map<String,String> parameter = new HashMap<>();
                    @Override
                    public void run() {
                        try {
                            parameter.put("refresh_token",GlobalVars.refresh_token);
                            parameter.put("reboot","true");
                            parameter.put("device_name","esp8266");
                            CustomRequest request = new CustomRequest(
                                    "https://server.uitprojects.com/device/set-setting",
                                    "POST",
                                    null,
                                    parameter
                                    );

                            Map<String,String> response = request.sendRequest();
                            int stop = 0 ;

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                thread.start();
            }
        });







        reboot_ras = findViewById(R.id.btn_ras_reboot);
        reboot_ras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {

                    final Map<String,String> parameter = new HashMap<>();
                    @Override
                    public void run() {
                        try {
                            parameter.put("refresh_token",GlobalVars.refresh_token);
                            parameter.put("reboot","true");
                            parameter.put("device_name","ras");
                            CustomRequest request = new CustomRequest(
                                    "https://server.uitprojects.com/device/set-setting",
                                    "POST",
                                    null,
                                    parameter
                            );

                            Map<String,String> response = request.sendRequest();
                            int stop = 0 ;

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                thread.start();
            }
        });







       background_Thread =new Thread(new Runnable() {
           URL url = null;
           List<Device> list_device = new ArrayList<>();
            InputStream reader;
            int count;

           @Override
           public void run() {
               try {
                   url = new URL("https://server.uitprojects.com/device/monitor");
                   HttpURLConnection con = (HttpURLConnection) url.openConnection();
                   con.setRequestMethod("GET");
                   con.setReadTimeout(120000);
                   int status = con.getResponseCode();
                   reader = con.getInputStream();
                   BufferedReader char_reader = new BufferedReader(new InputStreamReader(reader));

                   String[] device_name_list = char_reader.readLine().split(",");

                   count = device_name_list.length;

                   for (int i = 0; i < device_name_list.length; i++)
                       list_device.add(new Device(device_name_list[i]));
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }

               while (true){
                   try {

                       for (int i = 0; i < count; i++) {
                           byte[] buffer = new byte[4];
                           reader.read(buffer);
                           list_device.get(i).is_online = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
                       }

                       for (int i = 0; i < count; i++) {

                           new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                               @SuppressLint("SetTextI18n")
                               @Override
                               public void run() {
                                   if(list_device.get(0).is_online==1){
                                       esp.setText("Online");
                                   }
                                   else
                                       esp.setText("Offline");

                                   if(list_device.get(1).is_online==1)
                                       ras.setText("Online");
                                   else
                                       ras.setText("Offline");

                               }
                           }, 500);

                       }
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }


               }
           }
       });



    }
}
