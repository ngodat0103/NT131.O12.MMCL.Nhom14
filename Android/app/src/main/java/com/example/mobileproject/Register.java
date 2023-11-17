package com.example.mobileproject;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    Spinner spinner;
    EditText username_edt,password_edt,email_edt,confirm_password_edt;

    public static final String[] languages = {"Choose Language", "English", "Vietnamese"};
    Button signup_button;
    Handler ui_handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        spinner = findViewById(R.id.spinner);
        username_edt = findViewById(R.id.edt_username);
        password_edt = findViewById(R.id.edt_password);
        email_edt = findViewById(R.id.edt_email);
        confirm_password_edt = findViewById(R.id.edt_confirmpassword);







        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                if(selectedLang.equals("English")){
                    setLocal(Register.this, "en");
                    finish();
                    startActivity(getIntent());
                }else if (selectedLang.equals("Vietnamese")){
                    setLocal(Register.this, "hi");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        signup_button = findViewById(R.id.btn_signup);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = username_edt.getText().toString();
                String password  = password_edt.getText().toString();
                String email = email_edt.getText().toString();
                String confirm_password = confirm_password_edt.getText().toString();

                if (username == null || password == null || email ==null || confirm_password == null){
                    Toast.makeText(getApplicationContext(),"Please fill enough information",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Objects.equals(password,confirm_password)){
                    Toast.makeText(getApplicationContext(),"Password don't match",Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String,String> parameters = new HashMap<>();
                parameters.put("username_primary",username);
                parameters.put("password",password);
                parameters.put("email",email);

                Thread register_Thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CustomRequest register_request = new CustomRequest(
                                    "https://server.uitprojects.com/registration",
                                    "POST",
                                    null,
                                    parameters
                            );
                            Map<String,String> response = register_request.sendRequest();
                            if(Objects.equals(response.get("status"), "successful")){
                                ui_handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"create account successfully",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                            }
                            else{
                                ui_handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(Objects.requireNonNull(response.get("reason")).contains("1062"))
                                            Toast.makeText(getApplicationContext(),"Username existed",Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(getApplicationContext(), response.get("reason"),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
                register_Thread.start();
            }
        });

    }

    public void setLocal(Activity activity, String langcode){
        Locale locale = new Locale(langcode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

    }
}