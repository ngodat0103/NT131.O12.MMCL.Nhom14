package com.uit.sensordht;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.uit.sensordht.API.APIManager;
import com.uit.sensordht.Interface.CurrentWeatherCallback;
import com.uit.sensordht.Model.Weather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity {
    TextView tvHumidity, tvTemperature, tvTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvTime = findViewById(R.id.tvCurrentTime);
        APIManager.fnGetCurrentWeather(new CurrentWeatherCallback() {
            @Override
            public void onSuccess(Weather weather) {
                long timestamp = (weather.time + 3600 * 7) * 1000;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
//                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int month = calendar.get(Calendar.MONTH) + 1; // Tháng trong Calendar bắt đầu từ 0
                int year = calendar.get(Calendar.YEAR);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                int day = calendar.get(Calendar.DATE);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss", Locale.ENGLISH);
                String formattedDateTime = sdf.format(calendar.getTime());
                Log.d("DateTime", "Day of week: " + dayOfWeek);
                Log.d("DateTime", "Month: " + month);
                Log.d("DateTime", "Year: " + year);
                Log.d("DateTime", "Hour: " + hour);
                Log.d("DateTime", "Minute: " + minute);
                Log.d("DateTime", "Second: " + second);
                Log.d("DateTime", "Formatted date time: " + formattedDateTime);
                String humidity = String.join(" ", String.valueOf(weather.humidity), getResources().getString(R.string.percent));
                String temperature = String.join(" ", String.valueOf(weather.temperature), getResources().getString(R.string.celsius));
                tvHumidity.setText(humidity);
                tvTemperature.setText(temperature);
                tvTime.setText(formattedDateTime);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}