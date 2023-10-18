package me.ngodat0103.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        share_PreFerences_Editor = sharedPreferences.edit();
        Thread connection_server_client_Thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    client_connection_module.client_connection_module_init("10.0.2.2",2509);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        connection_server_client_Thread.start();
        String refresh_token = sharedPreferences.getString("refresh_token","null");
        Log.d("authentication","refresh_token: "+refresh_token);
        if (!refresh_token.equals("null"))
            authentication_usingtoken(refresh_token);
        setContentView(R.layout.activity_main);

    }

    private void authentication_usingtoken(String refresh_token){
        Thread authentication_usingtoken_Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map response_from_server;
                try {
                  response_from_server = handle_request_types_module.authentication("null","null",refresh_token);
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new RuntimeException(e);
                }
                ui_Handle.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("authentication",response_from_server.toString());
                        if (response_from_server.get("status").toString().equals("success")) {
                            Intent dashboard_Intent = new Intent(MainActivity.this, DashboardActivity.class);
                            dashboard_Intent.putExtra("refresh_token",response_from_server.get("refresh_token").toString());
                            startActivity(dashboard_Intent);
                        }
                    }
                });
            }
        });
        authentication_usingtoken_Thread.start();
    }
    public void login_Button(View view) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeyException {
        EditText username_EditText = findViewById(R.id.username);
        EditText password_EditText = findViewById(R.id.password);
        Thread authentication_thread = new Thread(new Runnable() {
            Map response_from_server_Map;
            public void run() {
                try {
                    response_from_server_Map = handle_request_types_module.authentication(username_EditText.getText().toString(),password_EditText.getText().toString(),"null");
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new RuntimeException(e);
                }
                ui_Handle.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("authentication",response_from_server_Map.toString());
                        String status_login_string = response_from_server_Map.get("status").toString();
                        if(status_login_string.equals("success")) {
                            share_PreFerences_Editor.putString("refresh_token", response_from_server_Map.get("refresh_token").toString());
                            share_PreFerences_Editor.apply();
                            String refresh_token = sharedPreferences.getString("refresh_token", "NULL");
                            Intent dashboard_Intent = new Intent(MainActivity.this, DashboardActivity.class);
                            dashboard_Intent.putExtra("refresh_token", response_from_server_Map.get("refresh_token").toString());
                            startActivity(dashboard_Intent);
                        }

                    }
                });
            }
        });
    authentication_thread.start();

    }
}