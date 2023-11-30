package com.uit.sensordht.Interface;

import com.uit.sensordht.Model.Weather;

public interface CurrentWeatherCallback {
    void onSuccess(Weather weather);
    void onFailure(String errorMessage);
}
