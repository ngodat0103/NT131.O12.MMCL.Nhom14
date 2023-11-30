package com.uit.sensordht.Model;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("time")
    public long time;
    @SerializedName("temperature")
    public int temperature;
    @SerializedName("humidity")
    public int humidity;
}
