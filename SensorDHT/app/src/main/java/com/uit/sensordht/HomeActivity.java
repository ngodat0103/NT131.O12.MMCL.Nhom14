package com.uit.sensordht;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);
// Start the animation
        animationView.playAnimation();
// Pause the animation
        animationView.pauseAnimation();
// Set the speed of the animation
        animationView.setSpeed(1.5f);
    }
}