package me.ngodat0103.myapplication;

import static java.lang.Thread.interrupted;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DashboardActivity extends AppCompatActivity {
ImageView image_profile_ImageView;
Handler ui_Handler = new Handler();
TextView temp_textview ;
Thread background_Thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        image_profile_ImageView = findViewById(R.id.image_profile_ImageView);
        temp_textview = findViewById(R.id.temp_textview);
        Intent self_Intent = getIntent();
        String refresh_token = self_Intent.getStringExtra("refresh_token");
        background_Thread = new Thread(new Runnable() {
            byte[] image_profile_bytes;
            String temp;
          //  Map weather_data_Map;
            @Override
            public void run() {
                try {
                    image_profile_bytes = handle_request_types_module.load_profile_image(refresh_token);
                    //   weather_data_Map = handle_request_types_module.get_weather();
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new RuntimeException(e);
                }
                ui_Handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap image_Bitmap = BitmapFactory.decodeByteArray(image_profile_bytes, 0, image_profile_bytes.length);
                        image_profile_ImageView.setImageBitmap(image_Bitmap);
                        temp_textview.setVisibility(View.VISIBLE);
                        //   Log.d("weather", weather_data_Map.toString());
                    }
                });
                while (true) {
                    try {
                        if (interrupted()) {
                            return;
                        }
                        Thread.sleep(300);
                        temp = handle_request_types_module.get_temp();
                    } catch (NoSuchPaddingException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalBlockSizeException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    } catch (BadPaddingException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidKeyException e) {
                        break;
                    } catch (InvalidAlgorithmParameterException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        break;
                    }
                    ui_Handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String text = "temp: " + temp;
                            Log.d("temp",temp);
                            temp_textview.setText(text);
                        }
                    });

                }
            }
        });
        background_Thread.start();
    }

    @Override
    protected void onDestroy() {
        background_Thread.interrupt();
        super.onDestroy();
    }
}