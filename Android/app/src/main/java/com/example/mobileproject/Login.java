package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Login extends AppCompatActivity {
    Spinner spinner;
    public static final String[] languages = {"Choose Language", "English", "Vietnamese"};
    EditText username_edt;
    EditText password_edt;
    Button login_btn,back_btn;
    Handler ui_handle = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username_edt = findViewById(R.id.edt_username);
        password_edt = findViewById(R.id.edt_password);
        back_btn = findViewById(R.id.btn_back);
        login_btn = findViewById(R.id.btn_login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> parameters = new HashMap<>();
                parameters.put("username_primary",username_edt.getText().toString());
                parameters.put("password",password_edt.getText().toString());
                parameters.put("device_name",Build.MODEL);
                Thread authenticate_Thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String,String>  response;

                        try {
                            CustomRequest authenticate = new CustomRequest(
                                    "https://server.uitprojects.com/authentication",
                                    "POST",
                                    null,
                                    parameters
                            );
                            response = authenticate.sendRequest();
                            if (Objects.equals(response.get("status"), "success"))
                                ui_handle.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Login OK", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            else
                                ui_handle.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                authenticate_Thread.start();

            }
        });

    }
}