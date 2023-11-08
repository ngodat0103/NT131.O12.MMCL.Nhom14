package me.ngodat0103.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor share_PreFerences_Editor;
    Handler ui_Handle = new Handler();
    Button register_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
       // share_PreFerences_Editor = sharedPreferences.edit();
        register_btn = findViewById(R.id.btn_register);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getApplicationContext(),Register.class);
                startActivity(register);

            }
        });

    }



    public void login_Button(View view) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        EditText username_EditText = findViewById(R.id.username);
        EditText password_EditText = findViewById(R.id.password);
        Thread authentication_thread = new Thread(new Runnable() {
            Map<String,String> response_json;
            public void run() {
                try {
                    response_json =  Api_request.authentication(username_EditText.getText().toString(),password_EditText.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (Objects.equals(response_json.get("status"),"success"))
                {
                    Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                    dashboard.putExtra("refresh_token",response_json.get("refresh_token"));
                    dashboard.putExtra("image_profile",response_json.get("image_profile"));
                    startActivity(dashboard);
                }
                else {
                    ui_Handle.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "username or password invalid", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            });
    authentication_thread.start();
    }
}