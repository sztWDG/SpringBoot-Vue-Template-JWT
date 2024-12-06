package com.example.service;

import com.example.entity.vo.request.WeatherVO;

public interface WeatherService {
    //通过所在城市的经纬度来确定天气信息
    WeatherVO fetchWeather(double longitude, double latitude);
}
