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
    TextView signup_txtview;
    TextView forgot_password_edt;
    Handler ui_handle = new Handler();

    @Override
    protected void onRestart() {
        recreate();
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username_edt = findViewById(R.id.edt_username);
        password_edt = findViewById(R.id.edt_password);
        forgot_password_edt = findViewById(R.id.txtview_forgot_password);


        signup_txtview = findViewById(R.id.txtview_sign_up);
        signup_txtview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getApplicationContext(),Register.class);
                startActivity(register);
            }
        });




        forgot_password_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgot_password = new Intent(getApplicationContext(),ForgotPassword.class);
                startActivity(forgot_password);
            }
        });
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
                                        Intent dashboard = new Intent(getApplicationContext(),Dashboard.class);
                                        dashboard.putExtra("image_profile",response.get("image_profile"));
                                        dashboard.putExtra("refresh_token",response.get("refresh_token"));
                                        startActivity(dashboard);
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



        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                if(selectedLang.equals("English")){
                    setLocal(Login.this, "en");
                    finish();
                    startActivity(getIntent());
                }else if(selectedLang.equals("Vietnamese")){
                    setLocal(Login.this, "hi");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());
    }


}