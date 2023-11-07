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
import android.widget.EditText;
import android.widget.ImageView;

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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor share_PreFerences_Editor;
    Handler ui_Handle = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
       // share_PreFerences_Editor = sharedPreferences.edit();


    }
    public void login_Button(View view) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        EditText username_EditText = findViewById(R.id.username);
        EditText password_EditText = findViewById(R.id.password);
        Log.d("authentication","has called");
        Thread authentication_thread = new Thread(new Runnable() {
            URL login_url;
            HttpURLConnection con;
            Map<String, String> parameters = new HashMap<>();
            DataOutputStream out;
            public void run() {

                try {
                    login_url = new URL("http://10.0.2.2/authentication");
                    parameters.put("username_primary",username_EditText.getText().toString());
                    parameters.put("password",password_EditText.getText().toString());
                    parameters.put("device_name",Build.MODEL);
                    con = (HttpURLConnection) login_url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    con.setDoOutput(true);
                    StringBuilder result = new StringBuilder();
                    out = new DataOutputStream(con.getOutputStream());
                    for(Map.Entry<String,String> entry: parameters.entrySet()) {
                        try {
                            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        result.append("=");
                        try {
                            result.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        result.append("&");
                    }
                    String resultString = result.toString();
                    resultString = resultString.substring(0,resultString.length()-1);
                    out.writeBytes(resultString);
                    out.flush();
                    out.close();


                    int status = con.getResponseCode();
                    if (status==200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuffer content = new StringBuffer();
                        String read_line;
                        while ((read_line = in.readLine()) != null) {
                            content.append(read_line);
                        }
                        ui_Handle.post(new Runnable() {
                            @Override
                            public void run() {

                                Log.d("authentication:", String.valueOf(content.toString()));
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (ProtocolException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            });
    authentication_thread.start();
    }
}