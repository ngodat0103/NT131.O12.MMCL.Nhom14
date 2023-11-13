package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {
    Button search_btn;
    public static final String[] languages = {"Choose Language", "English", "Vietnamese"};
    Spinner spinner;
    EditText username_edt;
    Handler ui_handle = new Handler();
    WebView forgot_password_Webview;


    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        username_edt = findViewById(R.id.edt_username);




        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                if (selectedLang.equals("English")) {
                    setLocal(ForgotPassword.this, "en");
                    finish();
                    startActivity(getIntent());
                } else if (selectedLang.equals("Vietnamese")) {
                    setLocal(ForgotPassword.this,"hi");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        search_btn = findViewById(R.id.btn_search);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username_edt.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Enter your username",Toast.LENGTH_SHORT).show();
                }
                else {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("username_primary",username_edt.getText().toString());


                    Map<String,String> headers = new HashMap<>();
                    headers.put("check-valid-otp","false");
                    headers.put("projects","nhung");
                    headers.put("change-password","false");


                    Thread forgot_password_thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String,String> response  = new HashMap<>();
                            try {
                                CustomRequest forgot_password_request = new CustomRequest(
                                        "https://server.uitprojects.com/reset_password",
                                        "POST",
                                        headers,
                                        parameters
                                );
                                response= forgot_password_request.sendRequest();
                                if (Objects.equals(response.get("status"), "otp_sent")){
                                    ui_handle.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            setContentView(R.layout.otp_layout);
                                            Toast.makeText(getApplicationContext(), "Enter a OTP code sent to your email", Toast.LENGTH_SHORT).show();
                                            EditText otp_edt = findViewById(R.id.edt_otp_code);
                                            Button verify_btn = findViewById(R.id.btn_verify);
                                            headers.put("check-valid-otp","true");
                                            verify_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    parameters.put("otp_code",otp_edt.getText().toString());
                                                    Thread check_otp_Thread = new Thread(new Runnable() {
                                                        Map<String,String> response;
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                CustomRequest check_valid_otp = new CustomRequest(
                                                                        "https://server.uitprojects.com/reset_password",
                                                                        "POST",
                                                                        headers,
                                                                        parameters
                                                                );
                                                                response = check_valid_otp.sendRequest();
                                                                if (Objects.equals(response.get("status"), "valid")){
                                                                    ui_handle.post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            setContentView(R.layout.new_password_layout);
                                                                            Toast.makeText(getApplicationContext(),"Enter your new password",Toast.LENGTH_SHORT).show();
                                                                            Button change_password_btn = findViewById(R.id.btn_change_password);
                                                                            EditText new_password = findViewById(R.id.edt_new_password);
                                                                            EditText new_confirm_password = findViewById(R.id.edt_confirm_new_password);
                                                                            change_password_btn.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    if (new_password.getText().toString().equals(new_confirm_password.getText().toString())) {
                                                                                        Map<String, String> parameters = new HashMap<>();
                                                                                        Map<String, String> headers = new HashMap<>();
                                                                                        headers.put("projects", "nhung");
                                                                                        headers.put("change-password", "true");
                                                                                        parameters.put("username_primary", username_edt.getText().toString());
                                                                                        parameters.put("new_password", new_password.getText().toString());
                                                                                        Thread change_password_Thread = new Thread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                try {
                                                                                                    CustomRequest change_password = new CustomRequest(
                                                                                                            "https://server.uitprojects.com/reset_password",
                                                                                                            "POST",
                                                                                                            headers,
                                                                                                            parameters
                                                                                                    );
                                                                                                    response =  change_password.sendRequest();
                                                                                                    if(Objects.equals(response.get("status"), "success"))
                                                                                                        ui_handle.post(new Runnable() {
                                                                                                            @Override
                                                                                                            public void run() {
                                                                                                                Toast.makeText(getApplicationContext(),"Change password successfully",Toast.LENGTH_SHORT).show();
                                                                                                                finish();
                                                                                                            }
                                                                                                        });
                                                                                                } catch (
                                                                                                        IOException e) {
                                                                                                    throw new RuntimeException(e);
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                        change_password_Thread.start();
                                                                                    }
                                                                                    else
                                                                                        Toast.makeText(getApplicationContext(),"password don't match",Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                    });

                                                                }
                                                                else{
                                                                    Toast.makeText(getApplicationContext(), response.get("reason"),Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            } catch (IOException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        }
                                                    });
                                                    check_otp_Thread.start();
                                                }
                                            });
                                        }
                                    });
                                }

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    forgot_password_thread.start();
                }
            }
        });
    }
}