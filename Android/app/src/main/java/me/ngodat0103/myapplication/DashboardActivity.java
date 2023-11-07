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
import java.util.Base64;
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
        String image_profile_String = self_Intent.getStringExtra("image_profile");
        byte[] image_profile_bytes = image_profile_String.getBytes();
        image_profile_bytes = Base64.getDecoder().decode(image_profile_bytes);
        Bitmap image_bitmap = BitmapFactory.decodeByteArray(image_profile_bytes,0,image_profile_bytes.length);
        image_profile_ImageView.setImageBitmap(image_bitmap);
    }

}