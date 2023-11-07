package me.ngodat0103.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    Button register_btn;
    Handler ui_handle = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register_btn = findViewById(R.id.btn_register_push);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = findViewById(R.id.edt_username);
                EditText password = findViewById(R.id.edt_password);
                EditText email = findViewById(R.id.edt_email);
                Thread register_Thread = new Thread(new Runnable() {
                    Map<String,String> response_map;
                    @Override
                    public void run() {
                        try {
                           response_map = Api_request.register(
                                    username.getText().toString(),
                                    password.getText().toString(),
                                    email.getText().toString()
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                            ui_handle.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (Objects.equals(response_map.get("create_account"),"failed_because_username_exist"))
                                        Toast.makeText(getApplicationContext(),"username exsists",Toast.LENGTH_SHORT).show();
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"create new account successful",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });

                    }
                });
                register_Thread.start();
            }
        });
    }
}