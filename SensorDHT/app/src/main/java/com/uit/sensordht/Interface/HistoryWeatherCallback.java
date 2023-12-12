package com.uit.sensordht.Interface;

public interface HistoryWeatherCallback {
    void onSuccess(long timestamp, float temperature, float humidity);
    void onFailure(String errorMessage);
}
