package com.chaoweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//天气 JSON 数据总格式实体类
public class Weather {
    public String code = "200";
    public Basic basic;
    public AQI aqi;
    public Indices indices;
    public Forecast forecast;
}
